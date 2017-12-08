//
//  CanvasViewController.swift
//  BonnieDraw
//
//  Created by Professor on 07/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CanvasViewController:
        BackButtonViewController,
        UIPopoverPresentationControllerDelegate,
        JotViewDelegate,
        JotViewStateProxyDelegate,
        CanvasSettingTableViewControllerDelegate,
        SizePickerViewControllerDelegate,
        BrushPickerViewControllerDelegate,
        ColorPickerViewControllerDelegate,
        UIImagePickerControllerDelegate,
        UINavigationControllerDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var canvas: JotView!
    @IBOutlet weak var gridView: GridView!
    @IBOutlet weak var undoButton: UIBarButtonItem!
    @IBOutlet weak var redoButton: UIBarButtonItem!
    @IBOutlet weak var playButton: UIBarButtonItem!
    @IBOutlet weak var uploadButton: UIBarButtonItem!
    @IBOutlet weak var settingButton: UIBarButtonItem!
    @IBOutlet weak var sizeButton: UIBarButtonItem!
    @IBOutlet weak var eraserButton: UIBarButtonItem!
    @IBOutlet weak var brushButton: UIButton!
    @IBOutlet weak var resetButton: UIBarButtonItem!
    @IBOutlet weak var colorButton: UIBarButtonItem!
    private var brush = Brush()
    private var lastPenType: Type?
    private var paths = [Path]()
    private var redoPaths = [Path]()
    private var writeHandle: FileHandle?
    private var persistentBackgroundColor: UIColor?
    var jotViewStateInkPath = FileUrl.INK.path
    var jotViewStateThumbnailPath = FileUrl.THUMBNAIL.path
    var jotViewStatePlistPath = FileUrl.STATE.path

    override func viewDidLoad() {
        brush.isForceSupported = true
        canvas.delegate = self
        brushButton.layer.cornerRadius = view.bounds.width / 10
        let size = CGSize(width: 33, height: 33)
        let count = UserDefaults.standard.integer(forKey: Default.GRID)
        gridView.set(horizontalCount: count, verticalCount: count)
        UIGraphicsBeginImageContextWithOptions(size, false, UIScreen.main.scale)
        UIBezierPath(arcCenter: CGPoint(x: size.width / 2, y: size.height / 2), radius: brush.minSize / 2, startAngle: 0, endAngle: CGFloat.pi * 2, clockwise: true).stroke()
        sizeButton.image = UIGraphicsGetImageFromCurrentImageContext()
        sizeButton.tintColor = UIColor.white
        UIGraphicsGetCurrentContext()?.clear(CGRect(origin: .zero, size: size))
        UIBezierPath(roundedRect: CGRect(origin: .zero, size: size), cornerRadius: 4).fill()
        colorButton.image = UIGraphicsGetImageFromCurrentImageContext()
        colorButton.tintColor = brush.color
        UIGraphicsEndImageContext()
    }

    override func viewDidAppear(_ animated: Bool) {
        if canvas.state == nil {
            canvas.finishInit()
            let stateProxy = JotViewStateProxy(delegate: self)
            stateProxy?.loadJotStateAsynchronously(false, with: canvas.bounds.size, andScale: UIScreen.main.scale, andContext: canvas.context, andBufferManager: JotBufferManager.sharedInstance())
        } else {
            loading.hide(true)
        }
        NotificationCenter.default.addObserver(self, selector: #selector(applicationDidEnterBackground), name: .UIApplicationDidEnterBackground, object: nil)
    }

    override func viewWillDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self)
    }

    @objc func applicationDidEnterBackground(notification: Notification) {
        saveToDraft()
    }

    override func onBackPressed(_ sender: Any) {
        saveToDraft()
        super.onBackPressed(sender)
    }

    @IBAction func undo(_ sender: Any) {
        let path = paths.removeLast()
        redoPaths.append(path)
        if path.points.first?.type == .background {
            var backgroundColor: UIColor? = nil
            for path in paths {
                for point in path.points {
                    if point.type == .background {
                        backgroundColor = point.color
                    }
                }
            }
            gridView.backgroundColor = backgroundColor ?? persistentBackgroundColor ?? .white
        } else {
            canvas.undo()
        }
        checkCanvasStatus()
    }

    @IBAction func redo(_ sender: Any) {
        let path = redoPaths.removeLast()
        paths.append(path)
        if path.points.first?.type == .background {
            gridView.backgroundColor = path.points.first?.color ?? .white
        } else {
            canvas.redo()
        }
        checkCanvasStatus()
    }

    @IBAction func reset(_ sender: Any) {
        presentedViewController?.dismiss(animated: true)
        presentConfirmationDialog(title: "alert_reset_title".localized, message: "alert_reset_content".localized) {
            success in
            if success {
                self.persistentBackgroundColor = nil
                self.gridView.backgroundColor = .white
                self.canvas.clear(true)
                self.paths.removeAll()
                self.redoPaths.removeAll()
                self.writeHandle?.closeFile()
                self.checkCanvasStatus()
                do {
                    let manager = FileManager.default
                    if manager.fileExists(atPath: FileUrl.CACHE.path) {
                        try manager.removeItem(at: FileUrl.CACHE)
                    }
                    if manager.fileExists(atPath: FileUrl.DRAFT.path) {
                        try manager.removeItem(at: FileUrl.DRAFT)
                        UserDefaults.standard.removeObject(forKey: Default.DRAFT_BACKGROUND_COLOR)
                    }
                    manager.createFile(atPath: FileUrl.CACHE.path, contents: nil, attributes: nil)
                    self.writeHandle = try FileHandle(forWritingTo: FileUrl.CACHE)
                } catch {
                    Logger.d("\(#function): \(error.localizedDescription)")
                }
            }
        }
    }

    @IBAction func wrap(_ sender: UIBarButtonItem) {
        loading.hide(false)
        let bounds = canvas.bounds
        let color = gridView.backgroundColor ?? .white
        save(cacheToUrl: FileUrl.RESULT) {
            url in
            if url != nil {
                DispatchQueue.main.async {
                    self.canvas.exportToImage(
                            onComplete: {
                                image in
                                DispatchQueue.main.async {
                                    UIGraphicsBeginImageContextWithOptions(bounds.size, false, UIScreen.main.scale)
                                    color.setFill()
                                    UIBezierPath(rect: bounds).fill()
                                    image?.draw(in: bounds)
                                    let image = UIGraphicsGetImageFromCurrentImageContext()
                                    UIGraphicsEndImageContext()
                                    if let image = image {
                                        self.performSegue(withIdentifier: sender == self.playButton ? Segue.ANIMATION : Segue.UPLOAD, sender: image)
                                    } else {
                                        self.loading.hide(true)
                                    }
                                }
                            },
                            withScale: UIScreen.main.scale)
                }
            } else {
                self.loading.hide(true)
            }
        }
    }

    @IBAction func didSelectEraser(_ sender: UIBarButtonItem) {
        if let lastPenType = lastPenType {
            sender.tintColor = .white
            brush.type = lastPenType
            self.lastPenType = nil
        } else {
            sender.tintColor = .black
            lastPenType = brush.type
            brush.type = .eraser
        }
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        presentedViewController?.dismiss(animated: true)
        if let controller = segue.destination as? CanvasSettingTableViewController {
            controller.delegate = self
            controller.popoverPresentationController?.delegate = self
            controller.popoverPresentationController?.canOverlapSourceViewRect = true
            controller.popoverPresentationController?.backgroundColor = UIColor.black.withAlphaComponent(0.5)
            controller.preferredContentSize = CGSize(width: 60, height: DEBUG ? 300 : 240)
        } else if let controller = segue.destination as? SizePickerViewController {
            controller.delegate = self
            controller.value = Float(brush.minSize)
            controller.popoverPresentationController?.delegate = self
            controller.popoverPresentationController?.canOverlapSourceViewRect = true
            controller.popoverPresentationController?.backgroundColor = UIColor.black.withAlphaComponent(0.5)
            controller.preferredContentSize = CGSize(width: traitCollection.horizontalSizeClass == .compact ? view.bounds.width : view.bounds.width / 2, height: 76)
        } else if let controller = segue.destination as? BrushPickerViewController {
            controller.delegate = self
            controller.stepWidth = Float(brush.stepWidth)
            controller.alpha = Float(brush.maxAlpha)
            controller.type = brush.type
            controller.popoverPresentationController?.delegate = self
            controller.popoverPresentationController?.canOverlapSourceViewRect = true
            controller.popoverPresentationController?.backgroundColor = UIColor.black.withAlphaComponent(0.5)
            controller.preferredContentSize = CGSize(width: traitCollection.horizontalSizeClass == .compact ? view.bounds.width : view.bounds.width / 4, height: DEBUG ? 256 : 208)
        } else if let controller = segue.destination as? ColorPickerViewController {
            controller.delegate = self
            if segue.identifier == Segue.BACKGROUND_COLOR {
                controller.type = .background
                controller.color = gridView.backgroundColor ?? .white
            } else {
                controller.type = .canvas
                controller.color = brush.color
            }
            controller.popoverPresentationController?.delegate = self
            controller.popoverPresentationController?.canOverlapSourceViewRect = true
            controller.popoverPresentationController?.backgroundColor = UIColor.black.withAlphaComponent(0.5)
            controller.preferredContentSize = CGSize(width: UIScreen.main.bounds.width * (traitCollection.horizontalSizeClass == .compact ? 0.9 : 0.45), height: 204)
        } else if let controller = segue.destination as? CanvasAnimationViewController,
                  let image = sender as? UIImage {
            controller.workThumbnail = image
            controller.workFileUrl = FileUrl.RESULT
        } else if let controller = segue.destination as? UploadViewController,
                  let image = sender as? UIImage {
            controller.workThumbnail = image
            controller.workFileUrl = FileUrl.RESULT
        }
    }

    private func checkCanvasStatus() {
        undoButton.isEnabled = !paths.isEmpty
        redoButton.isEnabled = !redoPaths.isEmpty
        playButton.isEnabled = !paths.isEmpty
        uploadButton.isEnabled = !paths.isEmpty
        resetButton.isEnabled = !paths.isEmpty
    }

    internal func adaptivePresentationStyle(for controller: UIPresentationController) -> UIModalPresentationStyle {
        return .none
    }

    internal func canvasSetting(didSelectRowAt indexPath: IndexPath) {
        switch indexPath.row {
        case 0:
            let controller = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
            controller.popoverPresentationController?.barButtonItem = settingButton
            let color = UIColor.gray
            let noGridAction = UIAlertAction(title: "no_grid".localized, style: .default) {
                action in
                self.gridView.set(horizontalCount: 0, verticalCount: 0)
                UserDefaults.standard.set(0, forKey: Default.GRID)
            }
            noGridAction.setValue(color, forKey: "titleTextColor")
            controller.addAction(noGridAction)
            let threeByThreeAction = UIAlertAction(title: "three_by_three_grid".localized, style: .default) {
                action in
                self.gridView.set(horizontalCount: 3, verticalCount: 3)
                UserDefaults.standard.set(3, forKey: Default.GRID)
            }
            threeByThreeAction.setValue(color, forKey: "titleTextColor")
            controller.addAction(threeByThreeAction)
            let sixBySixAction = UIAlertAction(title: "six_by_six_grid".localized, style: .default) {
                action in
                self.gridView.set(horizontalCount: 6, verticalCount: 6)
                UserDefaults.standard.set(6, forKey: Default.GRID)
            }
            sixBySixAction.setValue(color, forKey: "titleTextColor")
            controller.addAction(sixBySixAction)
            let tenByTenAction = UIAlertAction(title: "ten_by_ten_grid".localized, style: .default) {
                action in
                self.gridView.set(horizontalCount: 10, verticalCount: 10)
                UserDefaults.standard.set(10, forKey: Default.GRID)
            }
            tenByTenAction.setValue(color, forKey: "titleTextColor")
            controller.addAction(tenByTenAction)
            let twentyByTwentyAction = UIAlertAction(title: "twenty_by_twenty_grid".localized, style: .default) {
                action in
                self.gridView.set(horizontalCount: 20, verticalCount: 20)
                UserDefaults.standard.set(20, forKey: Default.GRID)
            }
            twentyByTwentyAction.setValue(color, forKey: "titleTextColor")
            controller.addAction(twentyByTwentyAction)
            let cancelAction = UIAlertAction(title: "alert_button_cancel".localized, style: .cancel)
            controller.addAction(cancelAction)
            present(controller, animated: true)
        case 1:
            performSegue(withIdentifier: Segue.BACKGROUND_COLOR, sender: nil)
        case 2:
            checkPhotosPermission(successHandler: {
                let bounds = self.canvas.bounds
                let color = self.gridView.backgroundColor ?? .white
                self.canvas.exportToImage(
                        onComplete: {
                            image in
                            DispatchQueue.main.async {
                                UIGraphicsBeginImageContextWithOptions(bounds.size, false, UIScreen.main.scale)
                                color.setFill()
                                UIBezierPath(rect: bounds).fill()
                                image?.draw(in: bounds)
                                let image = UIGraphicsGetImageFromCurrentImageContext()
                                UIGraphicsEndImageContext()
                                if let image = image {
                                    AppDelegate.save(asset: image)
                                }
                            }
                        },
                        withScale: UIScreen.main.scale)
                let flashView = UIView(frame: self.canvas.frame)
                flashView.backgroundColor = .white
                self.view.addSubview(flashView)
                UIView.animate(
                        withDuration: 1,
                        animations: {
                            flashView.alpha = 0
                        },
                        completion: {
                            finished in
                            flashView.removeFromSuperview()
                        })
            })
        case 3:
            break
        default:
            let controller = UIImagePickerController()
            controller.delegate = self
            controller.sourceType = .photoLibrary
            present(controller, animated: true)
        }
    }

    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String: Any]) {
        picker.dismiss(animated: true) {
            if let image = info[UIImagePickerControllerOriginalImage] as? UIImage, let model = image.cgImage?.colorSpace?.model, model == .rgb {
                self.brush.textureCache = image
                self.brushButton.setImage(UIImage(named: "ic_android_white"), for: .normal)
            } else {
                self.presentDialog(title: "檔案不支援", message: "圖檔請使用 RGB 色彩空間匯出，不可為 Monochrome 或 CMYK 等等")
            }
        }
    }

    internal func sizePicker(didSelect size: CGFloat) {
        brush.minSize = size
        brush.maxSize = size * 1.5
        brush.stepWidth = size * 0.1
        let rect = CGSize(width: 33, height: 33)
        UIGraphicsBeginImageContextWithOptions(rect, false, UIScreen.main.scale)
        UIBezierPath(arcCenter: CGPoint(x: rect.width / 2, y: rect.height / 2), radius: brush.minSize / 2, startAngle: 0, endAngle: CGFloat.pi * 2, clockwise: true).stroke()
        sizeButton.image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
    }

    internal func brushPicker(didSelectStepWidth stepWidth: CGFloat) {
        brush.stepWidth = stepWidth
    }

    internal func brushPicker(didSelectAlpha alpha: CGFloat) {
        brush.minAlpha = alpha - 0.2
        brush.maxAlpha = alpha
    }

    internal func brushPicker(didSelectType type: Type) {
        brush.type = type
        var image: UIImage? = nil
        switch type {
        case .crayon:
            image = UIImage(named: "draw_pen_ic_3")
        case .pencil:
            image = UIImage(named: "draw_pen_ic_2")
        case .pen:
            image = UIImage(named: "draw_pen_ic_1")
        case .airbrush:
            image = UIImage(named: "draw_pen_ic_5")
        case .marker:
            image = UIImage(named: "draw_pen_ic_4")
        default:
            return
        }
        brushButton.setImage(image, for: .normal)
        eraserButton.tintColor = .white
    }

    internal func colorPicker(didSelect color: UIColor, type: ColorPickerViewController.ColorType?) {
        if type == .canvas {
            brush.color = color
            colorButton.tintColor = color
        } else {
            paths.append(Path(points: [Point(length: LENGTH_SIZE,
                    function: .draw,
                    position: .zero,
                    color: color,
                    action: .down,
                    size: 0,
                    type: .background,
                    duration: ANIMATION_TIMER)]))
            gridView.backgroundColor = color
            redoPaths.removeAll()
            saveToCache()
            checkCanvasStatus()
        }
    }

    internal func textureForStroke() -> JotBrushTexture! {
        return brush.texture()
    }

    internal func stepWidthForStroke() -> CGFloat {
        return brush.stepWidthForStroke()
    }

    internal func supportsRotation() -> Bool {
        return brush.isRotationSupported
    }

    internal func width(forCoalescedTouch coalescedTouch: UITouch!, from touch: UITouch!) -> CGFloat {
        return brush.width(forCoalescedTouch: coalescedTouch, fromTouch: touch)
    }

    internal func color(forCoalescedTouch coalescedTouch: UITouch!, from touch: UITouch!) -> UIColor! {
        return brush.color(forCoalescedTouch: coalescedTouch, fromTouch: touch)
    }

    internal func smoothness(forCoalescedTouch coalescedTouch: UITouch!, from touch: UITouch!) -> CGFloat {
        return brush.smoothness(forCoalescedTouch: coalescedTouch, fromTouch: touch)
    }

    internal func willAddElements(_ elements: [Any]!, to stroke: JotStroke!, fromPreviousElement previousElement: AbstractBezierPathElement!) -> [Any]! {
        return elements
    }

    internal func willBeginStroke(withCoalescedTouch coalescedTouch: UITouch!, from touch: UITouch!) -> Bool {
        paths.append(Path(points: [Point(length: LENGTH_SIZE,
                function: .draw,
                position: coalescedTouch.location(in: canvas),
                color: brush.color(forCoalescedTouch: coalescedTouch, fromTouch: touch) ?? .clear,
                action: .down,
                size: brush.width(forCoalescedTouch: coalescedTouch, fromTouch: touch),
                type: brush.type,
                duration: ANIMATION_TIMER)]))
        return true
    }

    internal func willMoveStroke(withCoalescedTouch coalescedTouch: UITouch!, from touch: UITouch!) {
        paths.last?.points.append(Point(length: LENGTH_SIZE,
                function: .draw,
                position: coalescedTouch.location(in: canvas),
                color: brush.color(forCoalescedTouch: coalescedTouch, fromTouch: touch) ?? .clear,
                action: .move,
                size: brush.width(forCoalescedTouch: coalescedTouch, fromTouch: touch),
                type: brush.type,
                duration: ANIMATION_TIMER))
    }

    internal func willEndStroke(withCoalescedTouch coalescedTouch: UITouch!, from touch: UITouch!, shortStrokeEnding: Bool) {
        let position = coalescedTouch.location(in: canvas)
        if canvas.bounds.contains(position) {
            paths.last?.points.append(Point(length: LENGTH_SIZE,
                    function: .draw,
                    position: position,
                    color: brush.color(forCoalescedTouch: coalescedTouch, fromTouch: touch) ?? .clear,
                    action: .up,
                    size: brush.width(forCoalescedTouch: coalescedTouch, fromTouch: touch),
                    type: brush.type,
                    duration: ANIMATION_TIMER))
        } else {
            paths.last?.points.last?.action = .up
        }
    }

    internal func didEndStroke(withCoalescedTouch coalescedTouch: UITouch!, from touch: UITouch!) {
        redoPaths.removeAll()
        saveToCache()
        checkCanvasStatus()
    }

    internal func willCancel(_ stroke: JotStroke!, withCoalescedTouch coalescedTouch: UITouch!, from touch: UITouch!) {
    }

    internal func didCancel(_ stroke: JotStroke!, withCoalescedTouch coalescedTouch: UITouch!, from touch: UITouch!) {
    }

    internal func didLoadState(_ state: JotViewStateProxy!) {
        DispatchQueue.global().async {
            do {
                let manager = FileManager.default
                if manager.fileExists(atPath: FileUrl.CACHE.path) {
                    try manager.removeItem(at: FileUrl.CACHE)
                }
                if FileManager.default.fileExists(atPath: FileUrl.DRAFT.path) {
                    try manager.copyItem(at: FileUrl.DRAFT, to: FileUrl.CACHE)
                    self.writeHandle = try FileHandle(forWritingTo: FileUrl.CACHE)
                    self.writeHandle?.seekToEndOfFile()
                    DispatchQueue.main.async {
                        self.playButton.isEnabled = true
                        self.uploadButton.isEnabled = true
                        self.resetButton.isEnabled = true
                    }
                } else {
                    manager.createFile(atPath: FileUrl.CACHE.path, contents: nil, attributes: nil)
                }
            } catch {
                Logger.d("\(#function): \(error.localizedDescription)")
            }
            DispatchQueue.main.async {
                self.gridView.backgroundColor = UserDefaults.standard.color(forKey: Default.DRAFT_BACKGROUND_COLOR) ?? .white
                self.canvas.loadState(state)
                self.canvas.isHidden = false
                self.loading.hide(true)
                UIView.animate(withDuration: 0.4) {
                    self.canvas.alpha = 1
                }
            }
        }
    }

    internal func didUnloadState(_ state: JotViewStateProxy!) {
    }

    private func saveToCache() {
        var pointsToSave = [Point]()
        while paths.count > canvas.state.undoLimit {
            let path = paths.removeFirst()
            for point in path.points {
                pointsToSave.append(point)
                if point.type == .background {
                    persistentBackgroundColor = point.color
                }
            }
        }
        if !pointsToSave.isEmpty {
            writeHandle?.write(DataConverter.parse(pointsToData: pointsToSave, withScale: (CGFloat(UInt16.max) + 1) / min(canvas.bounds.width, canvas.bounds.height)))
        }
    }

    private func saveToDraft(completionHandler: ((UIImage?) -> Void)? = nil) {
        let color = gridView.backgroundColor
        save(cacheToUrl: FileUrl.DRAFT) {
            url in
            if url != nil {
                self.canvas.exportImage(to: self.jotViewStateInkPath, andThumbnailTo: self.jotViewStateThumbnailPath, andStateTo: self.jotViewStatePlistPath, withThumbnailScale: UIScreen.main.scale) {
                    ink, thumbnail, state in
                    completionHandler?(thumbnail)
                    UserDefaults.standard.set(color: color, forKey: Default.DRAFT_BACKGROUND_COLOR)
                }
            }
        }
    }

    private func save(cacheToUrl url: URL, completionHandler: ((URL?) -> Void)? = nil) {
        let bounds = canvas.bounds
        DispatchQueue.global().async {
            do {
                let manager = FileManager.default
                if manager.fileExists(atPath: url.path) {
                    try manager.removeItem(at: url)
                }
                if try !self.paths.isEmpty || (manager.attributesOfItem(atPath: FileUrl.CACHE.path)[FileAttributeKey.size] as? Int) ?? 0 > 0 {
                    try manager.copyItem(at: FileUrl.CACHE, to: url)
                    let writeHandle = try FileHandle(forWritingTo: url)
                    writeHandle.seekToEndOfFile()
                    var pointsToSave = [Point]()
                    for path in self.paths {
                        for point in path.points {
                            pointsToSave.append(point)
                        }
                    }
                    if !pointsToSave.isEmpty {
                        writeHandle.write(DataConverter.parse(pointsToData: pointsToSave, withScale: (CGFloat(UInt16.max) + 1) / min(bounds.width, bounds.height)))
                    }
                    writeHandle.closeFile()
                }
                DispatchQueue.main.async {
                    completionHandler?(url)
                }
            } catch {
                Logger.d("\(#function): \(error.localizedDescription)")
                DispatchQueue.main.async {
                    completionHandler?(nil)
                }
            }
        }
    }
}

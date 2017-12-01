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
        CanvasAnimationViewDelegate,
        CanvasSettingTableViewControllerDelegate,
        SizePickerViewControllerDelegate,
        BrushPickerViewControllerDelegate,
        ColorPickerViewControllerDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var canvas: JotView!
    @IBOutlet weak var canvasAnimation: CanvasAnimationView!
    @IBOutlet weak var gridView: GridView!
    @IBOutlet weak var undoButton: UIBarButtonItem!
    @IBOutlet weak var redoButton: UIBarButtonItem!
    @IBOutlet weak var playButton: UIBarButtonItem!
    @IBOutlet weak var saveButton: UIBarButtonItem!
    @IBOutlet weak var settingButton: UIBarButtonItem!
    @IBOutlet weak var sizeButton: UIBarButtonItem!
    @IBOutlet weak var eraserButton: UIBarButtonItem!
    @IBOutlet weak var penButton: UIButton!
    @IBOutlet weak var resetButton: UIBarButtonItem!
    @IBOutlet weak var colorButton: UIBarButtonItem!
    private var brush = Brush(minSize: 6, maxSize: 12, minAlpha: 0.9, maxAlpha: 0.9)
    private var lastPenType: Type?
    var jotViewStateInkPath: String! {
        return try! FileManager.default.url(for: .documentDirectory, in: .userDomainMask, appropriateFor: nil, create: true).path.appending("ink.png")
    }
    var jotViewStatePlistPath: String! {
        return try! FileManager.default.url(for: .documentDirectory, in: .userDomainMask, appropriateFor: nil, create: true).path.appending("state.plist")
    }
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }

    override func viewDidLoad() {
        canvas.delegate = self
        canvasAnimation.delegate = self
        penButton.layer.cornerRadius = view.bounds.width / 10
        let size = CGSize(width: 33, height: 33)
        let count = UserDefaults.standard.integer(forKey: Default.GRID)
        gridView.set(horizontalCount: count, verticalCount: count)
        UIGraphicsBeginImageContextWithOptions(size, false, UIScreen.main.scale)
        UIColor.white.setStroke()
        UIBezierPath(arcCenter: CGPoint(x: size.width / 2, y: size.height / 2), radius: brush.minSize / 2, startAngle: 0, endAngle: CGFloat.pi * 2, clockwise: true).stroke()
        sizeButton.image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsGetCurrentContext()?.clear(CGRect(origin: .zero, size: size))
        UIBezierPath(roundedRect: CGRect(origin: .zero, size: size), cornerRadius: 4).fill()
        colorButton.image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
    }

    override func viewDidAppear(_ animated: Bool) {
        if canvas.isHidden {
            let stateProxy = JotViewStateProxy(delegate: self)
            stateProxy?.loadJotStateAsynchronously(false, with: canvas.bounds.size, andScale: UIScreen.main.scale, andContext: canvas.context, andBufferManager: JotBufferManager.sharedInstance())
            canvas.loadState(stateProxy)
        } else {
            loading.hide(true)
        }
        NotificationCenter.default.addObserver(self, selector: #selector(applicationDidEnterBackground), name: .UIApplicationDidEnterBackground, object: nil)
    }

    override func viewWillDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self)
    }

    @objc func applicationDidEnterBackground(notification: Notification) {
//        canvas.save()
    }

    @IBAction func undo(_ sender: Any) {
        canvas.undo()
    }

    @IBAction func redo(_ sender: Any) {
        canvas.redo()
    }

    @IBAction func reset(_ sender: Any) {
        presentConfirmationDialog(title: "alert_reset_title".localized, message: "alert_reset_content".localized) {
            success in
            if success {
                self.canvas.clear(true)
            }
        }
    }

    @IBAction func play(_ sender: UIBarButtonItem) {
//        do {
//            let url = try FileManager.default.url(
//                    for: .documentationDirectory,
//                    in: .userDomainMask,
//                    appropriateFor: nil,
//                    create: true).appendingPathComponent("animation.bdw")
//            sender.isEnabled = false
//            undoButton.isEnabled = false
//            redoButton.isEnabled = false
//            saveButton.isEnabled = false
//            settingButton.isEnabled = false
//            sizeButton.isEnabled = false
//            eraserButton.isEnabled = false
//            resetButton.isEnabled = false
//            colorButton.isEnabled = false
//            canvas.isUserInteractionEnabled = false
//            canvas.save(toUrl: url) {
//                url in
//                self.canvasAnimation.url = url
//                self.canvasAnimation.play()
//                self.canvasAnimation.isHidden = false
//                self.canvas.isHidden = true
//            }
//        } catch {
//            Logger.d("\(#function): \(error.localizedDescription)")
//        }
    }

    @IBAction func upload(_ sender: Any) {
//        loading.hide(false)
//        canvas.save() {
//            url in
//            self.performSegue(withIdentifier: Segue.UPLOAD, sender: url)
//        }
    }

    @IBAction func didSelectEraser(_ sender: UIBarButtonItem) {
//        if let lastPenType = lastPenType {
//            sender.tintColor = .white
//            canvas.type = lastPenType
//            self.lastPenType = nil
//        } else {
//            sender.tintColor = .black
//            lastPenType = canvas.type
//            canvas.type = .eraser
//        }
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = segue.destination as? CanvasSettingTableViewController {
            controller.delegate = self
            controller.popoverPresentationController?.delegate = self
            controller.popoverPresentationController?.canOverlapSourceViewRect = true
            controller.popoverPresentationController?.backgroundColor = UIColor.black.withAlphaComponent(0.5)
            controller.preferredContentSize = CGSize(width: 60, height: 240)
        } else if let controller = segue.destination as? SizePickerViewController {
            controller.delegate = self
            controller.value = Float(brush.minSize)
            controller.popoverPresentationController?.delegate = self
            controller.popoverPresentationController?.canOverlapSourceViewRect = true
            controller.popoverPresentationController?.backgroundColor = UIColor.black.withAlphaComponent(0.5)
            controller.preferredContentSize = CGSize(width: traitCollection.horizontalSizeClass == .compact ? view.bounds.width : view.bounds.width / 2, height: 76)
        } else if let controller = segue.destination as? BrushPickerViewController {
            controller.delegate = self
            controller.value = Float(brush.maxAlpha)
            controller.popoverPresentationController?.delegate = self
            controller.popoverPresentationController?.canOverlapSourceViewRect = true
            controller.popoverPresentationController?.backgroundColor = UIColor.black.withAlphaComponent(0.5)
            controller.preferredContentSize = CGSize(width: traitCollection.horizontalSizeClass == .compact ? view.bounds.width : view.bounds.width / 2, height: 208)
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
        } else if let url = sender as? URL,
                  let controller = segue.destination as? UploadViewController {
//            controller.workThumbnailData = canvas.thumbnailData()
//            controller.workFileUrl = url
        }
    }

    override func onBackPressed(_ sender: Any) {
        canvasAnimation.pause()
//        canvas.save()
//        canvas.close()
        super.onBackPressed(sender)
    }

    func adaptivePresentationStyle(for controller: UIPresentationController) -> UIModalPresentationStyle {
        return .none
    }

//    internal func canvasPathsDidChange() {
//        undoButton.isEnabled = !canvas.paths.isEmpty
//        redoButton.isEnabled = !canvas.redoPaths.isEmpty
//        playButton.isEnabled = !canvas.paths.isEmpty
//        if !playButton.isEnabled {
//            playButton.isEnabled = canvas.persistentImage != nil
//        }
//        saveButton.isEnabled = playButton.isEnabled
//        resetButton.isEnabled = !canvas.paths.isEmpty
//        if !resetButton.isEnabled {
//            resetButton.isEnabled = canvas.persistentImage != nil
//        }
//    }
//    internal func canvas(changeBackgroundColor color: UIColor) {
//        gridView.backgroundColor = color
//    }
    internal func canvasAnimationDidFinishAnimation() {
//        undoButton.isEnabled = true
//        redoButton.isEnabled = !canvas.redoPaths.isEmpty
//        playButton.isEnabled = true
//        saveButton.isEnabled = true
//        settingButton.isEnabled = true
//        sizeButton.isEnabled = true
//        eraserButton.isEnabled = true
//        resetButton.isEnabled = true
//        colorButton.isEnabled = true
//        canvas.isHidden = false
//        canvas.isUserInteractionEnabled = true
//        canvasAnimation.isHidden = true
    }

    internal func canvasAnimationFileParseError() {
        presentDialog(title: "canvas_data_parse_error_title".localized, message: "canvas_data_parse_error_content".localized) {
            action in
            super.onBackPressed(self)
        }
    }

    internal func canvasAnimation(changeBackgroundColor color: UIColor) {
        gridView.backgroundColor = color
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
        case 2: break
//            if let data = canvas.thumbnailData(),
//               let image = UIImage(data: data) {
//                checkPhotosPermission(successHandler: {
//                    let flashView = UIView(frame: self.canvas.frame)
//                    flashView.backgroundColor = .white
//                    self.view.addSubview(flashView)
//                    UIView.animate(
//                            withDuration: 1,
//                            animations: {
//                                flashView.alpha = 0
//                            },
//                            completion: {
//                                finished in
//                                flashView.removeFromSuperview()
//                            })
//                    AppDelegate.save(asset: image)
//                })
//            }
        case 3:
            break
        default:
            break
        }
    }

    internal func sizePicker(didSelect size: CGFloat) {
        brush.minSize = size
        brush.maxSize = size * 2
        let rect = CGSize(width: 33, height: 33)
        UIGraphicsBeginImageContextWithOptions(rect, false, UIScreen.main.scale)
        UIColor.white.setStroke()
        UIBezierPath(arcCenter: CGPoint(x: rect.width / 2, y: rect.height / 2), radius: brush.minSize / 2, startAngle: 0, endAngle: CGFloat.pi * 2, clockwise: true).stroke()
        sizeButton.image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
    }

    internal func brushPicker(didSelect type: Type) {
//        canvas.type = type
//        var image: UIImage? = nil
//        switch type {
//        case .crayon:
//            image = UIImage(named: "draw_pen_ic_3")
//        case .pencil:
//            image = UIImage(named: "draw_pen_ic_2")
//        case .pen:
//            image = UIImage(named: "draw_pen_ic_1")
//        case .airbrush:
//            image = UIImage(named: "draw_pen_ic_5")
//        case .marker:
//            image = UIImage(named: "draw_pen_ic_4")
//        default:
//            return
//        }
//        penButton.setImage(image, for: .normal)
    }

    internal func brushPicker(didSelect alpha: CGFloat) {
//        canvas.opacity = 1 - alpha
    }

    internal func colorPicker(didSelect color: UIColor, type: ColorPickerViewController.ColorType?) {
        if type == .canvas {
            brush.color = color
            colorButton.tintColor = color
        } else {
//            canvas.set(backgroundColor: color)
//            gridView.backgroundColor = color
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
        return true
    }

    internal func willMoveStroke(withCoalescedTouch coalescedTouch: UITouch!, from touch: UITouch!) {
    }

    internal func willEndStroke(withCoalescedTouch coalescedTouch: UITouch!, from touch: UITouch!, shortStrokeEnding: Bool) {
    }

    internal func didEndStroke(withCoalescedTouch coalescedTouch: UITouch!, from touch: UITouch!) {
    }

    internal func willCancel(_ stroke: JotStroke!, withCoalescedTouch coalescedTouch: UITouch!, from touch: UITouch!) {
    }

    internal func didCancel(_ stroke: JotStroke!, withCoalescedTouch coalescedTouch: UITouch!, from touch: UITouch!) {
    }

    internal func didLoadState(_ state: JotViewStateProxy!) {
        loading.hide(true)
        canvas.isHidden = false
        UIView.animate(withDuration: 0.4) {
            self.canvas.alpha = 1
        }
    }

    internal func didUnloadState(_ state: JotViewStateProxy!) {
    }
}

//
//  WorkViewController.swift
//  BonnieDraw
//
//  Created by Professor on 16/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class WorkViewController: BackButtonViewController, URLSessionDelegate, JotViewDelegate, JotViewStateProxyDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var gridView: GridView!
    @IBOutlet weak var canvas: JotView!
    @IBOutlet weak var thumbnail: UIImageView?
    @IBOutlet weak var progressBar: UIProgressView!
    @IBOutlet weak var navigationBar: UINavigationBar!
    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var profileName: UILabel!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var like: UIButton!
    @IBOutlet weak var collect: UIButton!
    @IBOutlet weak var play: UIButton!
    @IBOutlet weak var previousStep: UIButton!
    @IBOutlet weak var nextStep: UIButton!
    @IBOutlet weak var decreaseSpeed: UIButton!
    @IBOutlet weak var increaseSpeed: UIButton!
    @IBOutlet weak var descriptionLabel: UILabel!
    private var brush = Brush()
    private var drawPoints = [Point]()
    private var readHandle: FileHandle?
    private var timer: Timer?
    private var animationSpeed = 1.0
    private var request: Request?
    var delegate: WorkViewControllerDelegate?
    var jotViewStateInkPath = FileUrl.INK.path
    var jotViewStatePlistPath = FileUrl.STATE.path
    var work: Work?

    override func viewDidLoad() {
        like.isSelected = work?.isLike ?? false
        collect.isSelected = work?.isCollect ?? false
        like.setImage(UIImage(named: like.isSelected ? "work_ic_like_on" : "work_ic_like"), for: .normal)
        collect.setImage(UIImage(named: collect.isSelected ? "collect_ic_on" : "collect_ic_off"), for: .normal)
        thumbnail?.sd_setShowActivityIndicatorView(true)
        thumbnail?.sd_setIndicatorStyle(.gray)
        if navigationBar.items?.first?.titleView == nil {
            let titleView = Bundle.main.loadView(from: "TitleView")
            titleView?.backgroundColor = .clear
            navigationBar.items?.first?.titleView = titleView
        }
        profileName.text = work?.profileName
        titleLabel.text = work?.title
        descriptionLabel.text = work?.description
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
        UIApplication.shared.isIdleTimerDisabled = false
        timer?.invalidate()
        if !drawPoints.isEmpty {
            canvas.drawCancelled()
        }
        request?.cancel()
        NotificationCenter.default.removeObserver(self)
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = segue.destination as? CommentViewController {
            controller.work = work
        }
    }

    @objc func applicationDidEnterBackground(notification: Notification) {
        UIApplication.shared.isIdleTimerDisabled = false
        timer?.invalidate()
        play.setImage(UIImage(named: "drawplay_ic_play"), for: .normal)
        play.isSelected = false
        previousStep.isEnabled = true
        nextStep.isEnabled = true
    }

    private func downloadData() {
        guard let fileUrl = work?.file else {
            return
        }
        progressBar.progress = 0
        loading.hide(false)
        request = Alamofire.download(fileUrl) {
            _, _ in
            return (FileUrl.RESULT, [.removePreviousFile, .createIntermediateDirectories])
        }.downloadProgress() {
            progress in
            self.progressBar.setProgress(Float(progress.fractionCompleted) * 0.9, animated: true)
        }.response(queue: DispatchQueue.main) {
            response in
            guard response.error == nil else {
                self.presentConfirmationDialog(
                        title: "service_download_fail_title".localized,
                        message: "app_network_unreachable_content".localized) {
                    success in
                    if success {
                        self.downloadData()
                    } else {
                        self.dismiss(animated: true)
                    }
                }
                return
            }
            self.thumbnail?.setImage(with: self.work?.thumbnail)
            self.loading.hide(true)
            self.canvas.isHidden = false
            UIView.animate(withDuration: 0.4) {
                self.canvas.alpha = 1
            }
        }
    }

    @IBAction func play(_ sender: UIButton) {
        thumbnail?.removeFromSuperview()
        if sender.isSelected {
            UIApplication.shared.isIdleTimerDisabled = false
            timer?.invalidate()
            sender.setImage(UIImage(named: "drawplay_ic_play"), for: .normal)
            sender.isSelected = false
            previousStep.isEnabled = true
            nextStep.isEnabled = true
        } else {
            if drawPoints.isEmpty {
                gridView.backgroundColor = .white
                canvas.clear(true)
                do {
                    let readHandle = try FileHandle(forReadingFrom: FileUrl.RESULT)
                    drawPoints.append(
                            contentsOf: DataConverter.parse(
                                    dataToPoints:
                                    readHandle.readData(ofLength: Int(POINT_BUFFER_COUNT * LENGTH_SIZE)),
                                    withScale: (CGFloat(UInt16.max) + 1) / min(canvas.bounds.width, canvas.bounds.height)))
                    if !drawPoints.isEmpty {
                        self.readHandle = readHandle
                        draw(instantly: false)
                    }
                } catch {
                    Logger.d("\(#function): \(error.localizedDescription)")
                }
            } else {
                draw(instantly: false)
            }
            sender.setImage(UIImage(named: "drawplay_ic_timeout"), for: .normal)
            sender.isSelected = true
            previousStep.isEnabled = false
            nextStep.isEnabled = false
            UIApplication.shared.isIdleTimerDisabled = true
        }
    }

    @IBAction func previousStep(_ sender: Any) {
    }

    @IBAction func nextStep(_ sender: Any) {
    }

    @IBAction func decreaseSpeed(_ sender: Any) {
        animationSpeed *= 2
        checkSpeedButtons()
    }

    @IBAction func increaseSpeed(_ sender: Any) {
        animationSpeed /= 2
        checkSpeedButtons()
    }

    @IBAction func like(_ sender: UIButton) {
        guard AppDelegate.reachability.connection != .none else {
            presentConfirmationDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized) {
                success in
                if success {
                    self.downloadData()
                }
            }
            return
        }
        guard let token = UserDefaults.standard.string(forKey: Default.TOKEN) else {
            return
        }
        request?.cancel()
        request = Alamofire.request(
                Service.standard(withPath: Service.SET_LIKE),
                method: .post,
                parameters: ["ui": UserDefaults.standard.integer(forKey: Default.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE, "fn": sender.isSelected ? 0 : 1, "worksId": work?.id ?? 0, "likeType": 1],
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], let res = data["res"] as? Int else {
                    self.presentDialog(
                            title: "service_download_fail_title".localized,
                            message: "app_network_unreachable_content".localized)
                    return
                }
                if res != 1 {
                    self.presentDialog(
                            title: "service_download_fail_title".localized,
                            message: data["msg"] as? String)
                } else {
                    sender.isSelected = !sender.isSelected
                    sender.setImage(UIImage(named: sender.isSelected ? "work_ic_like_on" : "work_ic_like"), for: .normal)
                    self.work?.isLike = sender.isSelected
                    if let work = self.work {
                        self.delegate?.work(didChange: work)
                    }
                }
            case .failure(let error):
                if let error = error as? URLError, error.code == .cancelled {
                    return
                }
                self.presentDialog(
                        title: "service_download_fail_title".localized,
                        message: error.localizedDescription)
            }
        }
    }

    @IBAction func share(_ sender: Any) {
    }

    @IBAction func collect(_ sender: UIButton) {
        guard AppDelegate.reachability.connection != .none else {
            presentConfirmationDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized) {
                success in
                if success {
                    self.downloadData()
                }
            }
            return
        }
        guard let token = UserDefaults.standard.string(forKey: Default.TOKEN) else {
            return
        }
        sender.isEnabled = false
        Alamofire.request(
                Service.standard(withPath: Service.SET_COLLECTION),
                method: .post,
                parameters: ["ui": UserDefaults.standard.integer(forKey: Default.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE, "fn": sender.isSelected ? 0 : 1, "worksId": work?.id ?? 0, "likeType": 1],
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            sender.isEnabled = true
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], let res = data["res"] as? Int else {
                    self.presentDialog(
                            title: "service_download_fail_title".localized,
                            message: "app_network_unreachable_content".localized)
                    return
                }
                if res != 1 {
                    self.presentDialog(
                            title: "service_download_fail_title".localized,
                            message: data["msg"] as? String)
                } else {
                    sender.isSelected = !sender.isSelected
                    sender.setImage(UIImage(named: sender.isSelected ? "collect_ic_on" : "collect_ic_off"), for: .normal)
                    self.work?.isCollect = sender.isSelected
                    if let work = self.work {
                        self.delegate?.work(didChange: work)
                    }
                }
            case .failure(let error):
                if let error = error as? URLError, error.code == .cancelled {
                    return
                }
                self.presentDialog(
                        title: "service_download_fail_title".localized,
                        message: error.localizedDescription)
            }
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
        canvas.loadState(state)
        downloadData()
    }

    internal func didUnloadState(_ state: JotViewStateProxy!) {
    }

    private func draw(instantly: Bool) {
        if !drawPoints.isEmpty {
            let point = drawPoints.removeFirst()
            let handler: (Bool) -> Void = {
                instantly in
                switch point.action {
                case .move:
                    self.canvas.drawMoved(point.position,
                            width: point.size,
                            color: point.type != .eraser ? point.color : nil,
                            smoothness: self.brush.smoothness,
                            stepWidth: self.brush.stepWidth)
                case .up:
                    if point.type != .background {
                        self.canvas.drawEnded(point.position,
                                width: point.size,
                                color: point.type != .eraser ? point.color : nil,
                                smoothness: self.brush.smoothness,
                                stepWidth: self.brush.stepWidth)
                    }
                case .down:
                    if point.type != .background {
                        self.brush.type = point.type
                        self.canvas.drawBegan(point.position,
                                width: point.size,
                                color: point.type != .eraser ? point.color : nil,
                                smoothness: self.brush.smoothness,
                                stepWidth: self.brush.stepWidth)
                    } else {
                        self.gridView.backgroundColor = point.color
                    }
                }
                if let readHandle = self.readHandle, self.drawPoints.count < POINT_BUFFER_COUNT / 2 {
                    let maxByteCount = Int(POINT_BUFFER_COUNT * LENGTH_SIZE)
                    let data = readHandle.readData(ofLength: maxByteCount)
                    self.drawPoints.append(contentsOf: DataConverter.parse(dataToPoints: data, withScale: (CGFloat(UInt16.max) + 1) / min(self.canvas.bounds.width, self.canvas.bounds.height)))
                    if data.count < maxByteCount {
                        readHandle.closeFile()
                        self.readHandle = nil
                    }
                }
                self.draw(instantly: instantly)
            }
            if !instantly {
                timer?.invalidate()
                timer = Timer.scheduledTimer(withTimeInterval: ANIMATION_TIMER * animationSpeed, repeats: false) {
                    timer in
                    handler(false)
                }
            } else {
                handler(true)
            }
        } else {
            play.setImage(UIImage(named: "drawplay_ic_play"), for: .normal)
            play.isSelected = false
            previousStep.isEnabled = true
            nextStep.isEnabled = true
        }
    }

    private func checkSpeedButtons() {
        title = "\("canvas_animation_title".localized) x\(1 / animationSpeed)"
        if animationSpeed >= 4 {
            decreaseSpeed.isEnabled = false
            increaseSpeed.isEnabled = true
        } else if animationSpeed <= 0.25 {
            decreaseSpeed.isEnabled = true
            increaseSpeed.isEnabled = false
        } else {
            decreaseSpeed.isEnabled = true
            increaseSpeed.isEnabled = true
        }
    }
}

protocol WorkViewControllerDelegate {
    func work(didChange work: Work)
}

//
//  WorkViewController.swift
//  BonnieDraw
//
//  Created by Professor on 16/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class WorkViewController: BackButtonViewController, URLSessionDelegate, JotViewDelegate, JotViewStateProxyDelegate, CommentViewControllerDelegate, EditViewControllerDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var gridView: GridView!
    @IBOutlet weak var canvas: JotView!
    @IBOutlet weak var thumbnail: UIImageView?
    @IBOutlet weak var progressBar: UIProgressView!
    @IBOutlet weak var profileImage: UIButton!
    @IBOutlet weak var profileName: UIButton!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var likeButton: UIButton!
    @IBOutlet weak var likes: UILabel!
    @IBOutlet weak var comments: UILabel!
    @IBOutlet weak var openLink: UIButton!
    @IBOutlet weak var collect: UIButton!
    @IBOutlet weak var play: UIButton!
    @IBOutlet weak var decrement: UIButton!
    @IBOutlet weak var increment: UIButton!
    @IBOutlet weak var descriptionLabel: UILabel!
    private var brush = Brush()
    private var persistentBackgroundColor: UIColor?
    private var paths = [Path]()
    private var drawPoints = [Point]()
    private var readHandle: FileHandle?
    private var request: Request?
    private var timer: Timer?
    private var isIncrementStep = false
    private var animationSpeed = 1.0 {
        didSet {
            title = "\("canvas_animation_title".localized) x\(1 / animationSpeed)"
        }
    }
    var delegate: WorkViewControllerDelegate?
    var jotViewStateInkPath = FileUrl.INK.path
    var jotViewStatePlistPath = FileUrl.STATE.path
    var workId: Int?
    var work: Work?

    override func viewDidLoad() {
        profileImage.sd_setShowActivityIndicatorView(true)
        profileImage.sd_setIndicatorStyle(.gray)
        thumbnail?.sd_setShowActivityIndicatorView(true)
        thumbnail?.sd_setIndicatorStyle(.gray)
        navigationItem.titleView = UIImageView(image: UIImage(named: "title_logo"))
        if let work = work {
            set(viewDataWith: work)
        }
    }

    override func viewDidAppear(_ animated: Bool) {
        if work == nil {
            downloadData()
        } else if canvas.state == nil {
            canvas.finishInit()
            let stateProxy = JotViewStateProxy(delegate: self)
            stateProxy?.loadJotStateAsynchronously(false, with: canvas.bounds.size, andScale: UIScreen.main.scale, andContext: canvas.context, andBufferManager: JotBufferManager.sharedInstance())
        } else {
            loading.hide(true)
        }
        NotificationCenter.default.addObserver(self, selector: #selector(applicationWillResignActive), name: .UIApplicationWillResignActive, object: nil)
    }

    override func viewWillDisappear(_ animated: Bool) {
        if timer?.isValid ?? false {
            UIApplication.shared.isIdleTimerDisabled = false
            timer?.invalidate()
            play.setImage(UIImage(named: "drawplay_ic_play"), for: .normal)
            play.isSelected = false
            setActionButtons()
        }
        if !drawPoints.isEmpty {
            canvas.drawCancelled()
            if drawPoints.first?.action != .down {
                drawPoints.insert(contentsOf: paths.removeLast().points, at: 0)
            }
        }
        request?.cancel()
        NotificationCenter.default.removeObserver(self)
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = segue.destination as? AccountViewController {
            controller.userId = work?.userId
        } else if let controller = segue.destination as? CommentViewController {
            controller.delegate = self
            controller.work = work
        } else if let controller = segue.destination as? EditViewController {
            controller.delegate = self
            controller.work = work
        } else if let controller = segue.destination as? ReportViewController {
            controller.work = work
        }
    }

    @objc func applicationWillResignActive(notification: Notification) {
        UIApplication.shared.isIdleTimerDisabled = false
        timer?.invalidate()
        play.setImage(UIImage(named: "drawplay_ic_play"), for: .normal)
        play.isSelected = false
        setActionButtons()
    }

    private func set(viewDataWith work: Work) {
        if work.userId == UserDefaults.standard.integer(forKey: Default.USER_ID) || delegate is AccountViewController {
            profileImage.isUserInteractionEnabled = false
            profileName.isUserInteractionEnabled = false
        }
        if work.isLike ?? false {
            likeButton.isUserInteractionEnabled = false
            likeButton.isSelected = true
        } else {
            likeButton.isUserInteractionEnabled = true
            likeButton.isSelected = false
        }
        likeButton.setImage(UIImage(named: likeButton.isSelected ? "work_ic_like_on" : "work_ic_like"), for: .normal)
        if let likes = work.likes, likes > 0 {
            self.likes.text = "\(likes)"
            self.likes.isHidden = false
        } else {
            likes.isHidden = true
        }
        if let comments = work.comments, comments > 0 {
            self.comments.text = "\(comments)"
            self.comments.isHidden = false
        } else {
            comments.isHidden = true
        }
        openLink.isHidden = work.link?.isEmpty ?? true
        collect.isSelected = work.isCollect ?? false
        collect.setImage(UIImage(named: collect.isSelected ? "collect_ic_on" : "collect_ic_off"), for: .normal)
        profileImage.setImage(with: work.profileImage, placeholderImage: UIImage(named: "photo-square"))
        profileName.setTitle(work.profileName, for: .normal)
        titleLabel.text = work.title
        descriptionLabel.text = work.summery
    }

    private func downloadData() {
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
                Service.standard(withPath: Service.WORK_LIST),
                method: .post,
                parameters: ["ui": UserDefaults.standard.integer(forKey: Default.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE, "wid": workId ?? 0],
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], let workDictionary = data["work"] as? [String: Any] else {
                    self.presentConfirmationDialog(
                            title: "service_download_fail_title".localized,
                            message: "app_network_unreachable_content".localized) {
                        success in
                        if success {
                            self.downloadData()
                        }
                    }
                    return
                }
                let work = Work(withDictionary: workDictionary)
                self.set(viewDataWith: work)
                self.work = work
                if self.canvas.state == nil {
                    self.canvas.finishInit()
                    let stateProxy = JotViewStateProxy(delegate: self)
                    stateProxy?.loadJotStateAsynchronously(false, with: self.canvas.bounds.size, andScale: UIScreen.main.scale, andContext: self.canvas.context, andBufferManager: JotBufferManager.sharedInstance())
                } else {
                    self.loading.hide(true)
                }
            case .failure(let error):
                if let error = error as? URLError, error.code == .cancelled {
                    return
                }
                self.presentConfirmationDialog(
                        title: "service_download_fail_title".localized,
                        message: error.localizedDescription) {
                    success in
                    if success {
                        self.downloadData()
                    }
                }
            }
        }
    }

    private func downloadFile() {
        guard let fileUrl = work?.file else {
            return
        }
        progressBar.progress = 0
        loading.hide(false)
        request?.cancel()
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
                        self.downloadFile()
                    } else {
                        self.dismiss(animated: true)
                    }
                }
                return
            }
            self.thumbnail?.setImage(with: self.work?.thumbnail)
            self.play.isEnabled = true
            self.increment.isEnabled = true
            self.loading.hide(true)
            self.canvas.isHidden = false
            UIView.animate(withDuration: 0.3) {
                self.canvas.alpha = 1
            }
        }
    }

    internal func comment(didCommentOnWork changedWork: Work) {
        work = changedWork
        if let comments = changedWork.comments, comments > 0 {
            self.comments.text = "\(comments)"
            self.comments.isHidden = false
        } else {
            comments.isHidden = true
        }
        delegate?.work(didChange: changedWork)
    }

    internal func commentDidTapProfile() {
    }

    internal func edit(didChange changedWork: Work) {
        work = changedWork
        set(viewDataWith: changedWork)
        delegate?.work(didChange: changedWork)
    }

    @IBAction func play(_ sender: UIButton) {
        if sender.isSelected {
            UIApplication.shared.isIdleTimerDisabled = false
            timer?.invalidate()
            sender.setImage(UIImage(named: "drawplay_ic_play"), for: .normal)
            sender.isSelected = false
            setActionButtons()
        } else {
            sender.setImage(UIImage(named: "drawplay_ic_timeout"), for: .normal)
            sender.isSelected = true
            isIncrementStep = false
            animationSpeed = 1
            setActionButtons()
            if drawPoints.isEmpty {
                thumbnail?.removeFromSuperview()
                canvas.clear(true)
                gridView.backgroundColor = .white
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
                        UIApplication.shared.isIdleTimerDisabled = true
                    } else {
                        readHandle.closeFile()
                    }
                } catch {
                    Logger.d("\(#function): \(error.localizedDescription)")
                }
            } else {
                draw(instantly: false)
                UIApplication.shared.isIdleTimerDisabled = true
            }
        }
    }

    @IBAction func decrement(_ sender: Any) {
        if play.isSelected {
            animationSpeed *= 2
            checkSpeedButtons()
        } else {
            let points = paths.removeLast().points
            drawPoints.insert(contentsOf: points, at: 0)
            if points.first?.type == .background {
                var backgroundColor: UIColor? = nil
                for path in paths.reversed() {
                    if let first = path.points.first,
                       first.type == .background {
                        backgroundColor = first.color
                        break
                    }
                }
                gridView.backgroundColor = backgroundColor ?? persistentBackgroundColor ?? .white
            } else if canvas.state.currentStroke != nil {
                canvas.drawCancelled()
            } else {
                canvas.undo()
            }
            checkStepButtons()
        }
    }

    @IBAction func increment(_ sender: Any) {
        if play.isSelected {
            animationSpeed /= 2
            checkSpeedButtons()
        } else {
            play.setImage(UIImage(named: "drawplay_ic_timeout"), for: .normal)
            play.isSelected = true
            isIncrementStep = true
            animationSpeed = 1
            setActionButtons()
            if !drawPoints.isEmpty {
                draw(instantly: false)
            } else {
                thumbnail?.removeFromSuperview()
                canvas.clear(true)
                gridView.backgroundColor = .white
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
                        UIApplication.shared.isIdleTimerDisabled = true
                    } else {
                        readHandle.closeFile()
                    }
                } catch {
                    Logger.d("\(#function): \(error.localizedDescription)")
                }
            }
        }
    }

    private func setActionButtons() {
        if play.isSelected {
            decrement.setImage(UIImage(named: "drawplay_ic_down_on"), for: .normal)
            decrement.setImage(UIImage(named: "drawplay_ic_down_off"), for: .disabled)
            increment.setImage(UIImage(named: "drawplay_ic_doble_on"), for: .normal)
            increment.setImage(UIImage(named: "drawplay_ic_doble_off"), for: .disabled)
            checkSpeedButtons()
        } else {
            decrement.setImage(UIImage(named: "drawplay_ic_prev_on"), for: .normal)
            decrement.setImage(UIImage(named: "drawplay_ic_prev_off"), for: .disabled)
            increment.setImage(UIImage(named: "drawplay_ic_next_on"), for: .normal)
            increment.setImage(UIImage(named: "drawplay_ic_next_off"), for: .disabled)
            checkStepButtons()
        }
    }

    @IBAction func more(_ sender: UIButton) {
        let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        alert.view.tintColor = UIColor.getAccentColor()
        if let presentation = alert.popoverPresentationController {
            presentation.sourceView = sender
            presentation.sourceRect = sender.bounds
        }
        let color = UIColor.gray
        let copyLinkAction = UIAlertAction(title: "more_copy_link".localized, style: .default) {
            action in
            UIPasteboard.general.url = URL(string: Service.sharePath(withId: self.work?.id))
        }
        copyLinkAction.setValue(color, forKey: "titleTextColor")
        alert.addAction(copyLinkAction)
        if work?.userId != UserDefaults.standard.integer(forKey: Default.USER_ID) {
            let reportAction = UIAlertAction(title: "more_report".localized, style: .destructive) {
                action in
                guard AppDelegate.reachability.connection != .none else {
                    self.presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
                    return
                }
                self.performSegue(withIdentifier: Segue.REPORT, sender: sender)
            }
            alert.addAction(reportAction)
        } else {
            let editAction = UIAlertAction(title: "more_edit_work".localized, style: .default) {
                action in
                guard AppDelegate.reachability.connection != .none else {
                    self.presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
                    return
                }
                self.performSegue(withIdentifier: Segue.EDIT, sender: sender)
            }
            editAction.setValue(color, forKey: "titleTextColor")
            alert.addAction(editAction)
            let removeAction = UIAlertAction(title: "more_remove_work".localized, style: .destructive) {
                action in
                let alert = UIAlertController(title: "more_remove_work".localized, message: "alert_delete_content".localized, preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "alert_button_delete".localized, style: .destructive) {
                    action in
                    guard AppDelegate.reachability.connection != .none else {
                        self.presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
                        return
                    }
                    guard let token = UserDefaults.standard.string(forKey: Default.TOKEN),
                          let work = self.work,
                          let id = work.id else {
                        return
                    }
                    self.loading.hide(false)
                    self.request?.cancel()
                    self.request = Alamofire.request(
                            Service.standard(withPath: Service.WORK_DELETE),
                            method: .post,
                            parameters: ["ui": UserDefaults.standard.integer(forKey: Default.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE, "worksId": id],
                            encoding: JSONEncoding.default).validate().responseJSON {
                        response in
                        switch response.result {
                        case .success:
                            self.loading.hide(true)
                            guard let data = response.result.value as? [String: Any], let response = data["res"] as? Int else {
                                self.presentDialog(title: "alert_delete_fail_title".localized, message: "app_network_unreachable_content".localized)
                                return
                            }
                            if response != 1 {
                                self.presentDialog(
                                        title: "service_download_fail_title".localized,
                                        message: data["msg"] as? String)
                            } else {
                                self.delegate?.work(didDelete: work)
                                self.onBackPressed(sender)
                            }
                        case .failure(let error):
                            self.loading.hide(true)
                            if let error = error as? URLError, error.code == .cancelled {
                                return
                            }
                            self.presentDialog(title: "alert_delete_fail_title".localized, message: error.localizedDescription)
                        }
                    }
                })
                alert.addAction(UIAlertAction(title: "alert_button_cancel".localized, style: .cancel))
                alert.view.tintColor = UIColor.getAccentColor()
                self.present(alert, animated: true)
            }
            alert.addAction(removeAction)
        }
        let cancelAction = UIAlertAction(title: "alert_button_cancel".localized, style: .cancel)
        alert.addAction(cancelAction)
        present(alert, animated: true)
    }

    @IBAction func like(_ sender: UIButton) {
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
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
                    sender.isUserInteractionEnabled = !sender.isSelected
                    sender.setImage(UIImage(named: sender.isSelected ? "work_ic_like_on" : "work_ic_like"), for: .normal)
                    self.work?.isLike = sender.isSelected
                    if let likes = self.work?.likes {
                        self.work?.likes = likes + (sender.isSelected ? 1 : -1)
                    }
                    if let likes = self.work?.likes, likes > 0 {
                        self.likes.text = "\(likes)"
                        self.likes.isHidden = false
                    } else {
                        self.likes.isHidden = true
                    }
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

    @IBAction func share(_ sender: UIButton) {
        if let url = URL(string: Service.sharePath(withId: work?.id)) {
            let controller = UIActivityViewController(activityItems: [url], applicationActivities: nil)
            controller.excludedActivityTypes = [.airDrop, .saveToCameraRoll, .assignToContact, .addToReadingList, .copyToPasteboard, .print]
            if let presentation = controller.popoverPresentationController {
                presentation.sourceView = sender
                presentation.sourceRect = sender.bounds
            }
            present(controller, animated: true)
        }
    }

    @IBAction func openLink(_ sender: UIButton) {
        if let link = URL(string: work?.link ?? ""),
           UIApplication.shared.canOpenURL(link) {
            UIApplication.shared.open(link, options: [:])
        }
    }

    @IBAction func collect(_ sender: UIButton) {
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
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
        downloadFile()
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
                    self.paths.last?.points.append(point)
                case .up:
                    if point.type != .background {
                        self.canvas.drawEnded(point.position,
                                width: point.size,
                                color: point.type != .eraser ? point.color : nil,
                                smoothness: self.brush.smoothness,
                                stepWidth: self.brush.stepWidth)
                    }
                    self.paths.last?.points.append(point)
                case .down:
                    if point.type != .background {
                        self.brush.type = point.type
                        self.brush.minSize = point.size
                        self.brush.maxSize = point.size * 1.5
                        self.canvas.drawBegan(point.position,
                                width: point.size,
                                color: point.type != .eraser ? point.color : nil,
                                smoothness: self.brush.smoothness,
                                stepWidth: self.brush.stepWidth)
                    } else {
                        self.gridView.backgroundColor = point.color
                    }
                    while self.paths.count >= self.canvas.state.undoLimit {
                        let path = self.paths.removeFirst()
                        if let first = path.points.first,
                           first.type == .background {
                            self.persistentBackgroundColor = first.color
                        }
                    }
                    self.paths.append(Path(points: [point]))
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
                if point.action == .up && self.isIncrementStep {
                    UIApplication.shared.isIdleTimerDisabled = false
                    self.play.setImage(UIImage(named: "drawplay_ic_play"), for: .normal)
                    self.play.isSelected = false
                    self.setActionButtons()
                    return
                }
                self.draw(instantly: instantly)
            }
            if !instantly {
                timer?.invalidate()
                timer = Timer.scheduledTimer(withTimeInterval: point.duration * animationSpeed, repeats: false) {
                    timer in
                    handler(false)
                }
            } else {
                handler(true)
            }
        } else {
            UIApplication.shared.isIdleTimerDisabled = false
            play.setImage(UIImage(named: "drawplay_ic_play"), for: .normal)
            play.isSelected = false
            setActionButtons()
        }
    }

    private func checkStepButtons() {
        decrement.isEnabled = !paths.isEmpty
        increment.isEnabled = !drawPoints.isEmpty
    }

    private func checkSpeedButtons() {
        if animationSpeed >= 4 {
            decrement.isEnabled = false
            increment.isEnabled = true
        } else if animationSpeed <= 0.0625 {
            decrement.isEnabled = true
            increment.isEnabled = false
        } else {
            decrement.isEnabled = true
            increment.isEnabled = true
        }
    }
}

protocol WorkViewControllerDelegate {
    func work(didChange work: Work)

    func work(didDelete work: Work)
}

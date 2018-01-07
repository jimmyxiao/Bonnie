//
//  CanvasAnimationViewController.swift
//  BonnieDraw
//
//  Created by Professor on 06/12/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CanvasAnimationViewController: BackButtonViewController, JotViewDelegate, JotViewStateProxyDelegate {
    @IBOutlet weak var gridView: GridView!
    @IBOutlet weak var canvas: JotView!
    @IBOutlet weak var thumbnail: UIImageView?
    @IBOutlet weak var play: UIButton!
    @IBOutlet weak var decrement: UIButton!
    @IBOutlet weak var increment: UIButton!
    private var brush = Brush()
    private var persistentBackgroundColor: UIColor?
    private var paths = [Path]()
    private var drawPoints = [Point]()
    private var readHandle: FileHandle?
    private var timer: Timer?
    private var isIncrementStep = false
    private var animationSpeed = 1.0 {
        didSet {
            title = "\("canvas_animation_title".localized) x\(1 / animationSpeed)"
        }
    }
    var workThumbnail: UIImage?
    var workFileUrl: URL?
    var jotViewStateInkPath = FileUrl.INK.path
    var jotViewStatePlistPath = FileUrl.STATE.path

    override func viewDidLoad() {
        thumbnail?.image = workThumbnail
        title = "\("canvas_animation_title".localized) x1.0"
    }

    override func viewDidAppear(_ animated: Bool) {
        if canvas.state == nil {
            canvas.finishInit()
            let stateProxy = JotViewStateProxy(delegate: self)
            stateProxy?.loadJotStateAsynchronously(false, with: canvas.bounds.size, andScale: UIScreen.main.scale, andContext: canvas.context, andBufferManager: JotBufferManager.sharedInstance())
        }
        NotificationCenter.default.addObserver(self, selector: #selector(applicationWillResignActive), name: .UIApplicationWillResignActive, object: nil)
    }

    override func viewWillDisappear(_ animated: Bool) {
        UIApplication.shared.isIdleTimerDisabled = false
        timer?.invalidate()
        if !drawPoints.isEmpty {
            canvas.drawCancelled()
        }
        NotificationCenter.default.removeObserver(self)
    }

    @objc func applicationWillResignActive(notification: Notification) {
        UIApplication.shared.isIdleTimerDisabled = false
        timer?.invalidate()
        play.setImage(UIImage(named: "drawplay_ic_play"), for: .normal)
        play.isSelected = false
        setActionButtons()
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
                guard let workFileUrl = workFileUrl else {
                    return
                }
                thumbnail?.removeFromSuperview()
                canvas.clear(true)
                gridView.backgroundColor = .white
                do {
                    let readHandle = try FileHandle(forReadingFrom: workFileUrl)
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
                guard let workFileUrl = workFileUrl else {
                    return
                }
                thumbnail?.removeFromSuperview()
                canvas.clear(true)
                gridView.backgroundColor = .white
                do {
                    let readHandle = try FileHandle(forReadingFrom: workFileUrl)
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
                timer = Timer.scheduledTimer(withTimeInterval: ANIMATION_TIMER * animationSpeed, repeats: false) {
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
        } else if animationSpeed <= 0.25 {
            decrement.isEnabled = true
            increment.isEnabled = false
        } else {
            decrement.isEnabled = true
            increment.isEnabled = true
        }
    }
}

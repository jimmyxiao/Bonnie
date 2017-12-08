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
    @IBOutlet weak var previousStep: UIButton!
    @IBOutlet weak var nextStep: UIButton!
    @IBOutlet weak var decreaseSpeed: UIButton!
    @IBOutlet weak var increaseSpeed: UIButton!
    var workThumbnail: UIImage?
    var workFileUrl: URL?
    private var brush = Brush(withBrushType: .pen, minSize: 6, maxSize: 12, minAlpha: 0.6, maxAlpha: 0.8)
    private var drawPoints = [Point]()
    private var readHandle: FileHandle?
    private var timer: Timer?
    private var animationSpeed = 1.0
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
        NotificationCenter.default.addObserver(self, selector: #selector(applicationDidEnterBackground), name: .UIApplicationDidEnterBackground, object: nil)
    }

    override func viewWillDisappear(_ animated: Bool) {
        UIApplication.shared.isIdleTimerDisabled = false
        timer?.invalidate()
        if !drawPoints.isEmpty {
            canvas.drawCancelled()
        }
        NotificationCenter.default.removeObserver(self)
    }

    @objc func applicationDidEnterBackground(notification: Notification) {
        UIApplication.shared.isIdleTimerDisabled = false
        timer?.invalidate()
        play.setImage(UIImage(named: "drawplay_ic_play"), for: .normal)
        play.isSelected = false
        previousStep.isEnabled = true
        nextStep.isEnabled = true
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
                guard let workFileUrl = workFileUrl else {
                    return
                }
                gridView.backgroundColor = .white
                canvas.clear(true)
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

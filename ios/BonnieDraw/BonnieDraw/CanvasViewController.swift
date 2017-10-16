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
        CanvasViewDelegate,
        SizePickerViewControllerDelegate,
        ColorPickerViewControllerDelegate {
    @IBOutlet weak var canvas: CanvasView!
    @IBOutlet weak var undoButton: UIBarButtonItem!
    @IBOutlet weak var redoButton: UIBarButtonItem!
    @IBOutlet weak var playButton: UIBarButtonItem!
    @IBOutlet weak var saveButton: UIBarButtonItem!
    @IBOutlet weak var sizeButton: UIBarButtonItem!
    @IBOutlet weak var eraserButton: UIBarButtonItem!
    @IBOutlet weak var penButton: UIButton!
    @IBOutlet weak var resetButton: UIBarButtonItem!
    @IBOutlet weak var colorButton: UIBarButtonItem!

    override func viewDidLoad() {
        canvas.delegate = self
        penButton.layer.cornerRadius = view.bounds.width / 10
        let size = CGSize(width: 28, height: 28)
        UIGraphicsBeginImageContextWithOptions(size, false, UIScreen.main.scale)
        UIBezierPath(arcCenter: CGPoint(x: size.width / 2, y: size.height / 2), radius: canvas.size / 2, startAngle: 0, endAngle: CGFloat(Double.pi * 2), clockwise: true).fill()
        sizeButton.image = UIGraphicsGetImageFromCurrentImageContext()
        sizeButton.tintColor = canvas.color
        UIGraphicsGetCurrentContext()?.clear(CGRect(origin: .zero, size: size))
        UIBezierPath(roundedRect: CGRect(origin: .zero, size: size), cornerRadius: 4).fill()
        colorButton.image = UIGraphicsGetImageFromCurrentImageContext()
        colorButton.tintColor = canvas.color
        UIGraphicsEndImageContext()
    }

    override func viewDidAppear(_ animated: Bool) {
        if canvas.isHidden {
            canvas.load()
            canvas.isHidden = false
            UIView.animate(withDuration: 0.4) {
                self.canvas.alpha = 1
            }
        }
    }

    @IBAction func undo(_ sender: Any) {
        canvas.undo()
    }

    @IBAction func redo(_ sender: Any) {
        canvas.redo()
    }

    @IBAction func reset(_ sender: AnyObject) {
        canvas.reset()
    }

    @IBAction func play(_ sender: AnyObject) {
        canvas.play()
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        canvas.lastTimestamp = -1
        if let controller = segue.destination as? SizePickerViewController {
            controller.delegate = self
            controller.popoverPresentationController?.delegate = self
            let height = CGFloat(controller.sizes.count * 44)
            let maxHeight = view.bounds.height - 111
            controller.preferredContentSize = CGSize(width: 44, height: height > maxHeight ? maxHeight : height)
        } else if let controller = segue.destination as? ColorPickerViewController {
            controller.delegate = self
            controller.popoverPresentationController?.delegate = self
            let height = CGFloat(controller.colors.count * 44)
            let maxHeight = view.bounds.height - 111
            controller.preferredContentSize = CGSize(width: 44, height: height > maxHeight ? maxHeight : height)
        } else if let controller = segue.destination as? SaveViewController {
            controller.workThumbnailData = canvas.thumbnailData()
            controller.workFileData = canvas.fileData()
        }
    }

    override func onBackPressed(_ sender: AnyObject) {
        canvas.stop()
        canvas.save()
        super.onBackPressed(sender)
    }

    func adaptivePresentationStyle(for controller: UIPresentationController) -> UIModalPresentationStyle {
        return .none
    }

    func canvasPathsDidChange() {
        undoButton.isEnabled = !canvas.paths.isEmpty
        redoButton.isEnabled = !canvas.redoPaths.isEmpty
        playButton.isEnabled = !canvas.paths.isEmpty
        if !playButton.isEnabled {
            playButton.isEnabled = canvas.persistentImage != nil
        }
        saveButton.isEnabled = playButton.isEnabled
        resetButton.isEnabled = !canvas.paths.isEmpty
        if !resetButton.isEnabled {
            resetButton.isEnabled = canvas.persistentImage != nil
        }
    }

    func canvasPathsWillBeginAnimation() {
        undoButton.isEnabled = false
        redoButton.isEnabled = false
        playButton.isEnabled = false
        saveButton.isEnabled = false
        sizeButton.isEnabled = false
        eraserButton.isEnabled = false
        resetButton.isEnabled = false
        colorButton.isEnabled = false
    }

    func canvasPathsDidFinishAnimation() {
        undoButton.isEnabled = true
        redoButton.isEnabled = !canvas.redoPaths.isEmpty
        playButton.isEnabled = true
        saveButton.isEnabled = true
        sizeButton.isEnabled = true
        eraserButton.isEnabled = true
        resetButton.isEnabled = true
        colorButton.isEnabled = true
    }

    func sizePicker(didSelect size: CGFloat) {
        canvas.size = size
        let rect = CGSize(width: 28, height: 28)
        UIGraphicsBeginImageContextWithOptions(rect, false, UIScreen.main.scale)
        UIBezierPath(arcCenter: CGPoint(x: rect.width / 2, y: rect.height / 2), radius: canvas.size / 2, startAngle: 0, endAngle: CGFloat(Double.pi * 2), clockwise: true).fill()
        sizeButton.image = UIGraphicsGetImageFromCurrentImageContext()
        sizeButton.tintColor = canvas.color
        UIGraphicsEndImageContext()
    }

    @IBAction func didSelectEraser(_ sender: Any) {
        canvas.color = ERASER_COLOR
        sizeButton.tintColor = ERASER_COLOR
        colorButton.tintColor = ERASER_COLOR
    }

    func colorPicker(didSelect color: UIColor) {
        canvas.color = color
        sizeButton.tintColor = color
        colorButton.tintColor = color
    }
}

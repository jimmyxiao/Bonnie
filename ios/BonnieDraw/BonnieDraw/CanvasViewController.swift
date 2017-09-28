//
//  CanvasViewController.swift
//  BonnieDraw
//
//  Created by Professor on 07/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CanvasViewController: BackButtonViewController, UIPopoverPresentationControllerDelegate, CanvasViewDelegate, SizePickerViewControllerDelegate, ColorPickerViewControllerDelegate {
    @IBOutlet weak var canvas: CanvasView!
    @IBOutlet weak var undo: UIBarButtonItem!
    @IBOutlet weak var redo: UIBarButtonItem!
    @IBOutlet weak var play: UIBarButtonItem!
    @IBOutlet weak var sizeButton: UIBarButtonItem!
    @IBOutlet weak var pen: UIButton!
    @IBOutlet weak var reset: UIBarButtonItem!
    @IBOutlet weak var colorButton: UIBarButtonItem!

    override func viewDidLoad() {
        canvas.delegate = self
        pen.layer.cornerRadius = view.bounds.width / 10
        let rect = CGSize(width: 28, height: 28)
        UIGraphicsBeginImageContextWithOptions(rect, false, UIScreen.main.scale)
        UIBezierPath(arcCenter: CGPoint(x: rect.width / 2, y: rect.height / 2), radius: canvas.size / 2, startAngle: 0, endAngle: CGFloat(Double.pi * 2), clockwise: true).fill()
        sizeButton.image = UIGraphicsGetImageFromCurrentImageContext()
        sizeButton.tintColor = canvas.color
        UIGraphicsGetCurrentContext()?.clear(CGRect(origin: .zero, size: rect))
        UIBezierPath(roundedRect: CGRect(origin: .zero, size: rect), cornerRadius: 4).fill()
        colorButton.image = UIGraphicsGetImageFromCurrentImageContext()
        colorButton.tintColor = canvas.color
        UIGraphicsEndImageContext()
    }

    override func viewWillAppear(_ animated: Bool) {
        UIApplication.shared.statusBarStyle = .lightContent
    }

    override func viewWillDisappear(_ animated: Bool) {
        UIApplication.shared.statusBarStyle = .default
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
        if let controller = segue.destination as? SizePickerViewController {
            controller.delegate = self
            controller.popoverPresentationController?.delegate = self
            let height = CGFloat(controller.sizes.count * 44)
            let maxHeight = view.bounds.height - 110
            controller.preferredContentSize = CGSize(width: 44, height: height > maxHeight ? maxHeight : height)
        } else if let controller = segue.destination as? ColorPickerViewController {
            controller.delegate = self
            controller.popoverPresentationController?.delegate = self
            let height = CGFloat(controller.colors.count * 44)
            let maxHeight = view.bounds.height - 110
            controller.preferredContentSize = CGSize(width: 44, height: height > maxHeight ? maxHeight : height)
        }
    }

    func adaptivePresentationStyle(for controller: UIPresentationController) -> UIModalPresentationStyle {
        return .none
    }

    func canvasPathsDidChange() {
        undo.isEnabled = !canvas.paths.isEmpty
        redo.isEnabled = !canvas.redoPaths.isEmpty
        play.isEnabled = !canvas.paths.isEmpty
        reset.isEnabled = !canvas.paths.isEmpty
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

    func colorPicker(didSelect color: UIColor) {
        canvas.color = color
        sizeButton.tintColor = color
        colorButton.tintColor = color
    }
}

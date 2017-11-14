//
//  ColorPickerViewController.swift
//  BonnieDraw
//
//  Created by Professor on 27/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class ColorPickerViewController: UIViewController, SaturationBrightnessViewDelegate, HueViewDelegate {
    var delegate: ColorPickerViewControllerDelegate?
    @IBOutlet weak var colorView: UIView!
    @IBOutlet weak var saturationBrightnessView: SaturationBrightnessView!
    @IBOutlet weak var hueView: HueView!
    @IBOutlet weak var collectionView: UICollectionView!
    var color: UIColor?

    override func viewDidLoad() {
        saturationBrightnessView.delegate = self
        hueView.delegate = self
        if let color = color {
            colorView.backgroundColor = color
            saturationBrightnessView.set(color: color)
            hueView.set(color: color)
        }
    }

    func saturationBrightness(didSelectColor color: UIColor) {
        colorView.backgroundColor = color
        delegate?.colorPicker(didSelect: color)
    }

    func hue(didSelectHue hue: CGFloat) {
        saturationBrightnessView.hue = hue
    }

    @IBAction func save(_ sender: Any) {
        view.center = CGPoint(x: view.center.x, y: view.center.y + 100)
    }

    @IBAction func add(_ sender: Any) {
        preferredContentSize = CGSize(
                width: UIScreen.main.bounds.width * (traitCollection.horizontalSizeClass == .compact ? 0.9 : 0.45),
                height: 280 + saturationBrightnessView.bounds.height)
    }

    @IBAction func remove(_ sender: Any) {
        preferredContentSize = CGSize(
                width: UIScreen.main.bounds.width * (traitCollection.horizontalSizeClass == .compact ? 0.9 : 0.45),
                height: 204)
    }
}

protocol ColorPickerViewControllerDelegate {
    func colorPicker(didSelect color: UIColor)
}

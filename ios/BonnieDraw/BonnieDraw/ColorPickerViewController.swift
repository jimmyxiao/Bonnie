//
//  ColorPickerViewController.swift
//  BonnieDraw
//
//  Created by Professor on 27/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class ColorPickerViewController: UIViewController, UITextFieldDelegate, UICollectionViewDataSource, UICollectionViewDelegate, SaturationBrightnessViewDelegate, HueViewDelegate {
    enum ColorType {
        case canvas
        case background
    }

    @IBOutlet weak var colorView: UIButton!
    @IBOutlet weak var hexField: UITextField!
    @IBOutlet weak var saveButton: UIButton!
    @IBOutlet weak var saturationBrightnessView: SaturationBrightnessView!
    @IBOutlet weak var hueView: HueView!
    @IBOutlet weak var collectionView: UICollectionView!
    @IBOutlet weak var stackViewTop: NSLayoutConstraint!
    var delegate: ColorPickerViewControllerDelegate?
    var type: ColorType?
    var color = UIColor.black
    private var colors = UserDefaults.standard.colors(forKey: Default.COLORS) ?? UIColor.getDefaultColors()
    private var isEditingMode = false
    private let colorExpression = try! NSRegularExpression(pattern: "[A-Fa-f0-9]+", options: .caseInsensitive)

    override func viewDidLoad() {
        saveButton.layer.borderColor = UIColor.white.cgColor
        saturationBrightnessView.delegate = self
        hueView.delegate = self
        colorView.backgroundColor = color
        hexField.text = color.toHex()
        saturationBrightnessView.set(color: color)
        hueView.set(color: color)
    }

    override func viewWillDisappear(_ animated: Bool) {
        if type == .background {
            delegate?.colorPicker(didSelect: color, type: type)
        }
    }

    internal func saturationBrightness(didSelectColor color: UIColor) {
        self.color = color
        colorView.backgroundColor = color
        hexField.text = color.toHex()
        if type == .canvas {
            delegate?.colorPicker(didSelect: color, type: type)
        }
    }

    internal func hue(didSelectHue hue: CGFloat) {
        let color = UIColor(hue: hue, saturation: saturationBrightnessView.saturation, brightness: saturationBrightnessView.brightness, alpha: 1)
        self.color = color
        colorView.backgroundColor = color
        hexField.text = color.toHex()
        saturationBrightnessView.set(hue: hue)
        if type == .canvas {
            delegate?.colorPicker(didSelect: color, type: type)
        }
    }

    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        return string.isEmpty || ((textField.text?.count ?? 0) + string.count <= 6 && colorExpression.firstMatch(in: string, options: [], range: NSMakeRange(0, string.count)) != nil)
    }

    @IBAction func textFieldDidChange(_ sender: UITextField) {
        if let string = sender.text,
           string.count == 6,
           let color = UIColor(hex: string) {
            colorView.backgroundColor = color
            saturationBrightnessView.set(color: color)
            hueView.set(color: color)
        }
    }

    internal func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }

    internal func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return colors.count
    }

    internal func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        if let cell = collectionView.dequeueReusableCell(withReuseIdentifier: Cell.COLOR_PICKER, for: indexPath) as? ColorPickerCollectionViewCell {
            cell.backgroundColor = colors[indexPath.row]
            cell.removeIcon.isHidden = !isEditingMode
            return cell
        }
        return UICollectionViewCell()
    }

    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if isEditingMode {
            colors.remove(at: indexPath.row)
            collectionView.deleteItems(at: [indexPath])
            UserDefaults.standard.set(colors: colors, forKey: Default.COLORS)
        } else if let color = collectionView.cellForItem(at: indexPath)?.backgroundColor {
            self.color = color
            if type == .canvas {
                delegate?.colorPicker(didSelect: color, type: type)
            }
            dismiss(animated: true)
        }
    }

    @IBAction func save(_ sender: Any) {
        if let color = colorView.backgroundColor {
            colors.append(color)
        }
        UserDefaults.standard.set(colors: colors, forKey: Default.COLORS)
        let indexPath = IndexPath(row: colors.count - 1, section: 0)
        collectionView.performBatchUpdates({
            collectionView.insertItems(at: [indexPath])
        }) {
            completed in
            self.collectionView.scrollToItem(at: indexPath, at: .centeredHorizontally, animated: true)
        }
    }

    @IBAction func add(_ sender: UIButton) {
        stackViewTop.priority = UILayoutPriority(UILayoutPriority.defaultHigh.rawValue + 1)
        preferredContentSize = CGSize(
                width: UIScreen.main.bounds.width * (traitCollection.horizontalSizeClass == .compact ? 0.9 : 0.45),
                height: 272 + saturationBrightnessView.bounds.height)
        sender.isEnabled = false
    }

    @IBAction func remove(_ sender: Any) {
        isEditingMode = !isEditingMode
        collectionView.reloadSections([0])
    }

    @IBAction func didSelectColorView(_ sender: UIButton) {
        if let color = sender.backgroundColor {
            self.color = color
            if type == .canvas {
                delegate?.colorPicker(didSelect: color, type: type)
            }
            dismiss(animated: true)
        }
    }
}

protocol ColorPickerViewControllerDelegate {
    func colorPicker(didSelect color: UIColor, type: ColorPickerViewController.ColorType?)
}

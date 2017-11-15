//
//  ColorPickerViewController.swift
//  BonnieDraw
//
//  Created by Professor on 27/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class ColorPickerViewController: UIViewController, UITextFieldDelegate, UICollectionViewDataSource, UICollectionViewDelegate, SaturationBrightnessViewDelegate, HueViewDelegate {
    @IBOutlet weak var colorView: UIView!
    @IBOutlet weak var saveButton: UIButton!
    @IBOutlet weak var saturationBrightnessView: SaturationBrightnessView!
    @IBOutlet weak var hueView: HueView!
    @IBOutlet weak var collectionView: UICollectionView!
    var delegate: ColorPickerViewControllerDelegate?
    var color: UIColor?
    private var colors = UserDefaults.standard.colors(forKey: Default.COLORS) ?? [UIColor]()
    private var isEditingMode = false

    override func viewDidLoad() {
        saveButton.layer.borderColor = UIColor.white.cgColor
        saturationBrightnessView.delegate = self
        hueView.delegate = self
        if let color = color {
            colorView.backgroundColor = color
            saturationBrightnessView.set(color: color)
            hueView.set(color: color)
        }
    }

    internal func saturationBrightness(didSelectColor color: UIColor) {
        colorView.backgroundColor = color
        delegate?.colorPicker(didSelect: color)
    }

    internal func hue(didSelectHue hue: CGFloat) {
        saturationBrightnessView.hue = hue
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
        } else if let color = collectionView.cellForItem(at: indexPath)?.backgroundColor {
            delegate?.colorPicker(didSelect: color)
            dismiss(animated: true)
        }
    }

    @IBAction func save(_ sender: Any) {
        if let color = colorView.backgroundColor {
            colors.append(color)
        }
        UserDefaults.standard.set(colors: colors, forKey: Default.COLORS)
        collectionView.insertItems(at: [IndexPath(row: colors.count - 1, section: 0)])
    }

    @IBAction func add(_ sender: Any) {
        preferredContentSize = CGSize(
                width: UIScreen.main.bounds.width * (traitCollection.horizontalSizeClass == .compact ? 0.9 : 0.45),
                height: 280 + saturationBrightnessView.bounds.height)
    }

    @IBAction func remove(_ sender: Any) {
        isEditingMode = !isEditingMode
        collectionView.reloadSections([0])
    }
}

protocol ColorPickerViewControllerDelegate {
    func colorPicker(didSelect color: UIColor)
}

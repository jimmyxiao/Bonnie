//
//  BrushPickerViewController.swift
//  BonnieDraw
//
//  Created by Professor on 28/11/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class BrushPickerViewController: UIViewController, UICollectionViewDataSource, UICollectionViewDelegate {
    @IBOutlet weak var decrease: UIButton!
    @IBOutlet weak var slider: UISlider!
    @IBOutlet weak var increase: UIButton!
    private let brushes = [Brush(type: .pen, imageName: "draw_pen_off_1"),
                           Brush(type: .pencil, imageName: "draw_pen_off_2"),
                           Brush(type: .crayon, imageName: "draw_pen_off_3"),
                           Brush(type: .marker, imageName: "draw_pen_off_4"),
                           Brush(type: .airbrush, imageName: "draw_pen_off_5")]
    var value: Float = 1
    var delegate: BrushPickerViewControllerDelegate?

    override func viewDidLoad() {
        slider.value = value
        checkValue()
    }

    @IBAction func decrease(_ sender: Any) {
        let value = slider.value - 0.1
        slider.setValue(value, animated: true)
        delegate?.brushPicker(didSelect: CGFloat(slider.value))
        checkValue()
    }

    @IBAction func sliderValueChanged(_ sender: UISlider) {
        delegate?.brushPicker(didSelect: CGFloat(sender.value))
        checkValue()
    }

    @IBAction func increase(_ sender: Any) {
        let value = slider.value + 0.1
        slider.setValue(value, animated: true)
        delegate?.brushPicker(didSelect: CGFloat(slider.value))
        checkValue()
    }

    private func checkValue() {
        if slider.value >= slider.maximumValue {
            if increase.isEnabled {
                increase.isEnabled = false
            }
        } else {
            if !increase.isEnabled {
                increase.isEnabled = true
            }
        }
        if slider.value <= slider.minimumValue {
            if decrease.isEnabled {
                decrease.isEnabled = false
            }
        } else {
            if !decrease.isEnabled {
                decrease.isEnabled = true
            }
        }
    }

    internal func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return brushes.count
    }

    internal func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: Cell.BRUSH_PICKER, for: indexPath) as! BrushPickerCollectionViewCell
        cell.imageView.image = UIImage(named: brushes[indexPath.row].imageName)
        return cell
    }

    internal func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        delegate?.brushPicker(didSelect: brushes[indexPath.row].type)
        dismiss(animated: true)
    }

    struct Brush {
        let type: Type
        let imageName: String
    }
}

protocol BrushPickerViewControllerDelegate {
    func brushPicker(didSelect alpha: CGFloat)
    func brushPicker(didSelect type: Type)
}

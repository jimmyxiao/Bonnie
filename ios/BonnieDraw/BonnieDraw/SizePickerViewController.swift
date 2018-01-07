//
//  SizePickerViewController.swift
//  BonnieDraw
//
//  Created by Professor on 28/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import ASValueTrackingSlider

class SizePickerViewController: UIViewController {
    @IBOutlet weak var decrease: UIButton!
    @IBOutlet weak var slider: ASValueTrackingSlider!
    @IBOutlet weak var increase: UIButton!
    var value: Float = 1
    var delegate: SizePickerViewControllerDelegate?

    override func viewDidLoad() {
        slider.popUpViewColor = UIColor.getAccentColor()
        slider.minimumTrackTintColor = .lightGray
        slider.value = value
        checkValue()
    }

    override func viewDidAppear(_ animated: Bool) {
        view.superview?.clipsToBounds = false
    }

    @IBAction func decrease(_ sender: Any) {
        let value = slider.value - 5
        slider.setValue(value, animated: true)
        delegate?.sizePicker(didSelect: CGFloat(slider.value))
        checkValue()
    }

    @IBAction func sliderValueChanged(_ sender: UISlider) {
        delegate?.sizePicker(didSelect: CGFloat(sender.value))
        checkValue()
    }

    @IBAction func increase(_ sender: Any) {
        let value = slider.value + 5
        slider.setValue(value, animated: true)
        delegate?.sizePicker(didSelect: CGFloat(slider.value))
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
}

protocol SizePickerViewControllerDelegate {
    func sizePicker(didSelect size: CGFloat)
}

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
    @IBOutlet weak var stepWidthSlider: UISlider?
    @IBOutlet weak var alphaSlider: UISlider!
    @IBOutlet weak var increase: UIButton!
    private var brushes = [Brush(type: .pen, imageName: "draw_pen_off_1"),
                           Brush(type: .pencil, imageName: "draw_pen_off_2"),
                           Brush(type: .crayon, imageName: "draw_pen_off_3"),
                           Brush(type: .marker, imageName: "draw_pen_off_4"),
                           Brush(type: .airbrush, imageName: "draw_pen_off_5")]
    var stepWidth: Float = 1
    var alpha: Float = 1
    var type: Type?
    var delegate: BrushPickerViewControllerDelegate?

    override func viewDidLoad() {
        stepWidthSlider?.value = stepWidth
        alphaSlider.value = alpha
        switch type {
        case .crayon?:
            brushes[2].imageName = "draw_pen_on_3"
        case .pencil?:
            brushes[1].imageName = "draw_pen_on_2"
        case .pen?:
            brushes[0].imageName = "draw_pen_on_1"
        case .airbrush?:
            brushes[4].imageName = "draw_pen_on_5"
        case .marker?:
            brushes[3].imageName = "draw_pen_on_4"
        default:
            break
        }
        checkValue()
    }

    @IBAction func decrease(_ sender: Any) {
        let value = alphaSlider.value - 0.1
        alphaSlider.setValue(value, animated: true)
        delegate?.brushPicker(didSelectAlpha: CGFloat(alphaSlider.value))
        checkValue()
    }

    @IBAction func stepWidthSliderValueChanged(_ sender: UISlider) {
        delegate?.brushPicker(didSelectStepWidth: CGFloat(sender.value))
    }

    @IBAction func alphaSliderValueChanged(_ sender: UISlider) {
        delegate?.brushPicker(didSelectAlpha: CGFloat(sender.value))
        checkValue()
    }

    @IBAction func increase(_ sender: Any) {
        let value = alphaSlider.value + 0.1
        alphaSlider.setValue(value, animated: true)
        delegate?.brushPicker(didSelectAlpha: CGFloat(alphaSlider.value))
        checkValue()
    }

    private func checkValue() {
        if alphaSlider.value >= alphaSlider.maximumValue {
            if increase.isEnabled {
                increase.isEnabled = false
            }
        } else {
            if !increase.isEnabled {
                increase.isEnabled = true
            }
        }
        if alphaSlider.value <= alphaSlider.minimumValue {
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
        delegate?.brushPicker(didSelectType: brushes[indexPath.row].type)
        dismiss(animated: true)
    }

    struct Brush {
        let type: Type
        var imageName: String
    }
}

protocol BrushPickerViewControllerDelegate {
    func brushPicker(didSelectStepWidth delta: CGFloat)
    func brushPicker(didSelectAlpha alpha: CGFloat)
    func brushPicker(didSelectType type: Type)
}

//
//  ColorPickerViewController.swift
//  BonnieDraw
//
//  Created by Professor on 27/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class ColorPickerViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    var delegate: ColorPickerViewControllerDelegate?
    let colors: [UIColor] = [.black, .darkGray, .lightGray, .gray, .red, .blue, .green, .cyan, .yellow, .magenta, .orange, .purple, .brown]

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return colors.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: Cell.COLOR_PICKER, for: indexPath)
        cell.backgroundColor = colors[indexPath.row]
        return cell
    }

    func tableView(_ tableView: UITableView, willSelectRowAt indexPath: IndexPath) -> IndexPath? {
        tableView.cellForRow(at: indexPath)?.alpha = 0.5
        return indexPath
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        delegate?.colorPicker(didSelect: colors[indexPath.row])
        DispatchQueue.main.async {
            self.dismiss(animated: true)
        }
    }
}

protocol ColorPickerViewControllerDelegate {
    func colorPicker(didSelect color: UIColor)
}

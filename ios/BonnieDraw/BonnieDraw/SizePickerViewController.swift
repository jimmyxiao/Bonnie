//
//  SizePickerViewController.swift
//  BonnieDraw
//
//  Created by Professor on 28/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class SizePickerViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    var delegate: SizePickerViewControllerDelegate?
    let sizes: [CGFloat] = [4, 10, 16, 22, 28]

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return sizes.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: Cell.SIZE_PICKER, for: indexPath) as! SizePickerTableViewCell
        cell.radius = sizes[indexPath.row] / 2
        return cell
    }

    func tableView(_ tableView: UITableView, willSelectRowAt indexPath: IndexPath) -> IndexPath? {
        tableView.cellForRow(at: indexPath)?.alpha = 0.5
        return indexPath
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        delegate?.sizePicker(didSelect: sizes[indexPath.row])
        DispatchQueue.main.async {
            self.dismiss(animated: true)
        }
    }
}

protocol SizePickerViewControllerDelegate {
    func sizePicker(didSelect size: CGFloat)
}

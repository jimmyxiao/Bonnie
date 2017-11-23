//
//  CanvasSettingTableViewController.swift
//  BonnieDraw
//
//  Created by Professor on 23/11/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CanvasSettingTableViewController: UITableViewController {
    var delegate: CanvasSettingTableViewControllerDelegate?

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        DispatchQueue.main.async {
            self.dismiss(animated: true) {
                self.delegate?.canvasSetting(didSelectRowAt: indexPath)
            }
        }
    }
}

protocol CanvasSettingTableViewControllerDelegate {
    func canvasSetting(didSelectRowAt indexPath: IndexPath)
}

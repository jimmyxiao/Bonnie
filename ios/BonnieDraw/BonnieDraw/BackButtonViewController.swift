//
//  BackButtonViewController.swift
//  Omna
//
//  Created by Jason Hsu 08329 on 10/17/16.
//  Copyright © 2016 D-Link. All rights reserved.
//

import UIKit

class BackButtonViewController: UIViewController {
    @IBAction func onBackPressed(_ sender: AnyObject) {
        if let navigationController = navigationController {
            if navigationController.popViewController(animated: true) == nil {
                navigationController.dismiss(animated: true)
            }
        } else {
            dismiss(animated: true)
        }
    }
}

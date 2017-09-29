//
//  ViewControllerExtension.swift
//  Omna
//
//  Created by Jason Hsu 08329 on 10/24/16.
//  Copyright © 2016 D-Link. All rights reserved.
//

import HomeKit
import Photos
import UserNotifications

extension UIViewController {
    func presentDialog(title: String? = nil, message: String? = nil, buttonText: String? = nil, handler: ((UIAlertAction) -> Void)? = nil) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: buttonText ?? "alert_button_confirm".localized, style: .cancel, handler: handler))
        alert.view.tintColor = UIColor.getAccentColor()
        present(alert, animated: true)
    }

    func presentConfirmationDialog(title: String? = nil, message: String? = nil, positiveTitle: String? = nil, negativeTitle: String? = nil, handler: @escaping ((Bool) -> Void)) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: positiveTitle ?? "alert_button_confirm".localized, style: .default) {
            action in
            handler(true)
        })
        alert.addAction(UIAlertAction(title: negativeTitle ?? "alert_button_cancel".localized, style: .cancel) {
            action in
            handler(false)
        })
        alert.view.tintColor = UIColor.getAccentColor()
        present(alert, animated: true)
    }
}

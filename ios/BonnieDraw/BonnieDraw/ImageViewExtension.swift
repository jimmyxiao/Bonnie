//
//  ImageViewExtension.swift
//  Native
//
//  Created by Professor on 19/06/2017.
//  Copyright © 2017 D-LINK. All rights reserved.
//

import UIKit
import SDWebImage

extension UIImageView {
    func setImage(with url: URL?, completion: ((CGSize?) -> Void)? = nil) {
        sd_setImage(with: url, placeholderImage: nil, options: [.allowInvalidSSLCertificates, .retryFailed]) {
            image, error, type, url in
            if let error = error {
                Logger.p("\(#function): \(error.localizedDescription)")
            } else if type == .none {
                self.alpha = 0
                UIView.animate(withDuration: 0.4) {
                    self.alpha = 1
                }
            }
            completion?(image?.size)
        }
    }
}

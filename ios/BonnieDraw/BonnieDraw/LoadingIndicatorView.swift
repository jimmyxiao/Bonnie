//
//  LoadingIndicatorView.swift
//  Omna
//
//  Created by Jason Hsu 08329 on 2/17/17.
//  Copyright © 2017 D-Link. All rights reserved.
//

import UIKit

class LoadingIndicatorView: UIView {
    override var isHidden: Bool {
        didSet {
            if isHidden {
                indicator.stopAnimating()
            } else {
                indicator.startAnimating()
            }
        }
    }
    var indicator = UIActivityIndicatorView()

    override init(frame: CGRect) {
        super.init(frame: frame)
        addIndicator()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        addIndicator()
    }

    func addIndicator() {
        indicator.activityIndicatorViewStyle = .whiteLarge
        indicator.color = UIColor.darkGray
        addAndCenter(subView: indicator)
    }

    func hide(_ hide: Bool) {
        if hide {
            UIView.animate(withDuration: 0.4, animations: {
                self.alpha = 0
            }) {
                finished in
                self.isHidden = hide
            }
        } else {
            isHidden = hide
            UIView.animate(withDuration: 0.4) {
                self.alpha = 1
            }
        }
    }
}

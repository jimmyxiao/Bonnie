//
//  TextFieldExtension.swift
//  BonnieDraw
//
//  Created by Professor on 18/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

extension UITextField {
    func addInputAccessoryView() {
        let toolbar = UIToolbar()
        toolbar.items = [UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil),
                         UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(resignFirstResponder))]
        toolbar.sizeToFit()
        inputAccessoryView = toolbar
    }
}

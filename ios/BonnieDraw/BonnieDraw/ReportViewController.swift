//
//  ReportViewController.swift
//  BonnieDraw
//
//  Created by Professor on 27/12/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import DropDown

class ReportViewController: UIViewController, UITextFieldDelegate {
    @IBOutlet weak var alertBox: UIVisualEffectView!
    @IBOutlet weak var reportType: UILabel!
    @IBOutlet weak var reportContent: UITextField!
    private let dropDownItems: [(type: ReportType, title: String)] = [(.sexual, "report_sexual".localized),
                                                                      (.violence, "report_violence".localized),
                                                                      (.other, "report_other".localized)]
    private var viewOriginY: CGFloat = 0
    private var keyboardOnScreen = false
    let dropDown = DropDown()
    var type = ReportType.sexual
    var work: Work?

    override func viewDidLoad() {
        dropDown.dataSource = dropDownItems.map() {
            item in
            return item.title
        }
        dropDown.selectRow(at: 0)
        dropDown.selectionAction = {
            index, text in
            self.reportType.text = text
            self.type = self.dropDownItems[index].type
        }
    }

    override func viewWillAppear(_ animated: Bool) {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: .UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidShow), name: .UIKeyboardDidShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: .UIKeyboardWillHide, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidHide), name: .UIKeyboardDidHide, object: nil)
    }

    override func viewDidAppear(_ animated: Bool) {
        alertBox.transform = CGAffineTransform(scaleX: 1.2, y: 1.2)
        UIView.animate(withDuration: 0.3) {
            self.alertBox.alpha = 1
            self.alertBox.transform = CGAffineTransform(scaleX: 1, y: 1)
        }
    }

    override func viewDidDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self)
    }

    @objc func keyboardWillShow(_ notification: Notification) {
        if !keyboardOnScreen, let keyboardSize = (notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue.size {
            viewOriginY = view.frame.origin.y
            view.frame.origin.y -= keyboardSize.height / 3
        }
    }

    @objc func keyboardWillHide(_ notification: Notification) {
        if keyboardOnScreen {
            view.frame.origin.y = viewOriginY
        }
    }

    @objc func keyboardDidShow(_ notification: Notification) {
        keyboardOnScreen = true
    }

    @objc func keyboardDidHide(_ notification: Notification) {
        keyboardOnScreen = false
    }

    internal func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }

    @IBAction func reportType(_ sender: UIButton) {
        dropDown.anchorView = sender.superview
        dropDown.show()
    }

    @IBAction func cancel(_ sender: Any) {
        UIView.animate(withDuration: 0.4, animations: {
            self.view.alpha = 0
        }) {
            finished in
            self.dismiss(animated: false)
        }
    }

    @IBAction func confirm(_ sender: Any) {
        cancel(sender)
    }
}

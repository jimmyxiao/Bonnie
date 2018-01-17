//
//  EditViewController.swift
//  BonnieDraw
//
//  Created by Professor on 05/01/2018.
//  Copyright © 2018 Professor. All rights reserved.
//

import UIKit
import Alamofire
import DropDown

class EditViewController: UIViewController, UITextFieldDelegate, UITextViewDelegate {
    @IBOutlet weak var alertBox: UIVisualEffectView!
    @IBOutlet weak var indicator: UIActivityIndicatorView!
    @IBOutlet weak var thumbnail: UIImageView!
    @IBOutlet weak var workTitle: UITextField!
    @IBOutlet weak var workDescription: UITextView!
    @IBOutlet weak var accessLabel: UILabel!
    @IBOutlet weak var confirmButton: UIButton!
    private var keyboardOnScreen = false
    private var viewOriginY: CGFloat = 0
    private var dataRequest: DataRequest?
    private let dropDownItems: [(access: AccessControl, title: String)] = [(.publicAccess, "access_control_public".localized),
                                                                           (.contactAccess, "access_control_contact".localized),
                                                                           (.privateAccess, "access_control_private".localized)]
    var delegate: EditViewControllerDelegate?
    let dropDown = DropDown()
    var work: Work?

    override func viewDidLoad() {
        thumbnail.sd_setShowActivityIndicatorView(true)
        thumbnail.sd_setIndicatorStyle(.gray)
        thumbnail.setImage(with: work?.thumbnail)
        workTitle.text = work?.title
        workDescription.text = work?.summery
        dropDown.dataSource = dropDownItems.map() {
            item in
            return item.title
        }
        dropDown.selectRow(at: 0)
        dropDown.selectionAction = {
            index, text in
            self.accessLabel.text = text
            self.work?.accessControl = self.dropDownItems[index].access
            self.confirmButton.isEnabled = true
        }
    }

    override func viewWillAppear(_ animated: Bool) {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: .UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: .UIKeyboardWillHide, object: nil)
    }

    override func viewDidAppear(_ animated: Bool) {
        alertBox.transform = CGAffineTransform(scaleX: 1.2, y: 1.2)
        UIView.animate(withDuration: 0.3) {
            self.alertBox.alpha = 1
            self.alertBox.transform = CGAffineTransform(scaleX: 1, y: 1)
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
    }

    override func viewDidDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self)
    }

    @objc func keyboardWillShow(_ notification: Notification) {
        if !keyboardOnScreen, let keyboardSize = (notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue.size {
            keyboardOnScreen = true
            viewOriginY = view.frame.origin.y
            view.frame.origin.y -= keyboardSize.height / 3
        }
    }

    @objc func keyboardWillHide(_ notification: Notification) {
        if keyboardOnScreen {
            keyboardOnScreen = false
            view.frame.origin.y = viewOriginY
        }
    }

    @IBAction func textFieldTextDidChange(_ sender: UITextField) {
        work?.title = sender.text
        confirmButton.isEnabled = true
    }

    private func showErrorMessage(message: String?) {
        presentDialog(title: "alert_edit_fail_title".localized, message: message)
        confirmButton.isEnabled = true
        indicator.stopAnimating()
    }

    @IBAction func accessControl(_ sender: UIButton) {
        dropDown.anchorView = sender.superview
        dropDown.show()
    }

    @IBAction func cancel(_ sender: Any) {
        UIView.animate(withDuration: 0.3, animations: {
            self.view.alpha = 0
        }) {
            finished in
            self.dismiss(animated: false)
        }
    }

    @IBAction func confirm(_ sender: UIButton) {
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
            return
        }
        guard let token = UserDefaults.standard.string(forKey: Default.TOKEN) else {
            return
        }
        let userId = UserDefaults.standard.integer(forKey: Default.USER_ID)
        let description = self.workDescription.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let title = self.workTitle.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        if description.isEmpty {
            presentDialog(title: "alert_edit_fail_title".localized, message: "alert_save_fail_description_empty".localized) {
                action in
                self.workDescription.becomeFirstResponder()
            }
        } else if title.isEmpty {
            presentDialog(title: "alert_edit_fail_title".localized, message: "alert_save_fail_name_empty".localized) {
                action in
                self.workTitle.becomeFirstResponder()
            }
        } else {
            sender.isEnabled = false
            indicator.startAnimating()
            dataRequest = Alamofire.request(
                    Service.standard(withPath: Service.WORK_SAVE),
                    method: .post,
                    parameters: ["ui": userId,
                                 "lk": token,
                                 "dt": SERVICE_DEVICE_TYPE,
                                 "ac": 2,
                                 "privacyType": work?.accessControl?.rawValue ?? 0,
                                 "title": title,
                                 "description": description,
                                 "worksId": work?.id ?? 0],
                    encoding: JSONEncoding.default).validate().responseJSON {
                response in
                switch response.result {
                case .success:
                    guard let data = response.result.value as? [String: Any], let response = data["res"] as? Int else {
                        self.showErrorMessage(message: "app_network_unreachable_content".localized)
                        return
                    }
                    if response != 1 {
                        self.showErrorMessage(message: data["msg"] as? String)
                    } else {
                        self.cancel(sender)
                        if let work = self.work {
                            self.delegate?.edit(didChange: work)
                        }
                    }
                case .failure(let error):
                    if let error = error as? URLError, error.code == .cancelled {
                        return
                    }
                    self.showErrorMessage(message: error.localizedDescription)
                }
            }
        }
    }

    @IBAction func didTapBackground(_ sender: Any) {
        view.endEditing(false)
    }

    internal func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }

    internal func textViewDidChange(_ textView: UITextView) {
        work?.summery = textView.text
        confirmButton.isEnabled = true
    }
}

protocol EditViewControllerDelegate {
    func edit(didChange work: Work)
}

//
//  ReportViewController.swift
//  BonnieDraw
//
//  Created by Professor on 27/12/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import DropDown
import Alamofire

class ReportViewController: UIViewController, UITextFieldDelegate {
    @IBOutlet weak var alertBox: UIVisualEffectView!
    @IBOutlet weak var reportType: UILabel!
    @IBOutlet weak var reportContent: UITextField!
    @IBOutlet weak var indicator: UIActivityIndicatorView!
    private let dropDownItems: [(type: ReportType, title: String)] = [(.sexual, "report_sexual".localized),
                                                                      (.violence, "report_violence".localized),
                                                                      (.other, "report_other".localized)]
    private var viewOriginCenterY: CGFloat?
    private var dataRequest: DataRequest?
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
        view.endEditing(true)
    }

    override func viewDidDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self)
    }

    @objc func keyboardWillShow(_ notification: Notification) {
        if let keyboardSize = (notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue.size,
           let animationDuration = notification.userInfo?[UIKeyboardAnimationDurationUserInfoKey] as? NSNumber,
           let animationCurve = notification.userInfo?[UIKeyboardAnimationCurveUserInfoKey] as? NSNumber {
            if viewOriginCenterY == nil {
                viewOriginCenterY = view.center.y
            }
            UIView.animate(withDuration: animationDuration.doubleValue, delay: 0, options: [UIViewAnimationOptions(rawValue: UInt(animationCurve.intValue))], animations: {
                if let viewOriginCenterY = self.viewOriginCenterY {
                    self.view.center.y = viewOriginCenterY - keyboardSize.height / 3
                }
            })
        }
    }

    @objc func keyboardWillHide(_ notification: Notification) {
        if let animationDuration = notification.userInfo?[UIKeyboardAnimationDurationUserInfoKey] as? NSNumber,
           let animationCurve = notification.userInfo?[UIKeyboardAnimationCurveUserInfoKey] as? NSNumber {
            UIView.animate(withDuration: animationDuration.doubleValue, delay: 0, options: [UIViewAnimationOptions(rawValue: UInt(animationCurve.intValue))], animations: {
                if let viewOriginCenterY = self.viewOriginCenterY {
                    self.view.center.y = viewOriginCenterY
                }
            })
        }
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
        UIView.animate(withDuration: 0.3, animations: {
            self.view.alpha = 0
        }) {
            finished in
            self.dismiss(animated: false)
        }
    }

    @IBAction func confirm(_ sender: UIButton) {
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "alert_network_unreachable_title".localized, message: "alert_network_unreachable_content".localized)
            return
        }
        guard let id = work?.id,
              let token = UserDefaults.standard.string(forKey: Defaults.TOKEN) else {
            return
        }
        let description = reportContent.text ?? ""
        if description.isEmpty {
            presentDialog(title: "alert_report_fail_title".localized, message: "alert_report_fail_description_empty".localized) {
                action in
                self.reportContent.becomeFirstResponder()
            }
        } else {
            sender.isEnabled = false
            indicator.startAnimating()
            dataRequest = Alamofire.request(
                    Service.standard(withPath: Service.SET_TURN_IN),
                    method: .post,
                    parameters: ["ui": UserDefaults.standard.integer(forKey: Defaults.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE, "worksId": id, "turnInType": type.rawValue, "description": description],
                    encoding: JSONEncoding.default).validate().responseJSON {
                response in
                switch response.result {
                case .success:
                    guard let data = response.result.value as? [String: Any], let response = data["res"] as? Int else {
                        self.presentDialog(title: "alert_report_fail_title".localized, message: "alert_network_unreachable_content".localized)
                        self.indicator.stopAnimating()
                        sender.isEnabled = true
                        return
                    }
                    if response == 1 {
                        UIView.animate(withDuration: 0.3, animations: {
                            self.view.alpha = 0
                        }) {
                            finished in
                            self.dismiss(animated: false)
                        }
                    } else {
                        self.presentDialog(title: "alert_report_fail_title".localized, message: data["msg"] as? String)
                        self.indicator.stopAnimating()
                        sender.isEnabled = true
                    }
                case .failure(let error):
                    if let error = error as? URLError, error.code == .cancelled {
                        return
                    }
                    self.presentDialog(title: "alert_report_fail_title".localized, message: error.localizedDescription)
                    self.indicator.stopAnimating()
                    sender.isEnabled = true
                }
            }
        }
    }
}

//
//  CommentViewController.swift
//  BonnieDraw
//
//  Created by Professor on 07/12/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class CommentViewController: BackButtonViewController, UITableViewDataSource, UITableViewDelegate, UITextFieldDelegate {
    @IBOutlet weak var navigationBar: UINavigationBar!
    @IBOutlet weak var emptyLabel: UILabel!
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var textField: UITextField!
    @IBOutlet weak var send: UIButton!
    @IBOutlet weak var viewBottom: NSLayoutConstraint!
    private let formatter = DateFormatter()
    private var dataRequest: DataRequest?
    private var timestamp = Date()
    private let refreshControl = UIRefreshControl()
    private let indicator = UIActivityIndicatorView(activityIndicatorStyle: .gray)
    private var keyboardOnScreen = false
    var work: Work?

    override func viewDidLoad() {
        navigationBar.items?.first?.rightBarButtonItem = UIBarButtonItem(customView: indicator)
        formatter.dateFormat = "yyyy/MM/dd"
        if work?.messages.isEmpty ?? true {
            emptyLabel.isHidden = false
        }
        tableView.refreshControl = refreshControl
    }

    override func viewWillAppear(_ animated: Bool) {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: .UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidShow), name: .UIKeyboardDidShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: .UIKeyboardWillHide, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidHide), name: .UIKeyboardDidHide, object: nil)
    }

    @objc func keyboardWillShow(_ notification: Notification) {
        if !keyboardOnScreen, let keyboardSize = (notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue.size {
            viewBottom.constant = -keyboardSize.height
            UIView.animate(withDuration: 0.4) {
                self.view.setNeedsDisplay()
            }
        }
    }

    @objc func keyboardWillHide(_ notification: Notification) {
        if keyboardOnScreen {
            viewBottom.constant = 0
            UIView.animate(withDuration: 0.4) {
                self.view.setNeedsDisplay()
            }
        }
    }

    @objc func keyboardDidShow(_ notification: Notification) {
        keyboardOnScreen = true
    }

    @objc func keyboardDidHide(_ notification: Notification) {
        keyboardOnScreen = false
    }

    override func viewDidAppear(_ animated: Bool) {
        if work?.messages.isEmpty ?? true {
            downloadData()
        } else if Date().timeIntervalSince1970 - timestamp.timeIntervalSince1970 > UPDATE_INTERVAL {
            downloadData()
        } else {
            emptyLabel.isHidden = true
            loading.hide(true)
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
    }

    override func viewDidDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self)
    }

    private func downloadData() {
        guard AppDelegate.reachability.connection != .none else {
            presentConfirmationDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized) {
                success in
                if success {
                    self.downloadData()
                }
            }
            return
        }
        dataRequest?.cancel()
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.WORK_LIST),
                method: .post,
                parameters: ["ui": UserDefaults.standard.integer(forKey: Default.USER_ID) ?? "", "lk": UserDefaults.standard.string(forKey: Default.TOKEN) ?? "", "dt": SERVICE_DEVICE_TYPE, "wid": work?.id ?? 0],
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], data["res"] as? Int == 1, let work = data["work"] as? [String: Any] else {
                    self.presentConfirmationDialog(
                            title: "service_download_fail_title".localized,
                            message: "app_network_unreachable_content".localized) {
                        success in
                        if success {
                            self.downloadData()
                        }
                    }
                    return
                }
                var messageList = [Message]()
                if let messages = work["msgList"] as? [[String: Any]] {
                    for message in messages {
                        messageList.append(Message(withDictionary: message))
                    }
                }
                self.work?.messages = messageList
                self.tableView.reloadSections([0], with: .automatic)
                self.emptyLabel.isHidden = !(self.work?.messages.isEmpty ?? true)
                if !self.loading.isHidden {
                    self.loading.hide(true)
                }
                self.timestamp = Date()
                self.refreshControl.endRefreshing()
                self.textField.isEnabled = true
                self.indicator.stopAnimating()
            case .failure(let error):
                if let error = error as? URLError, error.code == .cancelled {
                    return
                }
                self.presentConfirmationDialog(
                        title: "service_download_fail_title".localized,
                        message: error.localizedDescription) {
                    success in
                    if success {
                        self.downloadData()
                    }
                }
            }
        }
    }

    internal func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return work?.messages.count ?? 0
    }

    internal func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let message = work?.messages[indexPath.row]
        let cell = tableView.dequeueReusableCell(withIdentifier: Cell.COMMENT, for: indexPath) as! CommentTableViewCell
        cell.profileName.text = message?.userName
        cell.message.text = message?.message
        if let date = message?.date {
            cell.date.text = formatter.string(from: date)
        }
        return cell
    }

    internal func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        if refreshControl.isRefreshing {
            downloadData()
        }
    }

    @IBAction func textFieldTextDidChange(_ sender: UITextField) {
        send.isEnabled = !(sender.text?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ?? true)
    }

    internal func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }

    @IBAction func send(_ sender: UIButton) {
        textField.resignFirstResponder()
        guard let text = textField.text?.trimmingCharacters(in: .whitespacesAndNewlines) else {
            return
        }
        guard AppDelegate.reachability.connection != .none else {
            presentConfirmationDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized) {
                success in
                if success {
                    self.downloadData()
                }
            }
            return
        }
        textField.text = nil
        textField.isEnabled = false
        sender.isEnabled = false
        indicator.startAnimating()
        dataRequest?.cancel()
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.LEAVE_MESSAGE),
                method: .post,
                parameters: ["ui": UserDefaults.standard.integer(forKey: Default.USER_ID) ?? "", "lk": UserDefaults.standard.string(forKey: Default.TOKEN) ?? "", "dt": SERVICE_DEVICE_TYPE, "fn": 1, "worksId": work?.id ?? 0, "message": text],
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], let response = data["res"] as? Int else {
                    self.presentDialog(title: "service_leave_message_fail_title".localized, message: "app_network_unreachable_content".localized)
                    return
                }
                if response == 1 {
                    self.downloadData()
                } else {
                    self.textField.isEnabled = true
                    self.indicator.stopAnimating()
                    self.presentDialog(title: "service_leave_message_fail_title".localized, message: data["msg"] as? String)
                }
            case .failure(let error):
                if let error = error as? URLError, error.code == .cancelled {
                    return
                }
                self.textField.isEnabled = true
                self.indicator.stopAnimating()
                self.presentDialog(title: "service_leave_message_fail_title".localized, message: "app_network_unreachable_content".localized)
            }
        }
    }
}

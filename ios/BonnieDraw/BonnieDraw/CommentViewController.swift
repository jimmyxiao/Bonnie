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
    @IBOutlet weak var emptyLabel: UILabel!
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var textField: UITextField!
    @IBOutlet weak var send: UIButton!
    private let formatter = DateFormatter()
    private var dataRequest: DataRequest?
    private let refreshControl = UIRefreshControl()
    var work: Work?

    override func viewDidLoad() {
        formatter.dateFormat = "yyyy/MM/dd"
        if work?.messages.isEmpty ?? true {
            emptyLabel.isHidden = false
        }
        tableView.refreshControl = refreshControl
    }

    override func viewDidAppear(_ animated: Bool) {
        if work?.messages.isEmpty ?? true {
            downloadData()
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
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
                parameters: ["ui": UserDefaults.standard.string(forKey: Default.USER_ID) ?? "", "lk": UserDefaults.standard.string(forKey: Default.TOKEN) ?? "", "dt": SERVICE_DEVICE_TYPE, "wid": work?.id ?? 0],
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
                        var date: Date? = nil
                        if let milliseconds = message["creationDate"] as? Int {
                            date = Date(timeIntervalSince1970: Double(milliseconds) / 1000)
                        }
                        messageList.append(Message(id: message["worksMsgId"] as? Int,
                                userId: message["userId"] as? Int,
                                message: message["message"] as? String,
                                date: date,
                                userName: message["userName"] as? String,
                                userProfile: URL(string: Service.filePath(withSubPath: message["profilePicture"] as? String))))
                    }
                }
                self.work?.messages = messageList
                self.tableView.reloadSections([0], with: .automatic)
                self.emptyLabel.isHidden = !(self.work?.messages.isEmpty ?? true)
                if !self.loading.isHidden {
                    self.loading.hide(true)
                }
                self.refreshControl.endRefreshing()
                self.textField.isEnabled = true
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

    func textFieldDidEndEditing(_ textField: UITextField) {
        Logger.d("\(#function)")
    }

    @IBAction func send(_ sender: UIButton) {
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
        dataRequest?.cancel()
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.LEAVE_MESSAGE),
                method: .post,
                parameters: ["ui": UserDefaults.standard.string(forKey: Default.USER_ID) ?? "", "lk": UserDefaults.standard.string(forKey: Default.TOKEN) ?? "", "dt": SERVICE_DEVICE_TYPE, "fn": 1, "worksId": work?.id ?? 0, "message": text],
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
                    self.presentDialog(title: "service_leave_message_fail_title".localized, message: data["msg"] as? String)
                }
            case .failure(let error):
                if let error = error as? URLError, error.code == .cancelled {
                    return
                }
                self.textField.isEnabled = true
                self.presentDialog(title: "service_leave_message_fail_title".localized, message: "app_network_unreachable_content".localized)
            }
        }
    }
}

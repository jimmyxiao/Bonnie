//
//  NotificationViewController.swift
//  
//
//  Created by Professor on 27/09/2017.
//

import UIKit
import Alamofire

class NotificationViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    @IBOutlet weak var emptyLabel: UILabel!
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var tableView: UITableView!
    private let refreshControl = UIRefreshControl()
    private var dataRequest: DataRequest?
    private var timestamp = Date()
    private var notifications = [Notification]()
    private let dateFormatter = DateFormatter()
    private let placeholderImage = UIImage(named: "photo-square")

    override func viewDidLoad() {
        tableView.refreshControl = refreshControl
        dateFormatter.dateFormat = "yyyy-MM-dd"
    }

    override func viewDidAppear(_ animated: Bool) {
        if notifications.isEmpty {
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
        guard let token = UserDefaults.standard.string(forKey: Default.TOKEN) else {
            return
        }
        dataRequest?.cancel()
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.NOTIFICATION),
                method: .post,
                parameters: ["ui": UserDefaults.standard.integer(forKey: Default.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE],
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], data["res"] as? Int == 1, let notificationList = data["notiMsgList"] as? [[String: Any]] else {
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
                self.notifications.removeAll()
                for notification in notificationList {
                    self.notifications.append(Notification(
                            id: notification["notiMsgId"] as? Int,
                            type: NotificationType(rawValue: notification["notiMsgType"] as? Int ?? 0),
                            profileImage: URL(string: Service.filePath(withSubPath: notification["profilePicture"] as? String)),
                            profileName: notification["userNameFollow"] as? String,
                            date: self.dateFormatter.date(from: (notification["creationDate"] as? String) ?? ""),
                            thumbnail: URL(string: Service.filePath(withSubPath: notification["imagePath"] as? String))))
                }
                self.tableView.reloadSections([0], with: .automatic)
                self.emptyLabel.isHidden = !self.notifications.isEmpty
                if !self.loading.isHidden {
                    self.loading.hide(true)
                }
                self.timestamp = Date()
                self.refreshControl.endRefreshing()
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

    internal func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        if refreshControl.isRefreshing {
            downloadData()
        }
    }

    internal func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return notifications.count
    }

    internal func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let notification = notifications[indexPath.row]
        let cell = tableView.dequeueReusableCell(withIdentifier: Cell.NOTIFICATION, for: indexPath) as! NotificationTableViewCell
        cell.profileImage.setImage(with: notification.profileImage, placeholderImage: placeholderImage)
        cell.profileName.text = notification.profileName
        if let date = notification.date {
            cell.date.text = dateFormatter.string(from: date)
        }
        if let type = notification.type {
            switch type {
            case .followed:
                cell.message.text = "notification_user_followed".localized
            case .joined:
                break
            case .commented:
                cell.message.text = "notification_user_commented".localized
            case .messaged:
                cell.message.text = "notification_user_messaged".localized
            case .liked:
                cell.message.text = "notification_user_liked".localized
            }
        }
        cell.thumbnail.setImage(with: notification.thumbnail)
        return cell
    }

    struct Notification {
        let id: Int?
        let type: NotificationType?
        let profileImage: URL?
        let profileName: String?
        let date: Date?
        let thumbnail: URL?
    }
}

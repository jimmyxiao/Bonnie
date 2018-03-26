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
    var delegate: NotificationViewControllerDelegate?

    override func viewDidLoad() {
        navigationItem.hidesBackButton = true
        tableView.refreshControl = refreshControl
        dateFormatter.dateFormat = "yyyy-MM-dd"
    }

    override func viewDidAppear(_ animated: Bool) {
        if notifications.isEmpty {
            downloadData()
        } else if Date().timeIntervalSince1970 - timestamp.timeIntervalSince1970 > UPDATE_INTERVAL {
            downloadData()
        } else {
            loading.hide(true)
            emptyLabel.isHidden = true
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
    }

    override func viewDidDisappear(_ animated: Bool) {
        refreshControl.endRefreshing()
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = segue.destination as? AccountViewController,
           let indexPath = sender as? IndexPath {
            controller.userId = notifications[indexPath.row].userId
        } else if let navigationController = segue.destination as? UINavigationController,
                  let controller = storyboard?.instantiateViewController(withIdentifier: Identifier.WORK) as? WorkViewController,
                  let indexPath = sender as? IndexPath {
            controller.workId = notifications[indexPath.row].workId
            navigationController.setViewControllers([controller], animated: false)
        }
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
        loading.hide(false)
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
                    self.notifications.append(Notification(withDictionary: notification, dateFormatter: self.dateFormatter))
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

    @IBAction func profile(_ sender: UIButton) {
        guard let indexPath = tableView.indexPath(forView: sender) else {
            return
        }
        if UserDefaults.standard.integer(forKey: Default.USER_ID) == notifications[indexPath.row].userId {
            delegate?.notificationDidTapProfile()
        } else {
            performSegue(withIdentifier: Segue.ACCOUNT, sender: indexPath)
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
        cell.profileName.setTitle(notification.profileName, for: .normal)
        if let date = notification.date {
            cell.date.text = dateFormatter.string(from: date)
        }
        if let type = notification.type {
            switch type {
            case .followed:
                cell.message.text = "notification_user_followed".localized
            case .commented:
                cell.message.text = "notification_user_commented".localized
            case .posted:
                cell.message.text = "notification_user_posted".localized
            case .liked:
                cell.message.text = "notification_user_liked".localized
            default:
                break
            }
        }
        cell.thumbnail.setImage(with: notification.thumbnail)
        return cell
    }

    internal func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if notifications[indexPath.row].workId != nil {
            performSegue(withIdentifier: Segue.WORK, sender: indexPath)
        } else if notifications[indexPath.row].userId != UserDefaults.standard.integer(forKey: Default.USER_ID) {
            performSegue(withIdentifier: Segue.ACCOUNT, sender: indexPath)
        } else {
            delegate?.notificationDidTapProfile()
        }
        tableView.deselectRow(at: indexPath, animated: true)
    }

    struct Notification {
        let id: Int?
        let userId: Int?
        let type: NotificationType?
        let profileImage: URL?
        let profileName: String?
        let workId: Int?
        let date: Date?
        let thumbnail: URL?

        init(withDictionary dictionary: [String: Any], dateFormatter: DateFormatter) {
            id = dictionary["notiMsgId"] as? Int
            userId = dictionary["userIdFollow"] as? Int
            type = NotificationType(rawValue: dictionary["notiMsgType"] as? Int ?? 0)
            profileImage = URL(string: Service.filePath(withSubPath: dictionary["profilePicture"] as? String))
            profileName = dictionary["userNameFollow"] as? String
            workId = dictionary["worksId"] as? Int
            date = dateFormatter.date(from: (dictionary["creationDate"] as? String) ?? "")
            thumbnail = URL(string: Service.filePath(withSubPath: dictionary["imagePath"] as? String))
        }
    }
}

protocol NotificationViewControllerDelegate {
    func notificationDidTapProfile()
}

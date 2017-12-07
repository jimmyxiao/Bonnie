//
//  UserViewController.swift
//  BonnieDraw
//
//  Created by Professor on 24/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class UserViewController: BackButtonViewController, UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate {
    @IBOutlet var backButton: UIBarButtonItem!
    @IBOutlet weak var emptyLabel: UILabel!
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var tableView: UITableView!
    private var users = [User(imageUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), name: "Title", status: "Status", isFollowing: false),
                         User(imageUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), name: "Title", status: "Status", isFollowing: false),
                         User(imageUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), name: "Title", status: "Status", isFollowing: false),
                         User(imageUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), name: "Title", status: "Status", isFollowing: false),
                         User(imageUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), name: "Title", status: "Status", isFollowing: false),
                         User(imageUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), name: "Title", status: "Status", isFollowing: false),
                         User(imageUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), name: "Title", status: "Status", isFollowing: false),
                         User(imageUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), name: "Title", status: "Status", isFollowing: false),
                         User(imageUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), name: "Title", status: "Status", isFollowing: false),
                         User(imageUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), name: "Title", status: "Status", isFollowing: false),
                         User(imageUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), name: "Title", status: "Status", isFollowing: false),
                         User(imageUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), name: "Title", status: "Status", isFollowing: false),
                         User(imageUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), name: "Title", status: "Status", isFollowing: false),
                         User(imageUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), name: "Title", status: "Status", isFollowing: false),
                         User(imageUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), name: "Title", status: "Status", isFollowing: false)]
    private var tableViewUsers = [User]()
    private var dataRequest: DataRequest?
    private var timestamp: Date?
    private let searchBar = UISearchBar()
    private let refreshControl = UIRefreshControl()
    private let placeholderImage = UIImage(named: "photo-square")
    var type: FollowingType?

    override func viewDidLoad() {
        navigationItem.hidesBackButton = true
        searchBar.delegate = self
        searchBar.searchBarStyle = .minimal
        searchBar.returnKeyType = .done
        if let textField = searchBar.value(forKey: "searchField") as? UITextField {
            textField.textColor = UIColor.getTextColor()
        }
        if #available(iOS 11.0, *) {
            searchBar.heightAnchor.constraint(equalToConstant: 44).isActive = true
        }
        refreshControl.addTarget(self, action: #selector(downloadData), for: .valueChanged)
        tableView.refreshControl = refreshControl
    }

    override func viewDidAppear(_ animated: Bool) {
        if tableViewUsers.isEmpty {
            downloadData()
        } else if let timestamp = timestamp {
            if Date().timeIntervalSince1970 - timestamp.timeIntervalSince1970 > UPDATE_INTERVAL {
                downloadData()
            }
        } else {
            downloadData()
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
    }

    @IBAction func search(_ sender: Any) {
        if navigationItem.titleView != searchBar {
            navigationItem.setLeftBarButton(nil, animated: true)
            navigationItem.titleView = searchBar
            searchBar.becomeFirstResponder()
        } else {
            navigationItem.setLeftBarButton(backButton, animated: true)
            navigationItem.titleView = nil
            searchBar.text = nil
            tableViewUsers = users
            tableView.reloadSections([0], with: .automatic)
            emptyLabel.isHidden = !tableViewUsers.isEmpty
        }
    }

    internal func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        searchBar.resignFirstResponder()
    }

    internal func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        if searchText.isEmpty {
            tableViewUsers = users
        } else {
            tableViewUsers.removeAll()
            for user in users {
                if user.name?.uppercased().range(of: searchText.uppercased()) != nil {
                    tableViewUsers.append(user)
                }
            }
        }
        tableView.reloadSections([0], with: .automatic)
        emptyLabel.isHidden = !tableViewUsers.isEmpty
    }

    @objc private func downloadData() {
        guard AppDelegate.reachability.connection != .none else {
            presentConfirmationDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized) {
                success in
                if success {
                    self.downloadData()
                }
            }
            return
        }
        guard let userId = UserDefaults.standard.string(forKey: Default.USER_ID),
              let token = UserDefaults.standard.string(forKey: Default.TOKEN) else {
            return
        }
        dataRequest?.cancel()
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.FOLLOWING_LIST),
                method: .post,
                parameters: ["ui": userId, "lk": token, "dt": SERVICE_DEVICE_TYPE, "fn": type?.rawValue ?? 1],
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], data["res"] as? Int == 1, let userList = data["userList"] as? [[String: Any]] else {
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
                self.users.removeAll()
                for user in userList {
                    self.users.append(User(
                            imageUrl: URL(string: Service.filePath(withSubPath: user["profilePicture"] as? String)),
                            name: user["userName"] as? String,
                            status: "Status",
                            isFollowing: user["isFollowing"] as? Bool ?? false))
                }
                self.tableViewUsers = self.users
                self.tableView.reloadSections([0], with: .automatic)
                self.emptyLabel.isHidden = !self.tableViewUsers.isEmpty
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
        return tableViewUsers.count
    }

    internal func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: Cell.USER, for: indexPath) as? UserTableViewCell {
            let item = users[indexPath.row]
            cell.thumbnail.setImage(with: item.imageUrl, placeholderImage: placeholderImage)
            cell.title.text = item.name
            cell.status.text = item.status
            cell.follow.isSelected = item.isFollowing
            return cell
        }
        return UITableViewCell()
    }

    @IBAction func selectFollow(_ sender: FollowButton) {
        sender.isSelected = !sender.isSelected
        if let indexPath = tableView.indexPath(forView: sender) {
            users[indexPath.row].isFollowing = sender.isSelected
        }
    }
}

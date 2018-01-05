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
    private var users = [User]()
    private var tableViewUsers = [User]()
    private var dataRequest: DataRequest?
    private var timestamp = Date()
    private let searchBar = UISearchBar()
    private let refreshControl = UIRefreshControl()
    private let placeholderImage = UIImage(named: "photo-square")
    var delegate: UserViewControllerDelegate?
    var type: FollowingType?

    override func viewDidLoad() {
        navigationItem.hidesBackButton = true
        searchBar.delegate = self
        searchBar.searchBarStyle = .minimal
        searchBar.returnKeyType = .done
        if let textField = searchBar.value(forKey: "searchField") as? UITextField {
            textField.textColor = UIColor.getTextColor()
        }
        searchBar.heightAnchor.constraint(equalToConstant: 44).isActive = true
        tableView.refreshControl = refreshControl
    }

    override func viewDidAppear(_ animated: Bool) {
        if users.isEmpty {
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
        refreshControl.endRefreshing()
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
                if user.profileName?.uppercased().range(of: searchText.uppercased()) != nil {
                    tableViewUsers.append(user)
                }
            }
        }
        tableView.reloadSections([0], with: .automatic)
        emptyLabel.isHidden = !tableViewUsers.isEmpty
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
                Service.standard(withPath: Service.FOLLOWING_LIST),
                method: .post,
                parameters: ["ui": UserDefaults.standard.integer(forKey: Default.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE, "fn": type?.rawValue ?? 1],
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
                    self.users.append(User(withDictionary: user))
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
            if navigationItem.titleView != searchBar {
                downloadData()
            } else {
                refreshControl.endRefreshing()
            }
        }
    }

    internal func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tableViewUsers.count
    }

    internal func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: Cell.USER, for: indexPath) as? UserTableViewCell {
            let item = tableViewUsers[indexPath.row]
            cell.thumbnail.setImage(with: item.profileImage, placeholderImage: placeholderImage)
            cell.title.text = item.profileName
            cell.status.text = item.status
            cell.follow.isSelected = item.isFollowing ?? false
            return cell
        }
        return UITableViewCell()
    }

    @IBAction func follow(_ sender: FollowButton) {
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
            return
        }
        guard let token = UserDefaults.standard.string(forKey: Default.TOKEN),
              let indexPath = tableView.indexPath(forView: sender),
              let id = tableViewUsers[indexPath.row].id else {
            return
        }
        let follow = !sender.isSelected
        dataRequest?.cancel()
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.SET_FOLLOW),
                method: .post,
                parameters: ["ui": UserDefaults.standard.integer(forKey: Default.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE, "fn": follow ? 1 : 0, "followingUserId": id],
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], let response = data["res"] as? Int else {
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
                if response != 1 {
                    self.presentDialog(
                            title: "service_download_fail_title".localized,
                            message: data["msg"] as? String)
                } else {
                    if let index = self.users.index(where: {
                        user in
                        return user.id == id
                    }) {
                        self.users[index].isFollowing = follow
                    }
                    if let index = self.tableViewUsers.index(where: {
                        user in
                        return user.id == id
                    }) {
                        self.tableViewUsers[index].isFollowing = follow
                        if let cell = self.tableView.cellForRow(at: IndexPath(row: index, section: 0)) as? UserTableViewCell {
                            cell.follow.isSelected = follow
                            self.delegate?.user(didFollowUser: follow)
                        }
                    }
                }
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
}

protocol UserViewControllerDelegate {
    func user(didFollowUser follow: Bool)
}

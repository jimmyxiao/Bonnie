//
//  RecommendViewController.swift
//  BonnieDraw
//
//  Created by Professor on 24/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import FacebookCore
import TwitterKit
import Alamofire

class RecommendViewController: BackButtonViewController, UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate {
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
    var delegate: RecommendViewControllerDelegate?

    override func viewDidLoad() {
        navigationItem.title = title
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

    @IBAction func search(_ sender: UIBarButtonItem) {
        if navigationItem.titleView != searchBar {
            sender.tintColor = UIColor.getAccentColor()
            navigationItem.setLeftBarButton(nil, animated: true)
            navigationItem.titleView = searchBar
            searchBar.becomeFirstResponder()
        } else {
            sender.tintColor = nil
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
            presentConfirmationDialog(title: "alert_network_unreachable_title".localized, message: "alert_network_unreachable_content".localized) {
                success in
                if success {
                    self.downloadData()
                }
            }
            return
        }
        guard let token = UserDefaults.standard.string(forKey: Defaults.TOKEN) else {
            return
        }
        loading.hide(false)
        getFriendList() {
            userIds in
            self.dataRequest?.cancel()
            self.dataRequest = Alamofire.request(
                    Service.standard(withPath: Service.FRIEND_LIST),
                    method: .post,
                    parameters: ["ui": UserDefaults.standard.integer(forKey: Defaults.USER_ID),
                                 "lk": token,
                                 "dt": SERVICE_DEVICE_TYPE,
                                 "thirdPlatform": UserDefaults.standard.integer(forKey: Defaults.USER_TYPE),
                                 "uidList": userIds],
                    encoding: JSONEncoding.default).validate().responseJSON {
                response in
                switch response.result {
                case .success:
                    guard let data = response.result.value as? [String: Any], data["res"] as? Int == 1, let userList = data["friendList"] as? [[String: Any]] else {
//                        self.presentConfirmationDialog(
//                                title: "service_download_fail_title".localized,
//                                message: "alert_network_unreachable_content".localized) {
//                            success in
//                            if success {
//                                self.downloadData()
//                            }
//                        }
                        self.emptyLabel.isHidden = false
                        if !self.loading.isHidden {
                            self.loading.hide(true)
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
    }

    private func getFriendList(completionHandler: @escaping ([String]) -> Void) {
        var list = [String]()
        guard let userType = UserType(rawValue: UserDefaults.standard.integer(forKey: Defaults.USER_TYPE)),
              let userId = UserDefaults.standard.string(forKey: Defaults.THIRD_PARTY_ID) else {
            completionHandler(list)
            return
        }
        switch userType {
        case .email:
            completionHandler(list)
        case .facebook:
            GraphRequest(graphPath: "\(userId)/friends").start() {
                response, result in
                switch result {
                case .success(let response):
                    if let users = response.dictionaryValue?["data"] as? [[String: Any]] {
                        for user in users {
                            if let id = user["id"] as? String {
                                list.append(id)
                            }
                        }
                    }
                    completionHandler(list)
                case .failed(let error):
                    Logger.d("\(#function): \(error.localizedDescription)")
                    completionHandler(list)
                }
            }
        case .google:
            guard let accessToken = UserDefaults.standard.string(forKey: Defaults.THIRD_PARTY_TOKEN) else {
                completionHandler(list)
                return
            }
            dataRequest?.cancel()
            dataRequest = Alamofire.request(
                    "https://people.googleapis.com/v1/people/me/connections?personFields=metadata&pageSize=2000&access_token=\(accessToken)",
                    method: .get,
                    encoding: JSONEncoding.default).validate().responseJSON {
                response in
                switch response.result {
                case .success:
                    if let data = response.result.value as? [String: Any],
                       let connections = data["connections"] as? [[String: Any]] {
                        for connection in connections {
                            if let metadata = connection["metadata"] as? [String: Any],
                               let sources = metadata["sources"] as? [[String: Any]] {
                                for source in sources {
                                    if source["type"] as? String == "ACCOUNT",
                                       let id = source["id"] as? String {
                                        list.append(id)
                                    }
                                }
                            }
                        }
                    }
                case .failure(let error):
                    Logger.d("\(#function): \(error.localizedDescription)")
                }
                completionHandler(list)
            }
        case .twitter:
            let client = TWTRAPIClient(userID: userId)
            let request = client.urlRequest(withMethod: "GET",
                    urlString: "https://api.twitter.com/1.1/friends/ids.json?count=5000",
                    parameters: nil,
                    error: nil)
            client.sendTwitterRequest(request) {
                response, data, error in
                do {
                    if let data = data,
                       let json = try JSONSerialization.jsonObject(with: data, options: .allowFragments) as? [String: Any],
                       let ids = json["ids"] as? [String] {
                        list.append(contentsOf: ids)
                    }
                } catch let error {
                    Logger.d("\(#function): \(error.localizedDescription)")
                }
                completionHandler(list)
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
            cell.follow.isSelected = item.isFollowing ?? false
            return cell
        }
        return UITableViewCell()
    }

    @IBAction func follow(_ sender: FollowButton) {
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "alert_network_unreachable_title".localized, message: "alert_network_unreachable_content".localized)
            return
        }
        guard let token = UserDefaults.standard.string(forKey: Defaults.TOKEN),
              let indexPath = tableView.indexPath(forView: sender),
              let id = tableViewUsers[indexPath.row].id else {
            return
        }
        let follow = !sender.isSelected
        dataRequest?.cancel()
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.SET_FOLLOW),
                method: .post,
                parameters: ["ui": UserDefaults.standard.integer(forKey: Defaults.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE, "fn": follow ? 1 : 0, "followingUserId": id],
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], let response = data["res"] as? Int else {
                    self.presentConfirmationDialog(
                            title: "service_download_fail_title".localized,
                            message: "alert_network_unreachable_content".localized) {
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
                            self.delegate?.recommend(didFollowUser: follow)
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

protocol RecommendViewControllerDelegate {
    func recommend(didFollowUser follow: Bool)
}

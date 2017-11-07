//
//  FollowViewController.swift
//  BonnieDraw
//
//  Created by Professor on 24/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class FollowViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate {
    @IBOutlet weak var emptyLabel: UILabel!
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var tableView: UITableView!
    var delegate: FollowViewControllerDelegate?
    private var items = [TableViewItem(profileImage: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"),
            profileName: "Name",
            thumbnail: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"),
            likes: Int(arc4random_uniform(256))),
        TableViewItem(profileImage: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"),
                profileName: "Name",
                thumbnail: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"),
                likes: Int(arc4random_uniform(256))),
        TableViewItem(profileImage: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"),
                profileName: "Name",
                thumbnail: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"),
                likes: Int(arc4random_uniform(256))),
        TableViewItem(profileImage: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"),
                profileName: "Name",
                thumbnail: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"),
                likes: Int(arc4random_uniform(256))),
        TableViewItem(profileImage: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"),
                profileName: "Name",
                thumbnail: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"),
                likes: Int(arc4random_uniform(256)))]
    private var tableViewItems = [TableViewItem]()
    private var dataRequest: DataRequest?
    private var timestamp: Date?
    private var menuButton: UIBarButtonItem?
    private let searchBar = UISearchBar()
    private let titleView = Bundle.main.loadView(from: "TitleView")
    private let refreshControl = UIRefreshControl()

    override func viewDidLoad() {
        menuButton = UIBarButtonItem(image: UIImage(named: "title_bar_menu"), style: .plain, target: self, action: #selector(didTapMenu))
        navigationItem.leftBarButtonItem = menuButton
        navigationItem.titleView = titleView
        navigationItem.rightBarButtonItem = UIBarButtonItem(image: UIImage(named: "title_bar_ic_search"), style: .plain, target: self, action: #selector(search))
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
        tableView.contentInset = UIEdgeInsetsMake(0, 0, 44, 0)
    }

    override func viewDidAppear(_ animated: Bool) {
        if tableViewItems.isEmpty {
            downloadData()
        } else if let timestamp = timestamp {
            if Date().timeIntervalSince1970 - timestamp.timeIntervalSince1970 > UPDATE_INTERVAL {
                downloadData()
            }
        } else {
            downloadData()
        }
        delegate?.follow(enableMenuGesture: true)
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
        delegate?.follow(enableMenuGesture: false)
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tableViewItems.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: Cell.FOLLOW, for: indexPath) as? FollowTableViewCell {
            let item = tableViewItems[indexPath.row]
            cell.profileImage.setImage(with: item.profileImage)
            cell.profileName.text = item.profileName
            cell.thumbnail.setImage(with: item.thumbnail)
            cell.likes.text = "\(item.likes ?? 0)" + "likes".localized
            return cell
        }
        return UITableViewCell()
    }

    @objc internal func search() {
        if navigationItem.titleView == titleView {
            delegate?.follow(enableMenuGesture: false)
            navigationItem.setLeftBarButton(nil, animated: true)
            navigationItem.titleView = searchBar
            searchBar.becomeFirstResponder()
        } else {
            delegate?.follow(enableMenuGesture: true)
            navigationItem.setLeftBarButton(menuButton, animated: true)
            navigationItem.titleView = titleView
            searchBar.text = nil
            tableViewItems = items
            tableView.reloadSections([0], with: .automatic)
            emptyLabel.isHidden = !tableViewItems.isEmpty
        }
    }

    internal func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        searchBar.resignFirstResponder()
    }

    internal func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        if searchText.isEmpty {
            tableViewItems = items
        } else {
            tableViewItems.removeAll()
            for item in items {
                if item.profileName?.uppercased().range(of: searchText.uppercased()) != nil {
                    tableViewItems.append(item)
                }
            }
        }
        tableView.reloadSections([0], with: .automatic)
        emptyLabel.isHidden = !tableViewItems.isEmpty
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
        loading.hide(false)
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.WORK_LIST),
                method: .post,
                parameters: ["ui": userId, "lk": token, "dt": SERVICE_DEVICE_TYPE, "wt": 2, "stn": 1, "rc": 128],
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], data["res"] as? Int == 1 else {
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
//                    self.items.removeAll()
                self.tableViewItems = self.items
                self.tableView.reloadSections([0], with: .automatic)
                self.emptyLabel.isHidden = !self.tableViewItems.isEmpty
                self.loading.hide(true)
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

    @objc private func didTapMenu() {
        delegate?.followDidTapMenu()
    }

    struct TableViewItem {
        let profileImage: URL?
        let profileName: String?
        let thumbnail: URL?
        var likes: Int?
    }
}

protocol FollowViewControllerDelegate {
    func followDidTapMenu()

    func follow(enableMenuGesture enable: Bool)
}

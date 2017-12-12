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
    @IBOutlet var menuButton: UIBarButtonItem!
    @IBOutlet weak var emptyLabel: UILabel!
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var tableView: UITableView!
    var delegate: FollowViewControllerDelegate?
    private var works = [Work]()
    private var tableViewWorks = [Work]()
    private var dataRequest: DataRequest?
    private var timestamp: Date?
    private let searchBar = UISearchBar()
    private let titleView = Bundle.main.loadView(from: "TitleView")
    private let refreshControl = UIRefreshControl()
    private let placeholderImage = UIImage(named: "photo-square")

    override func viewDidLoad() {
        navigationItem.titleView = titleView
        searchBar.delegate = self
        searchBar.searchBarStyle = .minimal
        searchBar.returnKeyType = .done
        if let textField = searchBar.value(forKey: "searchField") as? UITextField {
            textField.textColor = UIColor.getTextColor()
        }
        if #available(iOS 11.0, *) {
            searchBar.heightAnchor.constraint(equalToConstant: 44).isActive = true
        }
        tableView.refreshControl = refreshControl
        tableView.contentInset = UIEdgeInsetsMake(0, 0, 44, 0)
    }

    override func viewDidAppear(_ animated: Bool) {
        if tableViewWorks.isEmpty {
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

    internal func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        if refreshControl.isRefreshing {
            downloadData()
        }
    }

    internal func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tableViewWorks.count
    }

    internal func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: Cell.FOLLOW, for: indexPath) as? FollowTableViewCell {
            let item = tableViewWorks[indexPath.row]
            cell.profileImage.setImage(with: item.profileImage, placeholderImage: placeholderImage)
            cell.profileName.text = item.profileName
            cell.thumbnail.setImage(with: item.thumbnail)
            cell.likes.text = "\(item.likes ?? 0)" + "likes".localized
            return cell
        }
        return UITableViewCell()
    }

    @IBAction func search(_ sender: Any) {
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
            tableViewWorks = works
            tableView.reloadSections([0], with: .automatic)
            emptyLabel.isHidden = !tableViewWorks.isEmpty
        }
    }

    internal func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        searchBar.resignFirstResponder()
    }

    internal func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        if searchText.isEmpty {
            tableViewWorks = works
        } else {
            tableViewWorks.removeAll()
            for item in works {
                if item.profileName?.uppercased().range(of: searchText.uppercased()) != nil {
                    tableViewWorks.append(item)
                }
            }
        }
        tableView.reloadSections([0], with: .automatic)
        emptyLabel.isHidden = !tableViewWorks.isEmpty
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
        guard let userId = UserDefaults.standard.string(forKey: Default.USER_ID),
              let token = UserDefaults.standard.string(forKey: Default.TOKEN) else {
            return
        }
        dataRequest?.cancel()
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.WORK_LIST),
                method: .post,
                parameters: ["ui": userId, "lk": token, "dt": SERVICE_DEVICE_TYPE, "wt": 1, "stn": 1, "rc": 128],
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], data["res"] as? Int == 1, let works = data["workList"] as? [[String: Any]] else {
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
                self.works.removeAll()
                for work in works {
                    self.works.append(Work(withDictionary: work))
                }
                self.tableViewWorks = self.works
                self.tableView.reloadSections([0], with: .automatic)
                self.emptyLabel.isHidden = !self.tableViewWorks.isEmpty
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

    @IBAction func didTapMenu(_ sender: Any) {
        delegate?.followDidTapMenu()
    }
}

protocol FollowViewControllerDelegate {
    func followDidTapMenu()

    func follow(enableMenuGesture enable: Bool)
}

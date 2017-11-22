//
//  RecommendViewController.swift
//  BonnieDraw
//
//  Created by Professor on 24/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class RecommendViewController: BackButtonViewController, UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate {
    @IBOutlet weak var emptyLabel: UILabel!
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var tableView: UITableView!
    private var recommendations = [Recommendation(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                                   Recommendation(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                                   Recommendation(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                                   Recommendation(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                                   Recommendation(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                                   Recommendation(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                                   Recommendation(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                                   Recommendation(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                                   Recommendation(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                                   Recommendation(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                                   Recommendation(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                                   Recommendation(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                                   Recommendation(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                                   Recommendation(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                                   Recommendation(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false)]
    private var tableViewRecommendations = [Recommendation]()
    private var dataRequest: DataRequest?
    private var timestamp: Date?
    private var backButton: UIBarButtonItem?
    private let searchBar = UISearchBar()
    private let refreshControl = UIRefreshControl()

    override func viewDidLoad() {
        backButton = UIBarButtonItem(image: UIImage(named: "back_icon"), style: .plain, target: self, action: #selector(onBackPressed))
        navigationItem.leftBarButtonItem = backButton
        navigationItem.rightBarButtonItem = UIBarButtonItem(image: UIImage(named: "title_bar_ic_search"), style: .plain, target: self, action: #selector(search))
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
        if tableViewRecommendations.isEmpty {
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

    @objc internal func search() {
        if navigationItem.titleView != searchBar {
            navigationItem.setLeftBarButton(nil, animated: true)
            navigationItem.titleView = searchBar
            searchBar.becomeFirstResponder()
        } else {
            navigationItem.setLeftBarButton(backButton, animated: true)
            navigationItem.titleView = nil
            searchBar.text = nil
            tableViewRecommendations = recommendations
            tableView.reloadSections([0], with: .automatic)
            emptyLabel.isHidden = !tableViewRecommendations.isEmpty
        }
    }

    internal func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        searchBar.resignFirstResponder()
    }

    internal func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        if searchText.isEmpty {
            tableViewRecommendations = recommendations
        } else {
            tableViewRecommendations.removeAll()
            for item in recommendations {
                if item.title?.uppercased().range(of: searchText.uppercased()) != nil {
                    tableViewRecommendations.append(item)
                }
            }
        }
        tableView.reloadSections([0], with: .automatic)
        emptyLabel.isHidden = !tableViewRecommendations.isEmpty
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
                self.tableViewRecommendations = self.recommendations
                self.tableView.reloadSections([0], with: .automatic)
                self.emptyLabel.isHidden = !self.tableViewRecommendations.isEmpty
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
        return tableViewRecommendations.count
    }

    internal func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: Cell.RECOMMEND, for: indexPath) as? RecommendTableViewCell {
            let item = recommendations[indexPath.row]
            cell.thumbnail.setImage(with: item.iamgeUrl)
            cell.title.text = item.title
            cell.status.text = item.status
            cell.follow.isSelected = item.isSelected
            return cell
        }
        return UITableViewCell()
    }

    @IBAction func selectFollow(_ sender: FollowButton) {
        sender.isSelected = !sender.isSelected
        if let indexPath = tableView.indexPath(forView: sender) {
            recommendations[indexPath.row].isSelected = sender.isSelected
        }
    }

    struct Recommendation {
        let iamgeUrl: URL?
        let title: String?
        let status: String?
        var isSelected: Bool
    }
}

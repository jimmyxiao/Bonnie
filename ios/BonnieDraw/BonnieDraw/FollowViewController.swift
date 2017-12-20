//
//  FollowViewController.swift
//  BonnieDraw
//
//  Created by Professor on 24/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class FollowViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate, WorkViewControllerDelegate {
    @IBOutlet weak var emptyLabel: UILabel!
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var tableView: UITableView!
    private var works = [Work]()
    private var tableViewWorks = [Work]()
    private var dataRequest: DataRequest?
    private var timestamp = Date()
    private let searchBar = UISearchBar()
    private let refreshControl = UIRefreshControl()
    private let titleView = Bundle.main.loadView(from: "TitleView")
    private let placeholderImage = UIImage(named: "photo-square")
    private let likeImage = UIImage(named: "work_ic_like")
    private let likeImageSelected = UIImage(named: "work_ic_like_on")
    private let collectionImage = UIImage(named: "collect_ic_off")
    private let collectionImageSelected = UIImage(named: "collect_ic_on")
    private var postData: [String: Any] = ["ui": UserDefaults.standard.integer(forKey: Default.USER_ID), "lk": UserDefaults.standard.string(forKey: Default.TOKEN) ?? "", "dt": SERVICE_DEVICE_TYPE, "wt": 1, "stn": 1, "rc": 128]

    override func viewDidLoad() {
        navigationItem.titleView = titleView
        searchBar.delegate = self
        searchBar.searchBarStyle = .minimal
        searchBar.returnKeyType = .done
        if let textField = searchBar.value(forKey: "searchField") as? UITextField {
            textField.textColor = UIColor.getTextColor()
        }
        searchBar.heightAnchor.constraint(equalToConstant: 44).isActive = true
        tableView.refreshControl = refreshControl
        tableView.contentInset = UIEdgeInsetsMake(0, 0, 44, 0)
    }

    override func viewDidAppear(_ animated: Bool) {
        if works.isEmpty {
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

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = segue.destination as? WorkViewController,
           let indexPath = tableView.indexPathForSelectedRow {
            controller.delegate = self
            controller.work = tableViewWorks[indexPath.row]
            tableView.deselectRow(at: indexPath, animated: true)
        } else if let controller = segue.destination as? CommentViewController,
                  let indexPath = sender as? IndexPath {
            controller.work = tableViewWorks[indexPath.row]
        }
    }

    @IBAction func search(_ sender: Any) {
        if navigationItem.titleView == titleView {
            navigationItem.setLeftBarButton(nil, animated: true)
            navigationItem.titleView = searchBar
            searchBar.becomeFirstResponder()
        } else {
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
            for work in works {
                if work.title?.uppercased().range(of: searchText.uppercased()) != nil {
                    tableViewWorks.append(work)
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
        dataRequest?.cancel()
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.WORK_LIST),
                method: .post,
                parameters: postData,
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

    internal func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        if refreshControl.isRefreshing {
            downloadData()
        }
    }

    internal func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tableViewWorks.count
    }

    internal func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let work = tableViewWorks[indexPath.row]
        let cell = tableView.dequeueReusableCell(withIdentifier: Cell.FOLLOW, for: indexPath) as! FollowTableViewCell
        cell.profileImage.setImage(with: work.profileImage, placeholderImage: placeholderImage)
        cell.profileName.text = work.profileName
        cell.thumbnail?.setImage(with: work.thumbnail)
        cell.followButton.isSelected = work.isFollow ?? false
        if let isLike = work.isLike {
            if isLike {
                cell.likeButton.isSelected = true
                cell.likeButton.setImage(likeImageSelected, for: .normal)
            } else {
                cell.likeButton.isSelected = false
                cell.likeButton.setImage(likeImage, for: .normal)
            }
        }
        if let isCollection = work.isCollect {
            if isCollection {
                cell.collectButton.isSelected = true
                cell.collectButton.setImage(collectionImageSelected, for: .normal)
            } else {
                cell.collectButton.isSelected = false
                cell.collectButton.setImage(collectionImage, for: .normal)
            }
        }
        cell.likes.text = work.likes ?? 0 > 0 ? "\(work.likes ?? 0)" + "likes".localized : "first_like".localized
        return cell
    }

    internal func work(didChange changedWork: Work) {
        if let index = self.works.index(where: {
            work in
            return work.id == changedWork.id
        }) {
            works[index] = changedWork
        }
        if let index = self.tableViewWorks.index(where: {
            work in
            return work.id == changedWork.id
        }) {
            tableViewWorks[index] = changedWork
            tableView.reloadRows(at: [IndexPath(row: index, section: 0)], with: .automatic)
        }
    }

    @IBAction func follow(_ sender: FollowButton) {
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
            return
        }
        guard let token = UserDefaults.standard.string(forKey: Default.TOKEN),
              let indexPath = tableView.indexPath(forView: sender),
              let id = tableViewWorks[indexPath.row].userId else {
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
                    for index in 0..<self.works.count {
                        if self.works[index].userId == id {
                            self.works[index].isFollow = follow
                        }
                    }
                    for index in 0..<self.tableViewWorks.count {
                        if self.tableViewWorks[index].userId == id {
                            self.tableViewWorks[index].isFollow = follow
                            (self.tableView.cellForRow(at: IndexPath(row: index, section: 0)) as? HomeTableViewCell)?.followButton.isSelected = follow
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

    @IBAction func more(_ sender: UIButton) {
        if let indexPath = tableView.indexPath(forView: sender) {
            Logger.d("\(#function) \(indexPath.row)")
        }
    }

    @IBAction func like(_ sender: UIButton) {
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
            return
        }
        guard let token = UserDefaults.standard.string(forKey: Default.TOKEN),
              let indexPath = tableView.indexPath(forView: sender),
              let id = tableViewWorks[indexPath.row].id else {
            return
        }
        let like = !sender.isSelected
        dataRequest?.cancel()
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.SET_LIKE),
                method: .post,
                parameters: ["ui": UserDefaults.standard.integer(forKey: Default.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE, "fn": like ? 1 : 0, "worksId": id, "likeType": 1],
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], let res = data["res"] as? Int else {
                    self.presentDialog(
                            title: "service_download_fail_title".localized,
                            message: "app_network_unreachable_content".localized)
                    return
                }
                if res != 1 {
                    self.presentDialog(
                            title: "service_download_fail_title".localized,
                            message: data["msg"] as? String)
                } else {
                    if let index = self.works.index(where: {
                        work in
                        return work.id == id
                    }) {
                        self.works[index].isLike = like
                        if let likes = self.works[index].likes {
                            self.works[index].likes = likes + (like ? 1 : -1)
                        }
                    }
                    if let index = self.tableViewWorks.index(where: {
                        work in
                        return work.id == id
                    }) {
                        self.tableViewWorks[index].isLike = like
                        if let likes = self.tableViewWorks[index].likes {
                            self.tableViewWorks[index].likes = likes + (like ? 1 : -1)
                        }
                        if let cell = self.tableView.cellForRow(at: IndexPath(row: index, section: 0)) as? FollowTableViewCell {
                            cell.likeButton.isSelected = like
                            cell.likeButton.setImage(like ? self.likeImageSelected : self.likeImage, for: .normal)
                            cell.likes.text = "\(self.tableViewWorks[index].likes ?? 0)" + "likes".localized
                        }
                    }
                }
            case .failure(let error):
                if let error = error as? URLError, error.code == .cancelled {
                    return
                }
                self.presentDialog(
                        title: "service_download_fail_title".localized,
                        message: error.localizedDescription)
            }
        }
    }

    @IBAction func comment(_ sender: UIButton) {
        if let indexPath = tableView.indexPath(forView: sender) {
            performSegue(withIdentifier: Segue.COMMENT, sender: indexPath)
        }
    }

    @IBAction func share(_ sender: UIButton) {
        if let indexPath = tableView.indexPath(forView: sender) {
            Logger.d("\(#function) \(indexPath.row)")
        }
    }

    @IBAction func collect(_ sender: UIButton) {
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
            return
        }
        guard let token = UserDefaults.standard.string(forKey: Default.TOKEN),
              let indexPath = tableView.indexPath(forView: sender),
              let id = tableViewWorks[indexPath.row].id else {
            return
        }
        let collect = !sender.isSelected
        dataRequest?.cancel()
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.SET_COLLECTION),
                method: .post,
                parameters: ["ui": UserDefaults.standard.integer(forKey: Default.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE, "fn": collect ? 1 : 0, "worksId": id],
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], let res = data["res"] as? Int else {
                    self.presentDialog(
                            title: "service_download_fail_title".localized,
                            message: "app_network_unreachable_content".localized)
                    return
                }
                if res != 1 {
                    self.presentDialog(
                            title: "service_download_fail_title".localized,
                            message: data["msg"] as? String)
                } else {
                    if let index = self.works.index(where: {
                        work in
                        return work.id == id
                    }) {
                        self.works[index].isCollect = collect
                    }
                    if let index = self.tableViewWorks.index(where: {
                        work in
                        return work.id == id
                    }) {
                        self.tableViewWorks[index].isCollect = collect
                        if let cell = self.tableView.cellForRow(at: IndexPath(row: index, section: 0)) as? FollowTableViewCell {
                            cell.collectButton.isSelected = collect
                            cell.collectButton.setImage(collect ? self.collectionImageSelected : self.collectionImage, for: .normal)
                        }
                    }
                }
            case .failure(let error):
                if let error = error as? URLError, error.code == .cancelled {
                    return
                }
                self.presentDialog(
                        title: "service_download_fail_title".localized,
                        message: error.localizedDescription)
            }
        }
    }
}

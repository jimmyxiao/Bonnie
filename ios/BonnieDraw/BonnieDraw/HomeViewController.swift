//
//  HomeViewController.swift
//  BonnieDraw
//
//  Created by Professor on 26/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class HomeViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate, WorkViewControllerDelegate, CommentViewControllerDelegate {
    @IBOutlet var menuButton: UIBarButtonItem!
    @IBOutlet weak var emptyLabel: UILabel!
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var tableView: UITableView!
    var delegate: HomeViewControllerDelegate?
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
    private var postData: [String: Any] = ["ui": UserDefaults.standard.integer(forKey: Default.USER_ID), "lk": UserDefaults.standard.string(forKey: Default.TOKEN) ?? "", "dt": SERVICE_DEVICE_TYPE, "wt": 2, "stn": 1, "rc": 128]

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
        delegate?.home(enableMenuGesture: true)
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
        delegate?.home(enableMenuGesture: false)
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = segue.destination as? WorkViewController,
           let indexPath = tableView.indexPathForSelectedRow {
            controller.delegate = self
            controller.work = tableViewWorks[indexPath.row]
            tableView.deselectRow(at: indexPath, animated: true)
        } else if let controller = segue.destination as? CommentViewController,
                  let indexPath = sender as? IndexPath {
            controller.delegate = self
            controller.work = tableViewWorks[indexPath.row]
        }
    }

    @IBAction func search(_ sender: Any) {
        if navigationItem.titleView == titleView {
            delegate?.home(enableMenuGesture: false)
            navigationItem.setLeftBarButton(nil, animated: true)
            navigationItem.titleView = searchBar
            searchBar.becomeFirstResponder()
        } else {
            delegate?.home(enableMenuGesture: true)
            navigationItem.setLeftBarButton(menuButton, animated: true)
            navigationItem.titleView = titleView
            searchBar.text = nil
            tableViewWorks = works
            tableView.reloadSections([0], with: .automatic)
            emptyLabel.isHidden = !tableViewWorks.isEmpty
        }
    }

    internal func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        postData["wt"] = 9
        postData["search"] = searchBar.text
        downloadData()
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

    func setTag(type: DrawerViewController.TagType, tag: String?) {
        switch type {
        case .popularWork:
            postData["wt"] = 2
        case .newWork:
            postData["wt"] = 4
        case .myWork:
            postData["wt"] = 5
        case .normal:
            postData["wt"] = 8
            postData["tagName"] = tag
        case .myCollection:
            postData["wt"] = 7
        default:
            return
        }
        downloadData()
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
        let cell = tableView.dequeueReusableCell(withIdentifier: Cell.HOME, for: indexPath) as! HomeTableViewCell
        cell.profileImage.setImage(with: work.profileImage, placeholderImage: placeholderImage)
        cell.profileName.setTitle(work.profileName, for: .normal)
        cell.followButton.isSelected = work.isFollow ?? false
        cell.title.text = work.title
        cell.thumbnail?.setImage(with: work.thumbnail)
        if let isLike = work.isLike {
            if isLike {
                cell.likeButton.isSelected = true
                cell.likeButton.setImage(likeImageSelected, for: .normal)
            } else {
                cell.likeButton.isSelected = false
                cell.likeButton.setImage(likeImage, for: .normal)
            }
        }
        if let likes = work.likes, likes > 0 {
            cell.likes.text = "\(likes)"
            cell.likes.isHidden = false
        } else {
            cell.likes.isHidden = true
        }
        if let comments = work.comments, comments > 0 {
            cell.comments.text = "\(comments)"
            cell.comments.isHidden = false
        } else {
            cell.comments.isHidden = true
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
        return cell
    }

    internal func work(didChange changedWork: Work) {
        if let index = works.index(where: {
            work in
            return work.id == changedWork.id
        }) {
            works[index] = changedWork
        }
        if let index = tableViewWorks.index(where: {
            work in
            return work.id == changedWork.id
        }) {
            tableViewWorks[index] = changedWork
            tableView.reloadRows(at: [IndexPath(row: index, section: 0)], with: .none)
        }
    }

    internal func comment(didCommentOnWork changedWork: Work) {
        if let index = works.index(where: {
            work in
            return work.id == changedWork.id
        }) {
            works[index] = changedWork
        }
        if let index = tableViewWorks.index(where: {
            work in
            return work.id == changedWork.id
        }) {
            tableViewWorks[index] = changedWork
            tableView.reloadRows(at: [IndexPath(row: index, section: 0)], with: .none)
        }
    }

    @IBAction func profile(_ sender: UIButton) {
        guard let indexPath = tableView.indexPath(forView: sender) else {
            return
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
        guard let indexPath = tableView.indexPath(forView: sender) else {
            return
        }
        let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        alert.view.tintColor = UIColor.getAccentColor()
        alert.popoverPresentationController?.sourceView = sender
        let color = UIColor.gray
        let copyLinkAction = UIAlertAction(title: "copy_link".localized, style: .default) {
            action in
        }
        copyLinkAction.setValue(color, forKey: "titleTextColor")
        alert.addAction(copyLinkAction)
        let reportAction = UIAlertAction(title: "report".localized, style: .destructive) {
            action in
            guard AppDelegate.reachability.connection != .none else {
                self.presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
                return
            }
        }
        alert.addAction(reportAction)
        let cancelAction = UIAlertAction(title: "alert_button_cancel".localized, style: .cancel)
        alert.addAction(cancelAction)
        present(alert, animated: true)
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
                        if let cell = self.tableView.cellForRow(at: IndexPath(row: index, section: 0)) as? HomeTableViewCell {
                            cell.likeButton.isSelected = like
                            cell.likeButton.setImage(like ? self.likeImageSelected : self.likeImage, for: .normal)
                            if let likes = self.tableViewWorks[index].likes, likes > 0 {
                                cell.likes.text = "\(likes)"
                                cell.likes.isHidden = false
                            } else {
                                cell.likes.isHidden = true
                            }
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
                        if let cell = self.tableView.cellForRow(at: IndexPath(row: index, section: 0)) as? HomeTableViewCell {
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

    @IBAction func didTapMenu(_ sender: Any) {
        delegate?.homeDidTapMenu()
    }
}

protocol HomeViewControllerDelegate {
    func homeDidTapMenu()

    func home(enableMenuGesture enable: Bool)
}

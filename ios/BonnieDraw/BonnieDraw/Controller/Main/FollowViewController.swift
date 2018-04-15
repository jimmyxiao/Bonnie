//
//  FollowViewController.swift
//  BonnieDraw
//
//  Created by Professor on 24/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class FollowViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate, WorkViewControllerDelegate, AccountViewControllerDelegate, CommentViewControllerDelegate, EditViewControllerDelegate {
    @IBOutlet weak var emptyLabel: UILabel!
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var tableView: UITableView!
    private var works = [Work]()
    private var tableViewWorks = [Work]()
    private var dataRequest: DataRequest?
    private var timestamp = Date()
    private let searchBar = UISearchBar()
    private let refreshControl = UIRefreshControl()
    private let titleView = UIImageView(image: UIImage(named: "title_logo"))
    private let placeholderImage = UIImage(named: "photo-square")
    private let likeImage = UIImage(named: "work_ic_like")
    private let likeImageSelected = UIImage(named: "work_ic_like_on")
    private let collectionImage = UIImage(named: "collect_ic_off")
    private let collectionImageSelected = UIImage(named: "collect_ic_on")
    var delegate: FollowViewControllerDelegate?

    override func viewDidLoad() {
        navigationItem.hidesBackButton = true
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
            loading.hide(true)
            emptyLabel.isHidden = true
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
    }

    override func viewDidDisappear(_ animated: Bool) {
        refreshControl.endRefreshing()
        if navigationItem.titleView != titleView {
            navigationItem.titleView = titleView
            navigationItem.rightBarButtonItem?.tintColor = nil
            searchBar.text = nil
            tableViewWorks = works
            tableView.reloadSections([0], with: .automatic)
            emptyLabel.isHidden = !tableViewWorks.isEmpty
        }
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = segue.destination as? AccountViewController,
           let indexPath = sender as? IndexPath {
            controller.delegate = self
            controller.userId = works[indexPath.row].userId
        } else if let controller = segue.destination as? EditViewController,
                  let indexPath = sender as? IndexPath {
            controller.delegate = self
            controller.work = works[indexPath.row]
        } else if let controller = segue.destination as? ReportViewController,
                  let indexPath = sender as? IndexPath {
            controller.work = works[indexPath.row]
        } else if let navigationController = segue.destination as? UINavigationController,
                  let indexPath = tableView.indexPathForSelectedRow ?? sender as? IndexPath {
            if segue.identifier == Segue.COMMENT {
                if let controller = storyboard?.instantiateViewController(withIdentifier: Identifier.COMMENT) as? CommentViewController {
                    controller.delegate = self
                    controller.work = tableViewWorks[indexPath.row]
                    navigationController.setViewControllers([controller], animated: false)
                }
            } else {
                if let controller = storyboard?.instantiateViewController(withIdentifier: Identifier.WORK) as? WorkViewController {
                    controller.delegate = self
                    controller.work = tableViewWorks[indexPath.row]
                    navigationController.setViewControllers([controller], animated: false)
                }
            }
            tableView.deselectRow(at: indexPath, animated: true)
        }
    }

    @IBAction func search(_ sender: UIBarButtonItem) {
        if navigationItem.titleView == titleView {
            sender.tintColor = UIColor.getAccentColor()
            navigationItem.setLeftBarButton(nil, animated: true)
            navigationItem.titleView = searchBar
            searchBar.becomeFirstResponder()
        } else {
            sender.tintColor = nil
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
            presentConfirmationDialog(title: "alert_network_unreachable_title".localized, message: "alert_network_unreachable_content".localized) {
                success in
                if success {
                    self.downloadData()
                }
            }
            return
        }
        loading.hide(false)
        dataRequest?.cancel()
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.WORK_LIST),
                method: .post,
                parameters: ["ui": UserDefaults.standard.integer(forKey: Default.USER_ID), "lk": UserDefaults.standard.string(forKey: Default.TOKEN) ?? "", "dt": SERVICE_DEVICE_TYPE, "wt": 1, "stn": 1, "rc": 128],
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], data["res"] as? Int == 1, let works = data["workList"] as? [[String: Any]] else {
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
            if navigationItem.titleView != searchBar {
                downloadData()
            } else {
                refreshControl.endRefreshing()
            }
        }
    }

    internal func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tableViewWorks.count
    }

    internal func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let work = tableViewWorks[indexPath.row]
        let cell = tableView.dequeueReusableCell(withIdentifier: Cell.FOLLOW, for: indexPath) as! FollowTableViewCell
        cell.profileImage.setImage(with: work.profileImage, placeholderImage: placeholderImage)
        cell.profileName.setTitle(work.profileName, for: .normal)
        cell.title.text = work.title
        cell.thumbnail?.setImage(with: work.thumbnail)
        if let isLike = work.isLike {
            if isLike {
                cell.likeButton.isUserInteractionEnabled = false
                cell.likeButton.isSelected = true
                cell.likeButton.setImage(likeImageSelected, for: .normal)
            } else {
                cell.likeButton.isUserInteractionEnabled = true
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
        cell.openLink.isHidden = work.link?.isEmpty ?? true
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

    internal func work(didDelete deletedWork: Work) {
        if let index = self.works.index(where: {
            work in
            return work.id == deletedWork.id
        }) {
            self.works.remove(at: index)
        }
        if let index = self.tableViewWorks.index(where: {
            work in
            return work.id == deletedWork.id
        }) {
            self.tableViewWorks.remove(at: index)
            self.emptyLabel.isHidden = !self.tableViewWorks.isEmpty
            self.tableView.deleteRows(at: [IndexPath(row: index, section: 0)], with: .automatic)
        }
    }

    internal func account(didFollowUserId userId: Int, follow: Bool) {
        if follow {
            downloadData()
        } else {
            works = works.filter {
                work in
                return work.userId != userId
            }
            tableViewWorks = tableViewWorks.filter {
                work in
                return work.userId != userId
            }
            tableView.reloadData()
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

    internal func commentDidTapProfile() {
        delegate?.followDidTapProfile()
    }

    internal func edit(didChange changedWork: Work) {
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
        if UserDefaults.standard.integer(forKey: Default.USER_ID) == tableViewWorks[indexPath.row].userId {
            delegate?.followDidTapProfile()
        } else {
            performSegue(withIdentifier: Segue.ACCOUNT, sender: indexPath)
        }
    }

    @IBAction func more(_ sender: UIButton) {
        guard let indexPath = tableView.indexPath(forView: sender) else {
            return
        }
        let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        alert.view.tintColor = UIColor.getAccentColor()
        if let presentation = alert.popoverPresentationController {
            presentation.sourceView = sender
            presentation.sourceRect = sender.bounds
        }
        let color = UIColor.gray
        let copyLinkAction = UIAlertAction(title: "more_copy_link".localized, style: .default) {
            action in
            UIPasteboard.general.url = URL(string: Service.sharePath(withId: self.tableViewWorks[indexPath.row].id))
        }
        copyLinkAction.setValue(color, forKey: "titleTextColor")
        alert.addAction(copyLinkAction)
        if tableViewWorks[indexPath.row].userId != UserDefaults.standard.integer(forKey: Default.USER_ID) {
            let reportAction = UIAlertAction(title: "more_report".localized, style: .destructive) {
                action in
                guard AppDelegate.reachability.connection != .none else {
                    self.presentDialog(title: "alert_network_unreachable_title".localized, message: "alert_network_unreachable_content".localized)
                    return
                }
                self.performSegue(withIdentifier: Segue.REPORT, sender: indexPath)
            }
            alert.addAction(reportAction)
        } else {
            let editAction = UIAlertAction(title: "more_edit_work".localized, style: .default) {
                action in
                guard AppDelegate.reachability.connection != .none else {
                    self.presentDialog(title: "alert_network_unreachable_title".localized, message: "alert_network_unreachable_content".localized)
                    return
                }
                self.performSegue(withIdentifier: Segue.EDIT, sender: indexPath)
            }
            editAction.setValue(color, forKey: "titleTextColor")
            alert.addAction(editAction)
            let removeAction = UIAlertAction(title: "more_remove_work".localized, style: .destructive) {
                action in
                let alert = UIAlertController(title: "more_remove_work".localized, message: "alert_delete_content".localized, preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "alert_button_delete".localized, style: .destructive) {
                    action in
                    guard AppDelegate.reachability.connection != .none else {
                        self.presentDialog(title: "alert_network_unreachable_title".localized, message: "alert_network_unreachable_content".localized)
                        return
                    }
                    guard let token = UserDefaults.standard.string(forKey: Default.TOKEN),
                          let id = self.tableViewWorks[indexPath.row].id else {
                        return
                    }
                    self.loading.hide(false)
                    self.dataRequest?.cancel()
                    self.dataRequest = Alamofire.request(
                            Service.standard(withPath: Service.WORK_DELETE),
                            method: .post,
                            parameters: ["ui": UserDefaults.standard.integer(forKey: Default.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE, "worksId": id],
                            encoding: JSONEncoding.default).validate().responseJSON {
                        response in
                        switch response.result {
                        case .success:
                            self.loading.hide(true)
                            guard let data = response.result.value as? [String: Any], let response = data["res"] as? Int else {
                                self.presentDialog(title: "alert_delete_fail_title".localized, message: "alert_network_unreachable_content".localized)
                                return
                            }
                            if response != 1 {
                                self.presentDialog(
                                        title: "service_download_fail_title".localized,
                                        message: data["msg"] as? String)
                            } else {
                                self.work(didDelete: self.tableViewWorks[indexPath.row])
                            }
                        case .failure(let error):
                            self.loading.hide(true)
                            if let error = error as? URLError, error.code == .cancelled {
                                return
                            }
                            self.presentDialog(title: "alert_delete_fail_title".localized, message: error.localizedDescription)
                        }
                    }
                })
                alert.addAction(UIAlertAction(title: "alert_button_cancel".localized, style: .cancel))
                alert.view.tintColor = UIColor.getAccentColor()
                self.present(alert, animated: true)
            }
            alert.addAction(removeAction)
        }
        let cancelAction = UIAlertAction(title: "alert_button_cancel".localized, style: .cancel)
        alert.addAction(cancelAction)
        present(alert, animated: true)
    }

    @IBAction func like(_ sender: UIButton) {
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "alert_network_unreachable_title".localized, message: "alert_network_unreachable_content".localized)
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
                            message: "alert_network_unreachable_content".localized)
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
                            cell.likeButton.isUserInteractionEnabled = !like
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
        if let indexPath = tableView.indexPath(forView: sender),
           let url = URL(string: Service.sharePath(withId: tableViewWorks[indexPath.row].id)) {
            let controller = UIActivityViewController(activityItems: [url], applicationActivities: nil)
            controller.excludedActivityTypes = [.airDrop, .saveToCameraRoll, .assignToContact, .addToReadingList, .copyToPasteboard, .print]
            if let presentation = controller.popoverPresentationController {
                presentation.sourceView = sender
                presentation.sourceRect = sender.bounds
            }
            present(controller, animated: true)
        }
    }

    @IBAction func openLink(_ sender: UIButton) {
        if let indexPath = tableView.indexPath(forView: sender),
           let link = URL(string: works[indexPath.row].link ?? ""),
           UIApplication.shared.canOpenURL(link) {
            UIApplication.shared.open(link, options: [:])
        }
    }

    @IBAction func collect(_ sender: UIButton) {
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "alert_network_unreachable_title".localized, message: "alert_network_unreachable_content".localized)
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
                            message: "alert_network_unreachable_content".localized)
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

protocol FollowViewControllerDelegate {
    func followDidTapProfile()
}

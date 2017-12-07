//
//  HomeViewController.swift
//  BonnieDraw
//
//  Created by Professor on 26/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class HomeViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate {
    @IBOutlet weak var emptyLabel: UILabel!
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var tableView: UITableView!
    var delegate: HomeViewControllerDelegate?
    private let commentTextAttributes = [NSAttributedStringKey.foregroundColor: UIColor.lightGray]
    private var works = [Work]()
    private var tableViewWorks = [Work]()
    private var dataRequest: DataRequest?
    private var timestamp: Date?
    private var menuButton: UIBarButtonItem?
    private let searchBar = UISearchBar()
    private let refreshControl = UIRefreshControl()
    private let titleView = Bundle.main.loadView(from: "TitleView")
    private let placeholderImage = UIImage(named: "photo-square")
    private let likeImage = UIImage(named: "work_ic_like")
    private let likeImageSelected = UIImage(named: "work_ic_like_on")
    private let collectionImage = UIImage(named: "collect_ic_off")
    private let collectionImageSelected = UIImage(named: "collect_ic_on")
    private var postData: [String: Any]?

    override func viewDidLoad() {
        navigationItem.titleView = titleView
        menuButton = navigationItem.leftBarButtonItem
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
        postData = ["ui": UserDefaults.standard.string(forKey: Default.USER_ID) ?? "", "lk": UserDefaults.standard.string(forKey: Default.TOKEN) ?? "", "dt": SERVICE_DEVICE_TYPE, "wt": 2, "stn": 1, "rc": 128]
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
        delegate?.home(enableMenuGesture: true)
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
        delegate?.home(enableMenuGesture: false)
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = segue.destination as? WorkViewController,
           let indexPath = tableView.indexPathForSelectedRow {
            controller.work = tableViewWorks[indexPath.row]
            tableView.deselectRow(at: indexPath, animated: true)
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
        postData?["wt"] = 9
        postData?["search"] = searchBar.text
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
            postData?["wt"] = 2
        case .newWork:
            postData?["wt"] = 4
        case .myWork:
            postData?["wt"] = 5
        case .normal:
            postData?["wt"] = 8
            postData?["tagName"] = tag
        case .myCollection:
            postData?["wt"] = 7
        default:
            return
        }
        downloadData()
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
        dataRequest?.cancel()
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.WORK_LIST),
                method: .post,
                parameters: postData,
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], data["res"] as? Int == 1, let workList = data["workList"] as? [[String: Any]] else {
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
                for work in workList {
                    self.works.append(Work(
                            id: work["worksId"] as? Int,
                            profileImage: URL(string: Service.filePath(withSubPath: work["profilePicture"] as? String)),
                            profileName: work["userName"] as? String,
                            thumbnail: URL(string: Service.filePath(withSubPath: work["imagePath"] as? String)),
                            file: URL(string: Service.filePath(withSubPath: work["bdwPath"] as? String)),
                            title: work["title"] as? String,
                            isLike: work["like"] as? Bool,
                            isCollection: work["collection"] as? Bool,
                            likes: work["likeCount"] as? Int))
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
        cell.profileName.text = work.profileName
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
        if let isCollection = work.isCollection {
            if isCollection {
                cell.collectButton.isSelected = true
                cell.collectButton.setImage(collectionImageSelected, for: .normal)
            } else {
                cell.collectButton.isSelected = false
                cell.collectButton.setImage(collectionImage, for: .normal)
            }
        }
        cell.likes.text = "\(work.likes ?? 0)" + "likes".localized
        return cell
    }

    @IBAction func more(_ sender: UIButton) {
        if let indexPath = tableView.indexPath(forView: sender) {
            Logger.d("\(#function) \(indexPath.row)")
        }
    }

    @IBAction func like(_ sender: UIButton) {
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
        if let indexPath = tableView.indexPath(forView: sender),
           let id = tableViewWorks[indexPath.row].id {
            sender.isEnabled = false
            Alamofire.request(
                    Service.standard(withPath: Service.SET_LIKE),
                    method: .post,
                    parameters: ["ui": userId, "lk": token, "dt": SERVICE_DEVICE_TYPE, "fn": sender.isSelected ? 0 : 1, "worksId": id, "likeType": 1],
                    encoding: JSONEncoding.default).validate().responseJSON {
                response in
                sender.isEnabled = true
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
                        sender.isSelected = !sender.isSelected
                        var work = self.tableViewWorks[indexPath.row]
                        work.isLike = sender.isSelected
                        if let likes = work.likes {
                            work.likes = likes + (sender.isSelected ? 1 : -1)
                        }
                        self.tableViewWorks[indexPath.row] = work
                        if let cell = self.tableView.cellForRow(at: indexPath) as? HomeTableViewCell {
                            cell.likeButton.setImage(sender.isSelected ? self.likeImageSelected : self.likeImage, for: .normal)
                            cell.likes.text = "\(work.likes ?? 0)" + "likes".localized
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

    @IBAction func comment(_ sender: UIButton) {
        if let indexPath = tableView.indexPath(forView: sender) {
            Logger.d("\(#function) \(indexPath.row)")
        }
    }

    @IBAction func share(_ sender: UIButton) {
        if let indexPath = tableView.indexPath(forView: sender) {
            Logger.d("\(#function) \(indexPath.row)")
        }
    }

    @IBAction func collect(_ sender: UIButton) {
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
        if let indexPath = tableView.indexPath(forView: sender),
           let id = tableViewWorks[indexPath.row].id {
            sender.isEnabled = false
            Alamofire.request(
                    Service.standard(withPath: Service.SET_COLLECTION),
                    method: .post,
                    parameters: ["ui": userId, "lk": token, "dt": SERVICE_DEVICE_TYPE, "fn": sender.isSelected ? 0 : 1, "worksId": id, "likeType": 1],
                    encoding: JSONEncoding.default).validate().responseJSON {
                response in
                sender.isEnabled = true
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
                        sender.isSelected = !sender.isSelected
                        self.tableViewWorks[indexPath.row].isCollection = sender.isSelected
                        if let cell = self.tableView.cellForRow(at: indexPath) as? HomeTableViewCell {
                            cell.collectButton.setImage(sender.isSelected ? self.collectionImageSelected : self.collectionImage, for: .normal)
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

    @IBAction func didTapMenu(_ sender: Any) {
        delegate?.homeDidTapMenu()
    }
}

protocol HomeViewControllerDelegate {
    func homeDidTapMenu()

    func home(enableMenuGesture enable: Bool)
}

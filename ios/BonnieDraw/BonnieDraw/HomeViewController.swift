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
    private var backButton: UIBarButtonItem?
    private let searchBar = UISearchBar()
    private let titleView = Bundle.main.loadView(from: "TitleView")

    override func viewDidLoad() {
        backButton = UIBarButtonItem(image: UIImage(named: "menu_ic_out"), style: .plain, target: self, action: #selector(didTapMenu))
        navigationItem.leftBarButtonItem = backButton
        titleView?.backgroundColor = .clear
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
        tableView.contentInset = UIEdgeInsetsMake(0, 0, 44, 0)
        tableView.rowHeight = UITableViewAutomaticDimension
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
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = segue.destination as? WorkViewController,
           let indexPath = tableView.indexPathForSelectedRow {
            controller.work = tableViewWorks[indexPath.row]
            tableView.deselectRow(at: indexPath, animated: true)
        }
    }

    @objc internal func search() {
        if navigationItem.titleView == titleView {
            delegate?.home(enableMenuGesture: false)
            navigationItem.setLeftBarButton(nil, animated: true)
            navigationItem.titleView = searchBar
            searchBar.becomeFirstResponder()
        } else {
            delegate?.home(enableMenuGesture: true)
            navigationItem.setLeftBarButton(backButton, animated: true)
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
        guard let userId = UserDefaults.standard.string(forKey: Default.USER_ID),
              let token = UserDefaults.standard.string(forKey: Default.TOKEN) else {
            return
        }
        loading.hide(false)
        works.removeAll()
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.WORK_LIST),
                method: .post,
                parameters: ["ui": userId, "lk": token, "dt": SERVICE_DEVICE_TYPE, "wt": 2, "stn": 1, "rc": 128],
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
                for work in workList {
                    self.works.append(Work(
                            id: work["worksId"] as? Int,
                            profileImage: nil,
                            profileName: work["userName"] as? String,
                            thumbnail: URL(string: Service.filePath(withSubPath: work["imagePath"] as? String)),
                            file: URL(string: Service.filePath(withSubPath: work["bdwPath"] as? String)),
                            title: work["title"] as? String,
                            likes: work["likeCount"] as? Int))
                }
                self.tableViewWorks = self.works
                self.tableView.reloadSections([0], with: .automatic)
                self.emptyLabel.isHidden = !self.tableViewWorks.isEmpty
                self.loading.hide(true)
                self.timestamp = Date()
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

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tableViewWorks.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let work = tableViewWorks[indexPath.row]
        let cell = tableView.dequeueReusableCell(withIdentifier: Cell.HOME, for: indexPath) as! HomeTableViewCell
        cell.profileImage.setImage(with: work.profileImage)
        cell.profileName.text = work.profileName
        cell.title.text = work.title
        cell.thumbnail?.setImage(with: work.thumbnail)
        cell.likes.text = "\(work.likes ?? 0)" + "likes".localized
//        let lastComment = NSMutableAttributedString(string: item.lastCommentProfileName + "\t")
//        lastComment.append(NSAttributedString(string: item.lastComment, attributes: commentTextAttributes))
//        cell.lastComment.attributedText = lastComment
//        let secondLastComment = NSMutableAttributedString(string: item.secondLastCommentProfileName + "\t")
//        secondLastComment.append(NSAttributedString(string: item.secondLastComment, attributes: commentTextAttributes))
//        cell.secondLastComment.attributedText = secondLastComment
//        cell.lastCommentDate.text = "\(item.lastCommentDate)"
        return cell
    }

    @IBAction func more(_ sender: UIButton) {
        if let indexPath = tableView.indexPath(forView: sender) {
            Logger.d("\(#function) \(indexPath.row)")
        }
    }

    @IBAction func like(_ sender: UIButton) {
        if let indexPath = tableView.indexPath(forView: sender) {
            Logger.d("\(#function) \(indexPath.row)")
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

    @objc func didTapMenu() {
        delegate?.homeDidTapMenu()
    }
}

protocol HomeViewControllerDelegate {
    func homeDidTapMenu()

    func home(enableMenuGesture enable: Bool)
}

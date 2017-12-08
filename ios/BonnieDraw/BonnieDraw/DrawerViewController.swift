//
//  DrawerViewController.swift
//  BonnieDraw
//
//  Created by Professor on 22/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class DrawerViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    enum TagType: Int {
        case popularWork, newWork, myWork, normal, myCollection, account, signOut
    }

    @IBOutlet weak var navigationBar: UINavigationBar!
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var profileName: UILabel!
    var delegate: DrawerViewControllerDelegate?
    private let startingTags = [Tag(type: .popularWork, image: "menu_ic_hotDraw", title: "menu_popular_works".localized),
                                Tag(type: .newWork, image: "menu_ic_newDraw", title: "menu_new_works".localized),
                                Tag(type: .myWork, image: "menu_ic_myDraw", title: "menu_my_works".localized)]
    private let endingTags = [Tag(type: .myCollection, image: "collect_ic_off", title: "menu_my_collection".localized),
                              Tag(type: .account, image: "menu_ic_account", title: "menu_account".localized),
                              Tag(type: .signOut, image: "menu_ic_out", title: "menu_sign_out".localized)]
    private let refreshControl = UIRefreshControl()
    private var dataRequest: DataRequest?
    private var timestamp: Date?
    private var tags = [Tag]()

    override func viewDidLoad() {
        if DEBUG {
            navigationBar.items?.first?.leftBarButtonItem = UIBarButtonItem(title: "Debug", style: .plain, target: self, action: #selector(debug))
        }
        if let url = UserDefaults.standard.url(forKey: Default.THIRD_PARTY_IMAGE) {
            profileImage.setImage(with: url)
        }
        profileName.text = UserDefaults.standard.string(forKey: Default.THIRD_PARTY_NAME)
        tableView.refreshControl = refreshControl
        downloadData()
    }

    override func viewDidAppear(_ animated: Bool) {
        if tags.isEmpty {
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
                Service.standard(withPath: Service.TAG_LIST),
                method: .post,
                parameters: ["ui": userId, "lk": token, "dt": SERVICE_DEVICE_TYPE],
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], data["res"] as? Int == 1, let tagList = data["tagList"] as? [[String: Any]] else {
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
                self.tags.removeAll()
                self.tags.append(contentsOf: self.startingTags)
                for tag in tagList {
                    self.tags.append(Tag(type: .normal, image: "left_menu_icon_1", title: tag["tagName"] as? String))
                }
                self.tags.append(contentsOf: self.endingTags)
                self.tableView.reloadSections([0], with: .automatic)
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
        return tags.count
    }

    internal func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: Cell.BASIC, for: indexPath)
        cell.imageView?.image = UIImage(named: tags[indexPath.row].image)
        cell.textLabel?.text = tags[indexPath.row].title
        if indexPath.row != 2 && indexPath.row != tags.count - 4 {
            cell.separatorInset = UIEdgeInsetsMake(0, 0, 0, tableView.bounds.width)
        }
        return cell
    }

    internal func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let tag = tags[indexPath.row]
        delegate?.drawer(didSelectType: tag.type, withTag: tag.title)
        tableView.deselectRow(at: indexPath, animated: true)
    }

    @objc private func debug() {
        performSegue(withIdentifier: Segue.DEBUG, sender: nil)
    }

    @IBAction func dismiss(_ sender: Any) {
        delegate?.drawerDidTapDismiss()
    }

    private struct Tag {
        let type: TagType
        let image: String
        let title: String?
    }
}

protocol DrawerViewControllerDelegate {
    func drawerDidTapDismiss()

    func drawer(didSelectType type: DrawerViewController.TagType, withTag tag: String?)
}

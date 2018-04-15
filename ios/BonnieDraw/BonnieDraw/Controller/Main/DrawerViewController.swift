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
    private var timestamp = Date()
    private var tags = [Tag]()

    override func viewDidLoad() {
        if DEBUG {
            navigationBar.items?.first?.leftBarButtonItem = UIBarButtonItem(title: "Debug", style: .plain, target: self, action: #selector(debug))
        }
        if let url = UserDefaults.standard.url(forKey: Default.IMAGE) {
            profileImage.setImage(with: url)
        }
        profileName.text = UserDefaults.standard.string(forKey: Default.NAME)
        UserDefaults.standard.addObserver(self, forKeyPath: Default.IMAGE, options: .new, context: nil)
        UserDefaults.standard.addObserver(self, forKeyPath: Default.NAME, options: .new, context: nil)
        tableView.refreshControl = refreshControl
        downloadData()
    }

    override func viewDidAppear(_ animated: Bool) {
        if tags.isEmpty {
            downloadData()
        } else if Date().timeIntervalSince1970 - timestamp.timeIntervalSince1970 > UPDATE_INTERVAL {
            downloadData()
        } else {
            loading.hide(true)
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
    }

    override func viewDidDisappear(_ animated: Bool) {
        refreshControl.endRefreshing()
    }

    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey: Any]?, context: UnsafeMutableRawPointer?) {
        if keyPath == Default.IMAGE {
            if let url = UserDefaults.standard.url(forKey: Default.IMAGE) {
                profileImage.setImage(with: url)
            }
        } else if keyPath == Default.NAME {
            profileName.text = UserDefaults.standard.string(forKey: Default.NAME)
        }
    }

    private func downloadData() {
        let completionHandler: ([[String: Any]]?) -> Void = {
            tagList in
            self.tags.removeAll()
            self.tags.append(contentsOf: self.startingTags)
            if let tagList = tagList {
                for tag in tagList {
                    self.tags.append(Tag(type: .normal, image: "left_menu_icon_1", title: tag["tagName"] as? String))
                }
            }
            self.tags.append(contentsOf: self.endingTags)
            self.tableView.reloadSections([0], with: .automatic)
            if !self.loading.isHidden {
                self.loading.hide(true)
            }
            self.timestamp = Date()
            self.refreshControl.endRefreshing()
        }
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "alert_network_unreachable_title".localized, message: "alert_network_unreachable_content".localized)
            completionHandler(nil)
            return
        }
        guard let token = UserDefaults.standard.string(forKey: Default.TOKEN) else {
            completionHandler(nil)
            return
        }
        loading.hide(false)
        dataRequest?.cancel()
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.TAG_LIST),
                method: .post,
                parameters: ["ui": UserDefaults.standard.integer(forKey: Default.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE],
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            if let data = response.result.value as? [String: Any],
               data["res"] as? Int == 1 {
                completionHandler(data["tagList"] as? [[String: Any]])
                return
            }
            completionHandler(nil)
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

    deinit {
        UserDefaults.standard.removeObserver(self, forKeyPath: Default.IMAGE)
        UserDefaults.standard.removeObserver(self, forKeyPath: Default.NAME)
    }
}

protocol DrawerViewControllerDelegate {
    func drawerDidTapDismiss()

    func drawer(didSelectType type: DrawerViewController.TagType, withTag tag: String?)
}

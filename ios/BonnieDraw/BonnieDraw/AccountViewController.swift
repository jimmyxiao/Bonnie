//
//  AccountViewController.swift
//  BonnieDraw
//
//  Created by Professor on 27/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class AccountViewController:
        UIViewController,
        UICollectionViewDataSource,
        UICollectionViewDelegate,
        UICollectionViewDelegateFlowLayout,
        SettingViewControllerDelegate,
        UserViewControllerDelegate,
        AccountEditViewControllerDelegate,
        WorkViewControllerDelegate,
        CommentViewControllerDelegate {
    @IBOutlet weak var collectionView: UICollectionView!
    private var indicator: UIActivityIndicatorView?
    private var loadingLabel: UILabel?
    private var dataRequest: DataRequest?
    private var timestamp = Date()
    private var cellId = Cell.ACCOUNT_GRID {
        didSet {
            collectionView.reloadSections([0])
        }
    }
    var profile: Profile?
    var works = [Work]()
    private let refreshControl = UIRefreshControl()
    private let placeholderImage = UIImage(named: "photo-square")
    private let likeImage = UIImage(named: "work_ic_like")
    private let likeImageSelected = UIImage(named: "work_ic_like_on")
    private let collectionImage = UIImage(named: "collect_ic_off")
    private let collectionImageSelected = UIImage(named: "collect_ic_on")

    override func viewDidLoad() {
        collectionView.refreshControl = refreshControl
    }

    override func viewDidAppear(_ animated: Bool) {
        if profile == nil {
            downloadData()
        } else if Date().timeIntervalSince1970 - timestamp.timeIntervalSince1970 > UPDATE_INTERVAL {
            downloadData()
        } else {
            loadingLabel?.text = works.isEmpty ? "empty_data".localized : nil
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
    }

    @IBAction func didSelectAccount(_ sender: Any) {
        collectionView.setContentOffset(.zero, animated: true)
    }

    @IBAction func didSelectSetting(_ sender: Any) {
        performSegue(withIdentifier: Segue.SETTING, sender: nil)
    }

    @IBAction func didSelectRecommend(_ sender: Any) {
        performSegue(withIdentifier: Segue.RECOMMEND, sender: nil)
    }

    @IBAction func didSelectListLayout(_ sender: Any) {
        if cellId != Cell.ACCOUNT_LIST {
            cellId = Cell.ACCOUNT_LIST
        }
    }

    @IBAction func didSelectGridLayout(_ sender: Any) {
        if cellId != Cell.ACCOUNT_GRID {
            cellId = Cell.ACCOUNT_GRID
        }
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = segue.destination as? SettingViewController {
            controller.delegate = self
        } else if let controller = segue.destination as? AccountEditViewController {
            controller.delegate = self
            controller.profile = profile
        } else if let controller = segue.destination as? WorkViewController,
                  let indexPath = collectionView.indexPathsForSelectedItems?.first {
            controller.delegate = self
            controller.work = works[indexPath.row]
            collectionView.deselectItem(at: indexPath, animated: true)
        } else if let controller = segue.destination as? CommentViewController,
                  let indexPath = sender as? IndexPath {
            controller.delegate = self
            controller.work = works[indexPath.row]
        } else if let controller = segue.destination as? UserViewController {
            controller.delegate = self
            switch segue.identifier {
            case Segue.FAN?:
                controller.type = .fan
                controller.title = "account_fan".localized
            case Segue.FOLLOW?:
                controller.type = .follow
                controller.title = "account_follow".localized
            default:
                break
            }
        }
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
        guard let token = UserDefaults.standard.string(forKey: Default.TOKEN) else {
            return
        }
        let userId = UserDefaults.standard.integer(forKey: Default.USER_ID)
        loadingLabel?.text = "loading".localized
        indicator?.startAnimating()
        dataRequest?.cancel()
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.USER_INFO_QUERY),
                method: .post,
                parameters: ["ui": userId, "lk": token, "dt": SERVICE_DEVICE_TYPE],
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
                self.profile = Profile(withDictionary: data)
                UserDefaults.standard.set(self.profile?.image, forKey: Default.IMAGE)
                UserDefaults.standard.set(self.profile?.name, forKey: Default.NAME)
                self.dataRequest = Alamofire.request(
                        Service.standard(withPath: Service.WORK_LIST),
                        method: .post,
                        parameters: ["ui": userId, "lk": token, "dt": SERVICE_DEVICE_TYPE, "wt": 5, "stn": 1, "rc": 128],
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
                        self.navigationItem.leftBarButtonItem?.title = self.profile?.name
                        self.collectionView.reloadSections([0])
                        self.loadingLabel?.text = self.works.isEmpty ? "empty_data".localized : nil
                        self.indicator?.stopAnimating()
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

    internal func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return works.count
    }

    internal func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        if kind == UICollectionElementKindSectionHeader {
            let headerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: Cell.ACCOUNT_HEADER, for: indexPath) as! AccountHeaderCollectionReusableView
            headerView.profileImage.setImage(with: UserDefaults.standard.url(forKey: Default.IMAGE))
            headerView.profileName.text = profile?.name
            headerView.worksCount.text = "\(profile?.worksCount ?? 0)"
            headerView.fansCount.text = "\(profile?.fansCount ?? 0)"
            headerView.followsCount.text = "\(profile?.followsCount ?? 0)"
            return headerView
        } else {
            let footerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: Cell.ACCOUNT_FOOTER, for: indexPath) as! AccountFooterCollectionReusableView
            indicator = footerView.indicator
            loadingLabel = footerView.label
            return footerView
        }
    }

    internal func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: cellId, for: indexPath)
        if let cell = cell as? AccountGridCollectionViewCell {
            cell.thumbnail.setImage(with: works[indexPath.row].thumbnail)
        } else if let cell = cell as? AccountListCollectionViewCell {
            let work = works[indexPath.row]
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
        }
        return cell
    }

    internal func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        if cellId == Cell.ACCOUNT_GRID {
            let width = collectionView.bounds.width / CGFloat(3)
            return CGSize(width: width, height: width)
        } else {
            return CGSize(width: collectionView.bounds.width, height: 156 + collectionView.bounds.width * 3 / 4)
        }
    }

    internal func settings(profileDidChange profile: Profile) {
        self.profile = profile
        navigationItem.leftBarButtonItem?.title = profile.name
        collectionView.reloadSections([0])
    }

    internal func settings(imageDidChange image: UIImage) {
        collectionView.reloadSections([0])
    }

    internal func user(didFollowUser follow: Bool) {
        if var followsCount = profile?.followsCount {
            followsCount += (follow ? 1 : -1)
            profile?.followsCount = followsCount
            collectionView.reloadSections([0])
        }
    }

    internal func accountEdit(profileDidChange profile: Profile) {
        self.profile = profile
        navigationItem.leftBarButtonItem?.title = profile.name
        collectionView.reloadSections([0])
    }

    internal func accountEdit(imageDidChange image: UIImage) {
        collectionView.reloadSections([0])
    }

    internal func work(didChange changedWork: Work) {
        if let index = works.index(where: {
            work in
            return work.id == changedWork.id
        }) {
            works[index] = changedWork
            collectionView.reloadSections([0])
        }
    }

    internal func comment(didCommentOnWork changedWork: Work) {
        if let index = works.index(where: {
            work in
            return work.id == changedWork.id
        }) {
            works[index] = changedWork
            collectionView.reloadItems(at: [IndexPath(row: index, section: 0)])
        }
    }

    @IBAction func more(_ sender: UIButton) {
        if let indexPath = collectionView.indexPath(forView: sender) {
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
    }

    @IBAction func like(_ sender: UIButton) {
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
            return
        }
        guard let token = UserDefaults.standard.string(forKey: Default.TOKEN),
              let indexPath = collectionView.indexPath(forView: sender),
              let id = works[indexPath.row].id else {
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
                        if let cell = self.collectionView.cellForItem(at: IndexPath(row: index, section: 0)) as? AccountListCollectionViewCell {
                            cell.likeButton.isSelected = like
                            cell.likeButton.setImage(like ? self.likeImageSelected : self.likeImage, for: .normal)
                            if let likes = self.works[index].likes, likes > 0 {
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
        if let indexPath = collectionView.indexPath(forView: sender) {
            performSegue(withIdentifier: Segue.COMMENT, sender: indexPath)
        }
    }

    @IBAction func share(_ sender: UIButton) {
        if let indexPath = collectionView.indexPath(forView: sender) {
            Logger.d("\(#function) \(indexPath.row)")
        }
    }

    @IBAction func collect(_ sender: UIButton) {
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
            return
        }
        guard let token = UserDefaults.standard.string(forKey: Default.TOKEN),
              let indexPath = collectionView.indexPath(forView: sender),
              let id = works[indexPath.row].id else {
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
                        if let cell = self.collectionView.cellForItem(at: IndexPath(row: index, section: 0)) as? AccountListCollectionViewCell {
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

//
//  AccountViewController.swift
//  BonnieDraw
//
//  Created by Professor on 27/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class AccountViewController: UIViewController, UICollectionViewDataSource, UICollectionViewDelegate, UICollectionViewDelegateFlowLayout {
    @IBOutlet weak var collectionView: UICollectionView!
    private var indicator: UIActivityIndicatorView?
    private var loadingLabel: UILabel?
    private var dataRequest: DataRequest?
    private var timestamp: Date?
    private var cellId = Cell.ACCOUNT_GRID {
        didSet {
            collectionView.reloadSections([0])
        }
    }
    var user: Profile?
    var works = [Work]()
    private let refreshControl = UIRefreshControl()

    override func viewDidLoad() {
        navigationItem.leftBarButtonItem = UIBarButtonItem(title: nil, style: .plain, target: self, action: #selector(didSelectAccount))
        navigationItem.leftBarButtonItem?.tintColor = .black
        navigationItem.rightBarButtonItems =
                [UIBarButtonItem(image: UIImage(named: "personal_ic_shape"), style: .plain, target: self, action: #selector(didSelectSetting)),
                 UIBarButtonItem(image: UIImage(named: "recommend_icon"), style: .plain, target: self, action: #selector(didSelectRecommend)),
                 UIBarButtonItem(image: UIImage(named: "collect_ic_off"), style: .plain, target: self, action: #selector(didSelectColllection)),
                 UIBarButtonItem(image: UIImage(named: "personal_ic_list"), style: .plain, target: self, action: #selector(didSelectListLayout)),
                 UIBarButtonItem(image: UIImage(named: "personal_ic_rectangle"), style: .plain, target: self, action: #selector(didSelectGridLayout))]
        collectionView.refreshControl = refreshControl
    }

    override func viewDidAppear(_ animated: Bool) {
        if let timestamp = timestamp {
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

    @objc func didSelectAccount() {
        collectionView.setContentOffset(.zero, animated: true)
    }

    @objc func didSelectGridLayout() {
        if cellId != Cell.ACCOUNT_GRID {
            cellId = Cell.ACCOUNT_GRID
        }
    }

    @objc func didSelectListLayout() {
        if cellId != Cell.ACCOUNT_LIST {
            cellId = Cell.ACCOUNT_LIST
        }
    }

    @objc func didSelectSetting() {
        performSegue(withIdentifier: Segue.SETTING, sender: nil)
    }

    @objc func didSelectColllection() {
        performSegue(withIdentifier: Segue.COLLECTION, sender: nil)
    }

    @objc func didSelectRecommend() {
        performSegue(withIdentifier: Segue.RECOMMEND, sender: nil)
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = segue.destination as? WorkViewController,
           let indexPath = collectionView.indexPathsForSelectedItems?.first {
            controller.work = works[indexPath.row]
            collectionView.deselectItem(at: indexPath, animated: true)
        } else if let controller = segue.destination as? UserViewController {
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
        loadingLabel?.text = "loading".localized
        indicator?.startAnimating()
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
                self.user = Profile(
                        type: UserType(rawValue: data["userType"] as? Int ?? -1),
                        code: data["userCode"] as? String,
                        name: data["userName"] as? String,
                        email: data["email"] as? String,
                        worksCount: data["worksNum"] as? Int,
                        fansCount: data["fansNum"] as? Int,
                        followsCount: data["followNum"] as? Int)
                self.dataRequest = Alamofire.request(
                        Service.standard(withPath: Service.WORK_LIST),
                        method: .post,
                        parameters: ["ui": userId, "lk": token, "dt": SERVICE_DEVICE_TYPE, "wt": 5, "stn": 1, "rc": 128],
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
                                    profileImage: nil,
                                    profileName: work["userName"] as? String,
                                    thumbnail: URL(string: Service.filePath(withSubPath: work["imagePath"] as? String)),
                                    file: URL(string: Service.filePath(withSubPath: work["bdwPath"] as? String)),
                                    title: work["title"] as? String,
                                    isLike: work["like"] as? Bool,
                                    isCollection: work["collection"] as? Bool,
                                    likes: work["likeCount"] as? Int))
                        }
                        self.navigationItem.leftBarButtonItem?.title = self.user?.name
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
            headerView.profileImage.setImage(with: UserDefaults.standard.url(forKey: Default.THIRD_PARTY_IMAGE))
            headerView.profileName.text = user?.name
            headerView.worksCount.text = "\(user?.worksCount ?? 0)"
            headerView.fansCount.text = "\(user?.fansCount ?? 0)"
            headerView.followsCount.text = "\(user?.followsCount ?? 0)"
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
            cell.profileImage.setImage(with: work.profileImage)
            cell.profileName.text = work.profileName
            cell.title.text = work.title
            cell.thumbnail?.setImage(with: work.thumbnail)
            cell.likes.text = "\(work.likes ?? 0)" + "likes".localized
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
}

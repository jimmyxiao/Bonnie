//
//  CollectionAllViewController.swift
//  BonnieDraw
//
//  Created by Professor on 22/11/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import XLPagerTabStrip
import Alamofire

class CollectionAllViewController: UIViewController, UICollectionViewDataSource, UICollectionViewDelegate, UICollectionViewDelegateFlowLayout, IndicatorInfoProvider, WorkViewControllerDelegate {
    @IBOutlet weak var collectionView: UICollectionView!
    @IBOutlet weak var emptyLabel: UILabel!
    @IBOutlet weak var loading: LoadingIndicatorView!
    private var dataRequest: DataRequest?
    private var timestamp = Date()
    private var works = [Work]()
    private let refreshControl = UIRefreshControl()

    override func viewDidLoad() {
        collectionView.refreshControl = refreshControl
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
           let indexPath = collectionView.indexPathsForSelectedItems?.first {
            controller.delegate = self
            controller.work = works[indexPath.row]
            collectionView.deselectItem(at: indexPath, animated: true)
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
        dataRequest?.cancel()
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.WORK_LIST),
                method: .post,
                parameters: ["ui": UserDefaults.standard.integer(forKey: Default.USER_ID), "lk": token, "dt": SERVICE_DEVICE_TYPE, "wt": 7, "stn": 1, "rc": 128],
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
                self.collectionView.reloadSections([0])
                self.collectionView.isHidden = self.works.isEmpty
                self.emptyLabel.isHidden = !self.works.isEmpty
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

    internal func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return works.count
    }

    internal func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: Cell.ACCOUNT_GRID, for: indexPath) as! AccountGridCollectionViewCell
        cell.thumbnail.setImage(with: works[indexPath.row].thumbnail)
        return cell
    }

    internal func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        return collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: Cell.ACCOUNT_HEADER, for: indexPath)
    }

    internal func indicatorInfo(for pagerTabStripController: PagerTabStripViewController) -> IndicatorInfo {
        return IndicatorInfo(title: "collection_tab_all".localized)
    }

    internal func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        let width = collectionView.bounds.width / CGFloat(3)
        return CGSize(width: width, height: width)
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
}

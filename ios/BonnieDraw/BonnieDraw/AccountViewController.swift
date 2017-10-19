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
    var works = [Work]()

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

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = segue.destination as? WorkViewController,
           let indexPath = collectionView.indexPathsForSelectedItems?.first {
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
        guard let userId = UserDefaults.standard.string(forKey: Default.USER_ID),
              let token = UserDefaults.standard.string(forKey: Default.TOKEN) else {
            return
        }
        loadingLabel?.text = "loading".localized
        indicator?.startAnimating()
        works.removeAll()
        dataRequest = Alamofire.request(
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
                self.collectionView.reloadData()
                self.loadingLabel?.text = self.works.isEmpty ? "empty_data".localized : nil
                self.indicator?.stopAnimating()
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

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return works.count
    }

    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        if kind == UICollectionElementKindSectionHeader {
            let headerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: Cell.ACCOUNT_HEADER, for: indexPath) as! AccountHeaderCollectionReusableView
            if let url = UserDefaults.standard.url(forKey: Default.THIRD_PARTY_IMAGE) {
                headerView.profileImage.setImage(with: url)
            }
            headerView.profileName.text = UserDefaults.standard.string(forKey: Default.THIRD_PARTY_NAME)
            return headerView
        } else {
            let footerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: Cell.ACCOUNT_FOOTER, for: indexPath) as! AccountFooterCollectionReusableView
            indicator = footerView.indicator
            loadingLabel = footerView.label
            return footerView
        }
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: Cell.ACCOUNT, for: indexPath) as! AccountCollectionViewCell
        cell.thumbnail.setImage(with: works[indexPath.row].thumbnail)
        return cell
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        let width = collectionView.frame.width / CGFloat(3)
        return CGSize(width: width, height: width)
    }
}

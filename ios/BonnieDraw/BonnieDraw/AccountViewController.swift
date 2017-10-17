//
//  AccountViewController.swift
//  BonnieDraw
//
//  Created by Professor on 27/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class AccountViewController: UIViewController, UICollectionViewDataSource, UICollectionViewDelegate, UICollectionViewDelegateFlowLayout {
    let items = [URL(string: "https://via.placeholder.com/128/\(AppDelegate.randomColor())"),
                 URL(string: "https://via.placeholder.com/128/\(AppDelegate.randomColor())"),
                 URL(string: "https://via.placeholder.com/128/\(AppDelegate.randomColor())"),
                 URL(string: "https://via.placeholder.com/128/\(AppDelegate.randomColor())"),
                 URL(string: "https://via.placeholder.com/128/\(AppDelegate.randomColor())"),
                 URL(string: "https://via.placeholder.com/128/\(AppDelegate.randomColor())"),
                 URL(string: "https://via.placeholder.com/128/\(AppDelegate.randomColor())"),
                 URL(string: "https://via.placeholder.com/128/\(AppDelegate.randomColor())"),
                 URL(string: "https://via.placeholder.com/128/\(AppDelegate.randomColor())"),
                 URL(string: "https://via.placeholder.com/128/\(AppDelegate.randomColor())")]

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return items.count
    }

    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        let headerView = collectionView.dequeueReusableSupplementaryView(ofKind: UICollectionElementKindSectionHeader, withReuseIdentifier: Cell.ACCOUNT_HEADER, for: indexPath) as! AccountHeaderCollectionReusableView
        if let profileImageUrl = UserDefaults.standard.string(forKey: Default.THIRD_PARTY_IMAGE) {
            headerView.profileImage.setImage(with: URL(string: profileImageUrl))
        } else {
            //            TODO: Set default profile image
        }
        headerView.profileName.text = UserDefaults.standard.string(forKey: Default.THIRD_PARTY_NAME)
        return headerView
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: Cell.ACCOUNT, for: indexPath) as! AccountCollectionViewCell
        cell.thumbnail.setImage(with: items[indexPath.row])
        return cell
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        let width = collectionView.frame.width / CGFloat(3)
        return CGSize(width: width, height: width)
    }
}

//
//  FollowViewController.swift
//  BonnieDraw
//
//  Created by Professor on 24/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import XLPagerTabStrip

class FollowViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, IndicatorInfoProvider {
    @IBOutlet weak var tableView: UITableView!
    var items = [TableViewItem(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                 TableViewItem(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                 TableViewItem(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                 TableViewItem(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                 TableViewItem(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                 TableViewItem(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                 TableViewItem(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                 TableViewItem(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                 TableViewItem(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                 TableViewItem(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                 TableViewItem(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                 TableViewItem(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                 TableViewItem(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                 TableViewItem(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false),
                 TableViewItem(iamgeUrl: URL(string: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())"), title: "Title", status: "Status", isSelected: false)]

    func indicatorInfo(for pagerTabStripController: PagerTabStripViewController) -> IndicatorInfo {
        return IndicatorInfo(title: title)
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: Cell.FOLLOW, for: indexPath) as? FollowTableViewCell {
            let item = items[indexPath.row]
            cell.thumbnail.setImage(with: item.iamgeUrl)
            cell.title.text = item.title
            cell.status.text = item.status
            cell.follow.isSelected = item.isSelected
            return cell
        }
        return UITableViewCell()
    }

    @IBAction func selectFollow(_ sender: FollowButton) {
        sender.isSelected = !sender.isSelected
        if let indexPath = tableView.indexPath(forView: sender) {
            items[indexPath.row].isSelected = sender.isSelected
        }
    }

    struct TableViewItem {
        let iamgeUrl: URL?
        let title: String?
        let status: String?
        var isSelected: Bool
    }
}

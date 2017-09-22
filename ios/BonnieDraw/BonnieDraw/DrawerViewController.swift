//
//  DrawerViewController.swift
//  BonnieDraw
//
//  Created by Professor on 22/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class DrawerViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    enum DrawerItem: Int {
        case popularWork, newWork, myWork, category1, category2, category3, account, signOut
    }

    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var profileName: UILabel!
    var delegate: DrawerViewControllerDelegate?
    private let items = [TableViewItem(image: "menu_ic_hotDraw", title: "menu_popular".localized),
                         TableViewItem(image: "menu_ic_newDraw", title: "menu_new".localized),
                         TableViewItem(image: "menu_ic_myDraw", title: "menu_my".localized),
                         TableViewItem(image: "menu_ic_category_1", title: "menu_category1".localized),
                         TableViewItem(image: "menu_ic_category_2", title: "menu_category2".localized),
                         TableViewItem(image: "menu_ic_category_3", title: "menu_category3".localized),
                         TableViewItem(image: "menu_ic_hotDraw", title: "menu_account".localized),
                         TableViewItem(image: "menu_ic_out", title: "menu_sign_out".localized)]

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: Cell.BASIC, for: indexPath)
        let item = items[indexPath.row]
        cell.textLabel?.text = item.title
        cell.imageView?.image = UIImage(named: item.image)
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        delegate?.drawer(didSelectRowAt: indexPath)
    }

    @IBAction func drawerDismiss(_ sender: Any) {
        delegate?.drawerDidTapDismiss()
    }

    private struct TableViewItem {
        let image: String
        let title: String
    }
}

protocol DrawerViewControllerDelegate {
    func drawerDidTapDismiss()

    func drawer(didSelectRowAt indexPath: IndexPath)
}

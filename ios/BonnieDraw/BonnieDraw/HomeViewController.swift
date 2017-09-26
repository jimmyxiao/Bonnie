//
//  HomeViewController.swift
//  BonnieDraw
//
//  Created by Professor on 26/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class HomeViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    @IBOutlet weak var tableView: UITableView!
    private let commentTextAttributes = [NSAttributedStringKey.foregroundColor: UIColor.lightGray]
    private let items = [TableViewItem(
            id: Int(arc4random_uniform(100)),
            profileImage: "https://via.placeholder.com/128/\(AppDelegate.randomColor())",
            profileName: "Foo Bar",
            thumbnail: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())",
            title: "標題",
            likes: Int(arc4random_uniform(100)),
            lastCommentProfileName: "阿貓",
            lastComment: "喵喵喵～",
            secondLastCommentProfileName: "阿狗",
            secondLastComment: "汪汪汪汪！",
            lastCommentDate: Int(Date().timeIntervalSince1970) + Int(arc4random_uniform(100000))),
        TableViewItem(
                id: Int(arc4random_uniform(100)),
                profileImage: "https://via.placeholder.com/128/\(AppDelegate.randomColor())",
                profileName: "Foo Bar",
                thumbnail: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())",
                title: "標題",
                likes: Int(arc4random_uniform(100)),
                lastCommentProfileName: "阿貓",
                lastComment: "喵喵喵～",
                secondLastCommentProfileName: "阿狗",
                secondLastComment: "汪汪汪汪！",
                lastCommentDate: Int(Date().timeIntervalSince1970) + Int(arc4random_uniform(100000))),
        TableViewItem(
                id: Int(arc4random_uniform(100)),
                profileImage: "https://via.placeholder.com/128/\(AppDelegate.randomColor())",
                profileName: "Foo Bar",
                thumbnail: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())",
                title: "標題",
                likes: Int(arc4random_uniform(100)),
                lastCommentProfileName: "阿貓",
                lastComment: "喵喵喵～",
                secondLastCommentProfileName: "阿狗",
                secondLastComment: "汪汪汪汪！",
                lastCommentDate: Int(Date().timeIntervalSince1970) + Int(arc4random_uniform(100000))),
        TableViewItem(
                id: Int(arc4random_uniform(100)),
                profileImage: "https://via.placeholder.com/128/\(AppDelegate.randomColor())",
                profileName: "Foo Bar",
                thumbnail: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())",
                title: "標題",
                likes: Int(arc4random_uniform(100)),
                lastCommentProfileName: "阿貓",
                lastComment: "喵喵喵～",
                secondLastCommentProfileName: "阿狗",
                secondLastComment: "汪汪汪汪！",
                lastCommentDate: Int(Date().timeIntervalSince1970) + Int(arc4random_uniform(100000))),
        TableViewItem(
                id: Int(arc4random_uniform(100)),
                profileImage: "https://via.placeholder.com/128/\(AppDelegate.randomColor())",
                profileName: "Foo Bar",
                thumbnail: "https://via.placeholder.com/400x300/\(AppDelegate.randomColor())",
                title: "標題",
                likes: Int(arc4random_uniform(100)),
                lastCommentProfileName: "阿貓",
                lastComment: "喵喵喵～",
                secondLastCommentProfileName: "阿狗",
                secondLastComment: "汪汪汪汪！",
                lastCommentDate: Int(Date().timeIntervalSince1970) + Int(arc4random_uniform(100000)))]

    override func viewDidLoad() {
        tableView.contentInset = UIEdgeInsetsMake(0, 0, 44, 0)
        tableView.rowHeight = UITableViewAutomaticDimension
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let item = items[indexPath.row]
        let cell = tableView.dequeueReusableCell(withIdentifier: Cell.HOME, for: indexPath) as! HomeTableViewCell
        cell.profileImage.setImage(with: URL(string: item.profileImage))
        cell.profileName.text = item.profileName
        cell.thumbnail?.setImage(with: URL(string: item.thumbnail))
        cell.likes.text = "\(item.likes)個讚"
        let lastComment = NSMutableAttributedString(string: item.lastCommentProfileName + "\t")
        lastComment.append(NSAttributedString(string: item.lastComment, attributes: commentTextAttributes))
        cell.lastComment.attributedText = lastComment
        let secondLastComment = NSMutableAttributedString(string: item.secondLastCommentProfileName + "\t")
        secondLastComment.append(NSAttributedString(string: item.secondLastComment, attributes: commentTextAttributes))
        cell.secondLastComment.attributedText = secondLastComment
        cell.lastCommentDate.text = "\(item.lastCommentDate)"
        return cell
    }

    @IBAction func more(_ sender: UIButton) {
    }

    @IBAction func like(_ sender: UIButton) {
    }

    @IBAction func comment(_ sender: UIButton) {
    }

    @IBAction func share(_ sender: UIButton) {
    }

    private struct TableViewItem {
        let id: Int
        let profileImage: String
        let profileName: String
        let thumbnail: String
        let title: String
        let likes: Int
        let lastCommentProfileName: String
        let lastComment: String
        let secondLastCommentProfileName: String
        let secondLastComment: String
        let lastCommentDate: Int
    }
}

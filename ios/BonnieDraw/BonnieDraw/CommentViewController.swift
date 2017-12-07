//
//  CommentViewController.swift
//  BonnieDraw
//
//  Created by Professor on 07/12/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CommentViewController: BackButtonViewController, UITableViewDataSource, UITableViewDelegate {
    @IBOutlet weak var emptyLabel: UILabel!
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var tableView: UITableView!

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 0
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return UITableViewCell()
    }
}

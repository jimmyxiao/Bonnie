//
//  DebugViewController.swift
//  BonnieDraw
//
//  Created by Professor on 07/11/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Crashlytics

class DebugViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    let items = ["Crash"]

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: Cell.BASIC, for: indexPath)
        cell.textLabel?.text = items[indexPath.row]
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        switch indexPath.row {
        case 0:
            Crashlytics.sharedInstance().crash()
        default:
            break
        }
        tableView.deleteRows(at: [indexPath], with: .automatic)
    }

    @IBAction func dismiss(_ sender: Any) {
        dismiss(animated: true)
    }
}

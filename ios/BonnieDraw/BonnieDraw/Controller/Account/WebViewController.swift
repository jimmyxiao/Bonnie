//
//  WebViewController.swift
//  Omna
//
//  Created by Jason Hsu 08329 on 3/7/17.
//  Copyright © 2017 D-Link. All rights reserved.
//

import UIKit
import WebKit

class WebViewController: BackButtonViewController, WKUIDelegate, WKNavigationDelegate {
    @IBOutlet weak var indicator: UIActivityIndicatorView!
    @IBOutlet weak var container: UIView!
    let keyPath = "loading"
    var webView: WKWebView?
    var url: URL?

    override func viewDidLoad() {
        let webView = WKWebView()
        webView.uiDelegate = self
        webView.navigationDelegate = self
        container.addAndFill(subView: webView)
        indicator.startAnimating()
        self.webView = webView
        if navigationController is LightStatusBarViewController {
            navigationItem.leftBarButtonItem?.tintColor = .white
            navigationItem.leftBarButtonItem?.image = UIImage(named: "top_bar_ic_back")
            indicator.color = .white
        }
    }

    override func viewWillAppear(_ animated: Bool) {
        webView?.addObserver(self, forKeyPath: keyPath, options: .new, context: nil)
    }

    override func viewDidAppear(_ animated: Bool) {
        loadPage()
    }

    override func viewWillDisappear(_ animated: Bool) {
        webView?.removeObserver(self, forKeyPath: keyPath)
    }

    func loadPage() {
        if AppDelegate.reachability.connection != .none {
            if let url = url {
                webView?.load(URLRequest(url: url))
            }
        } else {
            presentConfirmationDialog(title: "alert_network_unreachable_title".localized, message: "alert_network_unreachable_content".localized) {
                success in
                if success {
                    self.loadPage()
                } else {
                    self.onBackPressed(self)
                }
            }
        }
    }

    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey: Any]?, context: UnsafeMutableRawPointer?) {
        if keyPath == keyPath, let isLoading = change?[.newKey] as? Bool {
            if isLoading {
                indicator.startAnimating()
            } else {
                indicator.stopAnimating()
            }
        }
    }
}

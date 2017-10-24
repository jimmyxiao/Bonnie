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
    let keyPath = "loading"
    let indicator = UIActivityIndicatorView()
    var webView: WKWebView?
    var url: URL?

    override func viewDidLoad() {
        let webView = WKWebView()
        webView.uiDelegate = self
        webView.navigationDelegate = self
        webView.addObserver(self, forKeyPath: keyPath, options: .new, context: nil)
        view.addAndFill(subView: webView)
        navigationItem.rightBarButtonItem = UIBarButtonItem(customView: indicator)
        self.webView = webView
    }

    override func viewDidAppear(_ animated: Bool) {
        loadPage()
    }

    func loadPage() {
        if AppDelegate.reachability.connection != .none {
            if let url = url {
                webView?.load(URLRequest(url: url))
            }
        } else {
            presentConfirmationDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized) {
                success in
                if success {
                    self.loadPage()
                } else {
                    self.onBackPressed(self)
                }
            }
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        webView?.removeObserver(self, forKeyPath: keyPath)
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

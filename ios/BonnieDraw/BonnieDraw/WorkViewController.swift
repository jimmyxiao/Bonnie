//
//  WorkViewController.swift
//  BonnieDraw
//
//  Created by Professor on 16/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class WorkViewController: BackButtonViewController, URLSessionDelegate, CanvasViewDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var progressBar: UIProgressView!
    @IBOutlet weak var navigationBar: UINavigationBar!
    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var profileName: UILabel!
    @IBOutlet weak var canvas: CanvasView!
    @IBOutlet weak var play: UIButton!
    var work: Work?
    private var downloadRequest: DownloadRequest?

    override func viewDidLoad() {
        canvas.delegate = self
        if navigationBar.items?.first?.titleView == nil {
            let titleView = Bundle.main.loadView(from: "TitleView")
            titleView?.backgroundColor = .clear
            navigationBar.items?.first?.titleView = titleView
        }
        profileName.text = work?.profileName
    }

    override func viewDidAppear(_ animated: Bool) {
        downloadData()
    }

    override func viewWillDisappear(_ animated: Bool) {
        downloadRequest?.cancel()
        canvas.stop()
    }

    private func downloadData() {
        guard let fileUrl = work?.file,
              let destinationUrl = canvas.url else {
            return
        }
        progressBar.progress = 0
        loading.hide(false)
        downloadRequest = Alamofire.download(fileUrl) {
            _, _ in
            return (destinationUrl, [.removePreviousFile, .createIntermediateDirectories])
        }.downloadProgress() {
            progress in
            Logger.d(progress.fractionCompleted)
            self.progressBar.setProgress(Float(progress.fractionCompleted), animated: true)
        }.response() {
            response in
            guard response.error == nil else {
                self.presentConfirmationDialog(
                        title: "service_download_fail_title".localized,
                        message: "app_network_unreachable_content".localized) {
                    success in
                    if success {
                        self.downloadData()
                    } else {
                        self.dismiss(animated: true)
                    }
                }
                return
            }
            self.canvas.load()
            self.loading.hide(true)
        }
    }

    @IBAction func play(_ sender: UIButton) {
        sender.isEnabled = false
        canvas.play()
    }

    func canvasPathsDidFinishAnimation() {
        play.isEnabled = true
    }

    func canvasFileParseError() {
        presentDialog(title: "canvas_data_parse_error_title".localized, message: "canvas_data_parse_error_content".localized) {
            action in
            self.dismiss(animated: true)
        }
    }
}

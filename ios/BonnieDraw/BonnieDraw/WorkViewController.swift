//
//  WorkViewController.swift
//  BonnieDraw
//
//  Created by Professor on 16/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import Alamofire

class WorkViewController: BackButtonViewController, URLSessionDelegate, CanvasAnimationViewDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var canvasAnimation: CanvasAnimationView!
    @IBOutlet weak var progressBar: UIProgressView!
    @IBOutlet weak var navigationBar: UINavigationBar!
    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var profileName: UILabel!
    @IBOutlet weak var play: UIButton!
    var work: Work?
    private var downloadRequest: DownloadRequest?

    override func viewDidLoad() {
        do {
            canvasAnimation.url = try FileManager.default.url(
                    for: .documentationDirectory,
                    in: .userDomainMask,
                    appropriateFor: nil,
                    create: true).appendingPathComponent("download.bdw")
        } catch {
            Logger.d("\(#function): \(error.localizedDescription)")
        }
        canvasAnimation.delegate = self
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
        canvasAnimation.stop()
    }

    private func downloadData() {
        guard let fileUrl = work?.file,
              let destinationUrl = canvasAnimation.url else {
            return
        }
        progressBar.progress = 0
        loading.hide(false)
        downloadRequest = Alamofire.download(fileUrl) {
            _, _ in
            return (destinationUrl, [.removePreviousFile, .createIntermediateDirectories])
        }.downloadProgress() {
            progress in
            self.progressBar.setProgress(Float(progress.fractionCompleted), animated: true)
        }.response(queue: DispatchQueue.main) {
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
            DispatchQueue.main.async {
                self.canvasAnimation.load()
                self.loading.hide(true)
            }
        }
    }

    @IBAction func play(_ sender: UIButton) {
        if sender.isSelected {
            canvasAnimation.pause()
            sender.setImage(UIImage(named: "drawplay_ic_play"), for: .normal)
        } else {
            canvasAnimation.play()
            sender.setImage(UIImage(named: "drawplay_ic_timeout"), for: .normal)
        }
        sender.isSelected = !sender.isSelected
    }

    internal func canvasAnimationDidFinishAnimation() {
        play.setImage(UIImage(named: "drawplay_ic_play"), for: .normal)
        play.isSelected = false
    }

    internal func canvasAnimationFileParseError() {
        presentDialog(title: "canvas_data_parse_error_title".localized, message: "canvas_data_parse_error_content".localized) {
            action in
            super.onBackPressed(self)
        }
    }
}

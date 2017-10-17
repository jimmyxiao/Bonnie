//
//  WorkViewController.swift
//  BonnieDraw
//
//  Created by Professor on 16/10/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class WorkViewController: BackButtonViewController, URLSessionDelegate, CanvasViewDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var navigationBar: UINavigationBar!
    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var profileName: UILabel!
    @IBOutlet weak var canvas: CanvasView!
    @IBOutlet weak var play: UIButton!
    var work: Work?

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
        guard let fileUrl = work?.file,
              let url = canvas.url else {
            return
        }
        do {
            let data = try Data(contentsOf: fileUrl)
            if data.isEmpty {
                presentDialog(title: "canvas_data_parse_error_title".localized, message: "canvas_data_parse_error_content".localized) {
                    action in
                    self.dismiss(animated: true)
                }
            } else {
                try data.write(to: url)
                self.canvas.load()
                self.loading.hide(true)
            }
        } catch {
            Logger.d(error.localizedDescription)
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        canvas.stop()
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

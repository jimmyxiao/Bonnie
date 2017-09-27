//
//  CanvasViewController.swift
//  BonnieDraw
//
//  Created by Professor on 07/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CanvasViewController: BackButtonViewController {
    @IBOutlet weak var canvas: CanvasView!
    @IBOutlet weak var pen: UIButton!

    override func viewDidLoad() {
        pen.layer.cornerRadius = view.bounds.width / 10
    }

    override func viewWillAppear(_ animated: Bool) {
        UIApplication.shared.statusBarStyle = .lightContent
    }

    override func viewWillDisappear(_ animated: Bool) {
        UIApplication.shared.statusBarStyle = .default
    }

    @IBAction func reset(_ sender: AnyObject) {
        canvas.reset()
    }

    @IBAction func play(_ sender: AnyObject) {
        canvas.play()
    }
}

//
//  CanvasViewController.swift
//  BonnieDraw
//
//  Created by Professor on 07/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CanvasViewController: UIViewController {
    @IBOutlet weak var canvas: CanvasView!

    @IBAction func reset(_ sender: AnyObject) {
        canvas.reset()
    }

    @IBAction func play(_ sender: UIButton) {
        canvas.play()
    }
}

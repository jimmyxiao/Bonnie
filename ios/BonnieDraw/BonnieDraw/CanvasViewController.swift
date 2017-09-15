//
//  CanvasViewController.swift
//  BonnieDraw
//
//  Created by Professor on 07/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class CanvasViewController: UIViewController, CanvasViewDelegate {
    @IBOutlet weak var canvas: CanvasView!

    override func viewDidLoad() {
        canvas.delegate = self
    }

    @IBAction func reset(_ sender: AnyObject) {
        canvas.reset()
    }

    @IBAction func play(_ sender: UIButton) {
        canvas.play()
    }

    func canvasFileUrl() -> URL? {
        do {
            return try FileManager.default.url(for: .documentationDirectory, in: .userDomainMask, appropriateFor: nil, create: true).appendingPathComponent("temp.bdw")
        } catch let error {
            Logger.d(error.localizedDescription)
        }
        return nil
    }
}

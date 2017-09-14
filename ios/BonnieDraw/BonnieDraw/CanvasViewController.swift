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
    var lastPoint = CGPoint.zero
    var size: CGFloat = 10
    var alpha: CGFloat = 1
    var red: CGFloat = 0
    var green: CGFloat = 0
    var blue: CGFloat = 0
    var swiped = false
    var url: URL?
    var handle: FileHandle?
    var points = [Point]()
    var bytes = [UInt8]()

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

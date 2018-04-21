//
//  TabNavigationControllerAnimator.swift
//  BonnieDraw
//
//  Created by Professor on 08/11/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit

class TabNavigationControllerAnimator: NSObject, UIViewControllerAnimatedTransitioning {
    let duration = 0.3

    func transitionDuration(using transitionContext: UIViewControllerContextTransitioning?) -> TimeInterval {
        return duration
    }

    func animateTransition(using transitionContext: UIViewControllerContextTransitioning) {
        let containerView = transitionContext.containerView
        let from = transitionContext.viewController(forKey: UITransitionContextViewControllerKey.from)
        let to = transitionContext.viewController(forKey: UITransitionContextViewControllerKey.to)
        guard let view = to?.view else {
            return
        }
        containerView.addSubview(view)
        let center = view.center
        view.alpha = 0
        view.center = CGPoint(x: view.center.x, y: view.center.y + view.bounds.height / 3)
        UIView.animate(withDuration: duration,
                delay: 0,
                options: [.curveEaseOut],
                animations: {
                    view.alpha = 1
                    view.center = center
                    from?.view.alpha = 0
                }) {
            finished in
            transitionContext.completeTransition(!transitionContext.transitionWasCancelled)
        }
    }
}

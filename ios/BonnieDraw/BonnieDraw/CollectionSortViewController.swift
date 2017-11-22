//
//  CollectionSortViewController.swift
//  BonnieDraw
//
//  Created by Professor on 22/11/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import XLPagerTabStrip

class CollectionSortViewController: UIViewController, IndicatorInfoProvider {
    internal func indicatorInfo(for pagerTabStripController: PagerTabStripViewController) -> IndicatorInfo {
        return IndicatorInfo(title: "collection_tab_sort".localized)
    }
}

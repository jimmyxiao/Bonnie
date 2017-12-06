//
//  JotStrokeManager.h
//  JotUI
//
//  Created by Adam Wulf on 5/27/13.
//  Copyright (c) 2013 Milestone Made. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "JotStroke.h"


@interface JotStrokeManager : NSObject

+ (JotStrokeManager*)sharedInstance;

/**
 * return a stroke for the input touch only
 * if it already exists, otherwise nil
 */
- (JotStroke*)getStrokeForHash:(NSUInteger*)hash;

/**
 * return a new or existing stroke for the
 * input touch
 */
- (JotStroke*)makeStrokeForHash:(NSUInteger*)hash andTexture:(JotBrushTexture*)texture andBufferManager:(JotBufferManager*)bufferManager;


/**
 * replaces the stroke for one touch hash
 * with another stroke
 */
- (void)replaceStroke:(JotStroke*)oldStroke withStroke:(JotStroke*)newStroke;

/**
 * returns true if a stroke exists and has
 * been cancelled
 */
- (BOOL)cancelStrokeForHash:(NSUInteger*)hash;

/**
 * cancels a stroke
 */
- (BOOL)cancelStroke:(JotStroke*)stroke;


/**
 * remove a stroke from being tracked
 */
- (void)removeStrokeForHash:(NSUInteger*)hash;


/**
 * cancels all active strokes
 */
- (void)cancelAllStrokes;

@end
//
//  Event.h
//  Malmofestivalen
//
//  Created by Fredrik Glawe on 5/17/11.
//  Copyright 2011 mobiworks AB. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface Event : NSObject
{
@private
    NSNumber *_id;
    NSString *_title;
    NSString *_description;
    NSNumber *_sceneId;
    NSString *_businessId;
    NSString *_linkSpotify;
    NSString *_linkMySpace;
    NSString *_linkOriginal;
    NSString *_linkReadMore;
    NSDate *_startDate;
    NSDate *_endDate;
    NSString *_shortDescription;
}
@property(nonatomic, retain) NSNumber *id;
@property(nonatomic, retain) NSString *title;
@property(nonatomic, retain) NSString *description;
@property(nonatomic, retain) NSNumber *sceneId;
@property(nonatomic, retain) NSString *businessId;
@property(nonatomic, retain) NSString *linkSpotify;
@property(nonatomic, retain) NSString *linkMySpace;
@property(nonatomic, retain) NSString *linkOriginal;
@property(nonatomic, retain) NSString *linkReadMore;
@property(nonatomic, retain) NSDate *startDate;
@property(nonatomic, retain) NSDate *endDate;
@property(nonatomic, retain) NSString *shortDescription;
@end

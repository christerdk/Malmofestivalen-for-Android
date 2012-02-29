//
//  Event.m
//  Malmofestivalen
//
//  Created by Fredrik Glawe on 5/17/11.
//  Copyright 2011 mobiworks AB. All rights reserved.
//

#import "Event.h"


@implementation Event

@synthesize id = _id;
@synthesize title = _title;
@synthesize description = _description;
@synthesize sceneId = _sceneId;
@synthesize businessId = _businessId;
@synthesize linkSpotify = _linkSpotify;
@synthesize linkMySpace = _linkMySpace;
@synthesize linkOriginal = _linkOriginal;
@synthesize linkReadMore = _linkReadMore;
@synthesize startDate = _startDate;
@synthesize endDate = _endDate;
@synthesize shortDescription = _shortDescription;

-(void)dealloc
{
    [_title release];
    [_description release];
    [_sceneId release];
    [_businessId release];
    [_linkSpotify release];
    [_linkMySpace release];
    [_linkOriginal release];
    [_linkReadMore release];
    [_startDate release];
    [_endDate release];
    [_shortDescription release];
    [super dealloc];
}

@end

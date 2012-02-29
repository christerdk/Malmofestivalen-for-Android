//
//  Scene.m
//  Malmofestivalen
//
//  Created by Fredrik Glawe on 5/18/11.
//  Copyright 2011 mobiworks AB. All rights reserved.
//

#import "Scene.h"


@implementation Scene

@synthesize id = _id;
@synthesize title = _title;
@synthesize latitude = _latitude;
@synthesize longitude = _longitude;
@synthesize businessId = _businessId;
@synthesize description = _description;

-(void)dealloc
{
    [_id release];
    [_title release];
    [_latitude release];
    [_longitude release];
    [_businessId release];
    [_description release];
    [super dealloc];
}

-(CLLocationCoordinate2D)coordinate {
    CLLocationCoordinate2D coord;
    coord.latitude = [_latitude doubleValue] / 1000000;
    coord.longitude = [_longitude doubleValue] / 1000000;
    return coord;
}

@end

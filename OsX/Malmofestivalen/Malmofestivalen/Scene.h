//
//  Scene.h
//  Malmofestivalen
//
//  Created by Fredrik Glawe on 5/18/11.
//  Copyright 2011 mobiworks AB. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <MapKit/MapKit.h>

@interface Scene : NSObject <MKAnnotation>
{    
@private
    NSNumber *_id;
    NSString *_title;
    NSNumber *_latitude;
    NSNumber *_longitude;
    NSString *_businessId;
    NSString *_description;
}
@property(nonatomic, retain) NSNumber *id;
@property(nonatomic, retain) NSString *title;
@property(nonatomic, retain) NSNumber *latitude;
@property(nonatomic, retain) NSNumber *longitude;
@property(nonatomic, retain) NSString *businessId;
@property(nonatomic, retain) NSString *description;
@property(nonatomic, readonly) CLLocationCoordinate2D coordinate;
@end

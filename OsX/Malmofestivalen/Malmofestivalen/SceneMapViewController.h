//
//  SceneMapViewController.h
//  Malmofestivalen
//
//  Created by Magnus Pettersson on 6/2/11.
//  Copyright 2011 Consulence AB. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
@class Scene;

@interface SceneMapViewController : UIViewController <MKMapViewDelegate> {
    Scene *_scene;
    MKMapView *_mapView;
    BOOL _shouldZoomToUserLocation;
}

@property (nonatomic, retain) IBOutlet MKMapView *mapView;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil scene:(Scene *)scene;
- (IBAction)didFlipMyLocationSwitch:(id)sender;

@end

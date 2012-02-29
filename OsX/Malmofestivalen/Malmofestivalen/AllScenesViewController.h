//
//  AllScenesViewController.h
//  Malmofestivalen
//
//  Created by Magnus Pettersson on 5/19/11.
//  Copyright 2011 Consulence AB. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>

@interface AllScenesViewController : UIViewController <UITableViewDelegate, UITableViewDataSource, MKMapViewDelegate> {
    NSArray *_allScenes;
    UITableView *_tableView;
    MKMapView *_mapView;
    UIView *_mapBackgroundView;
    UISwitch *_myLocationSwitch;
    BOOL _shouldZoomToUserLocation;
    BOOL _shouldDisplayUserLocation;
}

@property (nonatomic, retain) IBOutlet UITableView *tableView;
@property (nonatomic, retain) IBOutlet UIView *mapBackgroundView;
@property (nonatomic, retain) IBOutlet MKMapView *mapView;

- (void)zoomToFitMapAnnotations:(MKMapView*)mapView;

- (IBAction)didFlipMyLocationSwitch:(id)sender;

@end

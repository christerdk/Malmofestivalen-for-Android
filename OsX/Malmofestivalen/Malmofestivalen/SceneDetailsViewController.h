//
//  SceneDetailsViewController.h
//  Malmofestivalen
//
//  Created by Magnus Pettersson on 5/22/11.
//  Copyright 2011 Consulence AB. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>

@class Scene;

@interface SceneDetailsViewController : UIViewController <UITableViewDelegate, UITableViewDataSource> {
    Scene *_scene;
    UITableView *_tableView;
    NSMutableDictionary *_events;
    NSMutableArray *_dates;
    NSDateFormatter *_cellDateFomratter;
}

@property (nonatomic, retain) IBOutlet UIView *tableBgView;
@property (nonatomic, retain) IBOutlet UITableView *tableView;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil scene:(Scene *)scene;
- (IBAction)showMapView:(id)sender;

@end

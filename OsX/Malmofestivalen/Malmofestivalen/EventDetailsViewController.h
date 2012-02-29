//
//  EventDetailsViewController.h
//  Malmofestivalen
//
//  Created by Magnus Pettersson on 6/2/11.
//  Copyright 2011 Consulence AB. All rights reserved.
//

#import <UIKit/UIKit.h>

@class Event, Scene;

@interface EventDetailsViewController : UIViewController {
    Event *_event;
    Scene *_scene;
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil event:(Event *)event scene:(Scene *)scene;

@end

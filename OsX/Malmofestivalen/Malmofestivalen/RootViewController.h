//
//  RootViewController.h
//  Malmofestivalen
//
//  Created by Magnus Pettersson on 5/15/11.
//  Copyright 2011 Consulence AB. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MainPageTableViewController.h"

@interface RootViewController : UIViewController <MainPageTableDelegate> {
    MainPageTableViewController *mMainPageTableViewController;
}

- (IBAction)didTouchAboutButton;

@end

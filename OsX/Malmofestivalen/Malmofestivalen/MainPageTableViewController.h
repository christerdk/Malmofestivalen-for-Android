//
//  MainPageTableViewController.h
//  Malmofestivalen
//
//  Created by Magnus Pettersson on 5/16/11.
//  Copyright 2011 Consulence AB. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol MainPageTableDelegate <NSObject>

- (void)didSelectItemAtIndex:(NSUInteger )index;
- (void)didTouchSearchButtonWithText:(NSString *)searchString;

@end

@interface MainPageTableViewController : UITableViewController <UITextFieldDelegate> {
    UITextField *mSearchField;
    id<MainPageTableDelegate> delegate;
}

@property (nonatomic, assign) id<MainPageTableDelegate> delegate;

@end

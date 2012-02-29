//
//  RootViewController.m
//  Malmofestivalen
//
//  Created by Magnus Pettersson on 5/15/11.
//  Copyright 2011 Consulence AB. All rights reserved.
//

#import "RootViewController.h"
#import "AllScenesViewController.h"

@implementation RootViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = @"Malmo Festival 2011";
    UIBarButtonItem *barButton = [[[UIBarButtonItem alloc] init] autorelease];
    barButton.title = @"Back";
    
    self.navigationItem.backBarButtonItem = barButton;
    
    //Main Page table 
    mMainPageTableViewController = [[MainPageTableViewController alloc] initWithNibName:@"MainPageTableViewController" bundle:nil];
    mMainPageTableViewController.delegate = self;
    [self.view addSubview:mMainPageTableViewController.view];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated
{
	[super viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
	[super viewDidDisappear:animated];
}

/*
 // Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
	// Return YES for supported orientations.
	return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
 */

// Handles selections in the main page table. Push new views here
- (void)didSelectItemAtIndex:(NSUInteger)index {
    NSLog(@"Index %i selected", index);
    
    UIViewController *vc = nil;
    switch (index) {
        case 0:
            //vc = create currentShowsViewController here
            break;
        case 1:
            vc = [[AllScenesViewController alloc] initWithNibName:@"AllScenesViewController" bundle:nil];
            break;
        case 2:
            //vc = create favouritesViewController here
            break;
        case 4:
            //vc = create ShareViewController here
            break;
        default:
            break;
    }
    
    if (vc != nil) {
        [self.navigationController pushViewController:vc animated:YES];
        [vc release];
    }
}

// Perform search with supplied search string
- (void)didTouchSearchButtonWithText:(NSString *)searchString {
    NSLog(@"Search pressed with string %@", searchString);
}

// Display about view
- (IBAction)didTouchAboutButton {
    NSLog(@"About pressed");
}

- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Relinquish ownership any cached data, images, etc that aren't in use.
}

- (void)viewDidUnload
{
    [super viewDidUnload];

    // Relinquish ownership of anything that can be recreated in viewDidLoad or on demand.
    // For example: self.myOutlet = nil;
}

- (void)dealloc
{
    [mMainPageTableViewController release], mMainPageTableViewController = nil;
    
    [super dealloc];
}

@end

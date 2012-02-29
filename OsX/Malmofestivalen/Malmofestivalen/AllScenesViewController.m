//
//  AllScenesViewController.m
//  Malmofestivalen
//
//  Created by Magnus Pettersson on 5/19/11.
//  Copyright 2011 Consulence AB. All rights reserved.
//

#import "AllScenesViewController.h"
#import "SceneDetailsViewController.h"
#import "DatabaseFactory.h"
#import "Scene.h"

@implementation AllScenesViewController

@synthesize tableView = _tableView, mapView = _mapView, mapBackgroundView = _mapBackgroundView;

/*
- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}
*/

- (void)dealloc
{
    [_allScenes release], _allScenes = nil;
    [_mapView release], _mapView = nil;
    [_tableView release], _tableView = nil;
    [_mapBackgroundView release], _mapBackgroundView = nil;
    [super dealloc];
}

- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = @"All Scenes";
    UIBarButtonItem *barButton = [[[UIBarButtonItem alloc] init] autorelease];
    barButton.title = @"Back";
    
    self.navigationItem.backBarButtonItem = barButton;
    
    // segmented control as the custom title view
	NSArray *segmentTextContent = [NSArray arrayWithObjects:
                                   @"List",
                                   @"Map",
                                   nil];
	UISegmentedControl* segmentedControl = [[UISegmentedControl alloc] initWithItems:segmentTextContent];
	segmentedControl.selectedSegmentIndex = 1;
	segmentedControl.autoresizingMask = UIViewAutoresizingFlexibleWidth;
	segmentedControl.segmentedControlStyle = UISegmentedControlStyleBar;
	segmentedControl.frame = CGRectMake(0, 0, 400, 30);
	[segmentedControl addTarget:self action:@selector(segmentAction:) forControlEvents:UIControlEventValueChanged];
	
	//defaultTintColor = [segmentedControl.tintColor retain];	// keep track of this for later
    
	self.navigationItem.titleView = segmentedControl;
	[segmentedControl release];

    _allScenes = [[[DatabaseFactory getInstance] getAllScenes] retain];
    [_mapView addAnnotations:_allScenes];
    
    //[annotaions release];
    [self zoomToFitMapAnnotations:_mapView];
    
    
    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
 
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

- (void)viewDidUnload
{
    self.tableView = nil;
    self.mapView = nil;
    self.mapBackgroundView = nil;
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    if (_shouldDisplayUserLocation) {
        //Start updating user loc again if it was enabled when leaving the view
        _mapView.showsUserLocation = TRUE;
        _shouldZoomToUserLocation = TRUE;
    }
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
    //Stop updating user loc when view dissappear
    _mapView.showsUserLocation = FALSE;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)segmentAction:(id)sender
{
	UISegmentedControl* segCtl = sender;
	// the segmented control was clicked, handle it here 
	
    NSInteger index = [segCtl selectedSegmentIndex];
    if(index == 0 && self.view != _tableView) {
        self.view = _tableView;
        _mapView.showsUserLocation = FALSE;
    } else if (index == 1 && self.view != _mapBackgroundView) {
        self.view = _mapBackgroundView;
        if (_shouldDisplayUserLocation) {
            _shouldZoomToUserLocation = TRUE;
            _mapView.showsUserLocation = TRUE;
        }
    }
}

- (IBAction)didFlipMyLocationSwitch:(id)sender {
    UISwitch *myLocSwitch = sender;
    
    if (myLocSwitch.on) {
        _mapView.showsUserLocation = TRUE;
        _shouldDisplayUserLocation = TRUE;
        _shouldZoomToUserLocation = TRUE;
    } else {
        _mapView.showsUserLocation = FALSE;
        _shouldDisplayUserLocation = FALSE;
        _shouldZoomToUserLocation = FALSE;
        [self zoomToFitMapAnnotations:_mapView];        
    }
}

//Called when the button in the callout view is touched
- (void)calloutViewButtonTouch:(id)sender {
    NSArray *annotations = [_mapView selectedAnnotations];
    if ([annotations count] > 0) {
        Scene *s = [annotations objectAtIndex:0];
        SceneDetailsViewController *vc = [[SceneDetailsViewController alloc] initWithNibName:@"SceneDetailsViewController" bundle:nil scene:s];
        [self.navigationController pushViewController:vc animated:YES];
        [vc release];
    }
}

-(void)zoomToFitMapAnnotations:(MKMapView*)mapView
{
    if([mapView.annotations count] == 0)
        return;
    
    CLLocationCoordinate2D topLeftCoord;
    topLeftCoord.latitude = -90;
    topLeftCoord.longitude = 180;
    
    CLLocationCoordinate2D bottomRightCoord;
    bottomRightCoord.latitude = 90;
    bottomRightCoord.longitude = -180;
    
    for(Scene* annotation in mapView.annotations)
    {
        topLeftCoord.longitude = fmin(topLeftCoord.longitude, annotation.coordinate.longitude);
        topLeftCoord.latitude = fmax(topLeftCoord.latitude, annotation.coordinate.latitude);
        
        bottomRightCoord.longitude = fmax(bottomRightCoord.longitude, annotation.coordinate.longitude);
        bottomRightCoord.latitude = fmin(bottomRightCoord.latitude, annotation.coordinate.latitude);
    }
    
    MKCoordinateRegion region;
    region.center.latitude = topLeftCoord.latitude - (topLeftCoord.latitude - bottomRightCoord.latitude) * 0.5;
    region.center.longitude = topLeftCoord.longitude + (bottomRightCoord.longitude - topLeftCoord.longitude) * 0.5;
    region.span.latitudeDelta = fabs(topLeftCoord.latitude - bottomRightCoord.latitude) * 1.5; // Add a little extra space on the sides
    region.span.longitudeDelta = fabs(bottomRightCoord.longitude - topLeftCoord.longitude) * 1.5; // Add a little extra space on the sides
    
    region = [mapView regionThatFits:region];
    [mapView setRegion:region animated:YES];
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return [_allScenes count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    
    Scene *scene = [_allScenes objectAtIndex:indexPath.row];
    cell.textLabel.text = scene.title;
    
    return cell;
}

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
    }   
    else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    SceneDetailsViewController *vc = [[SceneDetailsViewController alloc] initWithNibName:@"SceneDetailsViewController" bundle:nil scene:[_allScenes objectAtIndex:indexPath.row]];
    [self.navigationController pushViewController:vc animated:YES];
    [vc release];
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    // Navigation logic may go here. Create and push another view controller.
    /*
     <#DetailViewController#> *detailViewController = [[<#DetailViewController#> alloc] initWithNibName:@"<#Nib name#>" bundle:nil];
     // ...
     // Pass the selected object to the new view controller.
     [self.navigationController pushViewController:detailViewController animated:YES];
     [detailViewController release];
     */
}

#pragma mark - Map view delegate

- (MKAnnotationView *)mapView:(MKMapView *)mapView viewForAnnotation:(id <MKAnnotation>)annotation {    
    MKAnnotationView *annotationView;
    
    if (annotation == mapView.userLocation ) {
        return nil; // Standard view
    } 
    
    else {
        annotationView = [mapView dequeueReusableAnnotationViewWithIdentifier:@"SceneAnnotationView"];
        if (annotationView == nil) {
            annotationView = [[[MKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:@"SceneAnnotationView"] autorelease];
            annotationView.canShowCallout = TRUE;
            UIButton *calloutButton = [UIButton buttonWithType:UIButtonTypeDetailDisclosure];
            [calloutButton addTarget:self action:@selector(calloutViewButtonTouch:) forControlEvents:UIControlEventTouchUpInside];
            annotationView.rightCalloutAccessoryView = calloutButton;
            ((MKPinAnnotationView *)annotationView).animatesDrop = TRUE;
        }
    }
    
    return annotationView;
}
/*
- (void)mapViewWillStartLocatingUser:(MKMapView *)mapView {
    NSLog(@"Start user location");
}

- (void)mapViewDidStopLocatingUser:(MKMapView *)mapView {
    NSLog(@"User location stopped");
}
*/
- (void)mapView:(MKMapView *)mapView didUpdateUserLocation:(MKUserLocation *)userLocation {
    if (_shouldZoomToUserLocation) {
        [_mapView setCenterCoordinate:_mapView.userLocation.location.coordinate animated:TRUE];
        _shouldZoomToUserLocation = FALSE;
    }
}

@end

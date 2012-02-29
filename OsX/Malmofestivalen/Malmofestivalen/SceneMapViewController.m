//
//  SceneMapViewController.m
//  Malmofestivalen
//
//  Created by Magnus Pettersson on 6/2/11.
//  Copyright 2011 Consulence AB. All rights reserved.
//

#import "SceneMapViewController.h"
#import "Scene.h"

@interface SceneMapViewController(Private)
- (void)zoomToScene;
@end

@implementation SceneMapViewController

@synthesize mapView = _mapView;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil scene:(Scene *)scene
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        _scene = [scene retain];
    }
    return self;
}

- (void)dealloc
{
    [_scene release], _scene = nil;
    [_mapView release], _mapView = nil;
    [super dealloc];
}

- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

/*
// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView
{
}
*/


// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = _scene.title;
    
    [_mapView addAnnotation:_scene];
    //[self zoomToFitMapAnnotations:mapView];
    
    [self zoomToScene];
}


- (void)viewDidUnload
{
    self.mapView = nil;
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (IBAction)didFlipMyLocationSwitch:(id)sender {
    UISwitch *sw = sender;
    if (sw.on) {
        _mapView.showsUserLocation = TRUE;
        _shouldZoomToUserLocation = TRUE;
    } else {
        _mapView.showsUserLocation = FALSE;
        _shouldZoomToUserLocation = FALSE;
        [self zoomToScene];
    }
}

- (void)zoomToScene {
    MKCoordinateRegion region;
    region.center.latitude = _scene.coordinate.latitude;
    region.center.longitude = _scene.coordinate.longitude;
    region.span.latitudeDelta = 0.004; // Add a little extra space on the sides
    region.span.longitudeDelta = 0.004; // Add a little extra space on the sides
    
    region = [_mapView regionThatFits:region];
    [_mapView setRegion:region animated:YES];
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

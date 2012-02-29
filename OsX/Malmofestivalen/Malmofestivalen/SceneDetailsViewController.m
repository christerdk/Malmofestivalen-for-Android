//
//  SceneDetailsViewController.m
//  Malmofestivalen
//
//  Created by Magnus Pettersson on 5/22/11.
//  Copyright 2011 Consulence AB. All rights reserved.
//

#import "SceneDetailsViewController.h"
#import "SceneMapViewController.h"
#import "EventDetailsViewController.h"
#import "DatabaseFactory.h"
#import "Scene.h"
#import "Event.h"

@interface SceneDetailsViewController(Private)

- (void)sortEventsByDate:(NSArray *)events;

@end

@implementation SceneDetailsViewController

@synthesize tableView = _tableView, tableBgView = _tableBgView;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil scene:(Scene *)scene;
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
    [_events release], _events = nil;
    [_dates release], _dates = nil;
    [_cellDateFomratter release], _cellDateFomratter = nil;
    [_tableView release], _tableView = nil;
    [_tableBgView release], _tableBgView = nil;
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
    UIBarButtonItem *barButton = [[[UIBarButtonItem alloc] init] autorelease];
    barButton.title = @"Back";
    self.navigationItem.backBarButtonItem = barButton;
    
    _cellDateFomratter = [[NSDateFormatter alloc] init];
    [_cellDateFomratter setDateStyle:NSDateFormatterNoStyle];
    [_cellDateFomratter setTimeStyle:NSDateFormatterShortStyle];
    
    NSArray *e = [[[DatabaseFactory getInstance] getEventsWithSceneId:_scene.id] retain];
    [self sortEventsByDate:e];
    
    /*UITableViewCell *footer = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
    footer.textLabel.text = @"Show on map";
    footer.accessoryType = UITableViewCellAccessoryDetailDisclosureButton;*/
    /*UIView *footer = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 44)];
    footer.backgroundColor = [UIColor blueColor];
    self.tableView.tableFooterView = footer;
    [footer release];*/
}


- (void)viewDidUnload
{
    self.tableView = nil;
    self.tableBgView = nil;
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)sortEventsByDate:(NSArray *)events {
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    //[dateFormatter setDateFormat:@"yyyy MM dd"];
    [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
    [dateFormatter setTimeStyle:NSDateFormatterNoStyle];
    _events = [[NSMutableDictionary alloc] init];
    for (Event *event in events) {
        if (event.startDate != nil) {
            NSString *dateString = [dateFormatter stringFromDate:event.startDate];
            NSMutableArray *a = [_events objectForKey:dateString];
            if (a == nil) {
                a = [NSMutableArray arrayWithObject:event];
                [_events setObject:a forKey:dateString]; // The keys do not include time since it's the section titles
            } else {
                [a addObject:event];
            }
        }
    }
    [dateFormatter release];
    _dates = [[[_events allKeys] sortedArrayUsingSelector:@selector(compare:)] retain];
}

- (IBAction)showMapView:(id)sender {
    SceneMapViewController *vc = [[SceneMapViewController alloc] initWithNibName:@"SceneMapViewController" bundle:nil scene:_scene];
    [self.navigationController pushViewController:vc animated:YES];
    [vc release];
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return [_dates count];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return [[_events objectForKey:[_dates objectAtIndex:section]] count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    
    NSArray *eventArray = [_events objectForKey:[_dates objectAtIndex:indexPath.section]];
    Event *event = [eventArray objectAtIndex:indexPath.row];
    cell.textLabel.text = [NSString stringWithFormat:@"%@ - %@",[_cellDateFomratter stringFromDate:event.startDate], event.title];
    
    return cell;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    return [_dates objectAtIndex:section];
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
   
    // Navigation logic may go here. Create and push another view controller.
    /*
     <#DetailViewController#> *detailViewController = [[<#DetailViewController#> alloc] initWithNibName:@"<#Nib name#>" bundle:nil];
     // ...
     // Pass the selected object to the new view controller.
     [self.navigationController pushViewController:detailViewController animated:YES];
     [detailViewController release];
     */
    NSArray *eventArray = [_events objectForKey:[_dates objectAtIndex:indexPath.section]];
    Event *event = [eventArray objectAtIndex:indexPath.row];
    
    EventDetailsViewController *vc = [[EventDetailsViewController alloc] initWithNibName:@"EventDetailsViewController" bundle:nil event:event scene:_scene];
    [self.navigationController pushViewController:vc animated:YES];
    [vc release];
}


@end

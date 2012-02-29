//
//  MainPageTableViewController.m
//  Malmofestivalen
//
//  Created by Magnus Pettersson on 5/16/11.
//  Copyright 2011 Consulence AB. All rights reserved.
//

#import "MainPageTableViewController.h"


@implementation MainPageTableViewController

@synthesize delegate;

/*
- (id)initWithStyle:(UITableViewStyle)style {
    // Override initWithStyle: if you create the controller programmatically and want to perform customization that is not appropriate for viewDidLoad.
    if (self = [super initWithStyle:style]) {
    }
    return self;
}
*/


- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.view.frame = CGRectMake(10, self.view.frame.origin.y, self.view.frame.size.width, 44*5 - 1); //height of table equal to number of rows * row height - 1 to remove last separator
    self.tableView.separatorColor = [UIColor colorWithRed:124.f/255.f green:205.f/255.f blue:255.f/255.f alpha:1];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.tableView.scrollEnabled = FALSE;
    
    mSearchField = [[UITextField alloc] initWithFrame:CGRectMake(60, 8, 180, 28)];
    mSearchField.borderStyle = UITextBorderStyleRoundedRect;
    mSearchField.delegate = self;
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}


/*
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}
*/
/*
- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
}
*/
/*
- (void)viewWillDisappear:(BOOL)animated {
	[super viewWillDisappear:animated];
}
*/
/*
- (void)viewDidDisappear:(BOOL)animated {
	[super viewDidDisappear:animated];
}
*/

/*
// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/

- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
	
	// Release any cached data, images, etc that aren't in use.
}

- (void)viewDidUnload {
	// Release any retained subviews of the main view.
	// e.g. self.myOutlet = nil;
}


#pragma mark Table view methods

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}


// Customize the number of rows in the table view.
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 5;
}


// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"Cell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
    }
    
    NSString *cellText = nil;
    NSString *cellImage = nil;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    // Configure the cell
    switch (indexPath.row) {
        case 0:
            cellText = @"Current and upcoming events";
            cellImage = @"clock.png";
            break;
        case 1:
            cellText = @"Festival map";
            cellImage = @"globe.png";
            break;
        case 2:
            cellText = @"Your favourites";
            cellImage = @"musicnode.png";
            break;
        case 3:
            cellImage = @"search.png";
            [cell.contentView addSubview:mSearchField];
            cell.accessoryType = UITableViewCellAccessoryDetailDisclosureButton;
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            break;
        case 4:
            cellText = @"Share";
            cellImage = @"happyface.png";
            break;
        default:
            break;
    }
    cell.textLabel.text = cellText;
    cell.imageView.image = [UIImage imageNamed:cellImage];
	
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [delegate didSelectItemAtIndex:indexPath.row];
    [tableView deselectRowAtIndexPath:indexPath animated:TRUE];
    // Navigation logic may go here. Create and push another view controller.
	// AnotherViewController *anotherViewController = [[AnotherViewController alloc] initWithNibName:@"AnotherView" bundle:nil];
	// [self.navigationController pushViewController:anotherViewController];
	// [anotherViewController release];
}

- (void)tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 3) {
        [delegate didTouchSearchButtonWithText:mSearchField.text];
    }
}

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/


/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:YES];
    }   
    else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/


/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath {
}
*/


/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

/*
- (void)textFieldDidBeginEditing:(UITextField *)textField {
    [UIView animateWithDuration:0.3 
                     animations:^{
        self.view.transform = CGAffineTransformMakeTranslation(0, -20);
    }];
}
*/

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    /*
    [UIView animateWithDuration:0.3 
                     animations:^{
                         self.view.transform = CGAffineTransformMakeTranslation(0, 0);
                     }];
    */
    return TRUE;
}


                                                         
- (void)dealloc {
    [mSearchField release], mSearchField = nil;
    
    [super dealloc];
}


@end


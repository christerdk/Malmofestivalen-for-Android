//
//  MalmofestivalenTests.m
//  MalmofestivalenTests
//
//  Created by Magnus Pettersson on 5/15/11.
//  Copyright 2011 Consulence AB. All rights reserved.
//

#import "MalmofestivalenTests.h"


@implementation MalmofestivalenTests

- (void)setUp
{
    [super setUp];
    
    // Set-up code here.
}

- (void)tearDown
{
    // Tear-down code here.
    
    [super tearDown];
}

- (void)testExample
{
    //STFail(@"Unit tests are not implemented yet in MalmofestivalenTests");
}

-(void)testDatabaseFactory
{
    DatabaseFactory *dbFactory = [DatabaseFactory getInstance];
    STAssertNotNil(dbFactory, @"Databsefactroy getInstance was nil");
    NSArray *events = [dbFactory getAllEvents];
    STAssertNotNil(events, @"getAllEvents was nil");
    NSArray *scenes = [dbFactory getAllScenes];
    STAssertNotNil(scenes, @"getAllScenes was nil");
    [events release];
    [scenes release];
    
    Scene *scene = [dbFactory getSceneWithId:[NSNumber numberWithInt:1]];
    STAssertNotNil(scene, @"getSceneWithId was nil");
    [scene release];
    
    Event *event = [dbFactory getEventWithId:[NSNumber numberWithInt:1]];
    STAssertNotNil(event, @"getEventWithId was nil");
    
    NSArray *sceneEvents = [dbFactory getEventsWithSceneId:[NSNumber numberWithInt:1]];
    STAssertNotNil(sceneEvents, @"sceneEvents was nil");
}

@end

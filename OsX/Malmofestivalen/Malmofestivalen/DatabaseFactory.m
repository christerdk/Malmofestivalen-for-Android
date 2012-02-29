//
//  DatabaseFactory.m
//  Malmofestivalen
//
//  Created by Fredrik Glawe on 5/17/11.
//  Copyright 2011 mobiworks AB. All rights reserved.
//

#import "DatabaseFactory.h"

static DatabaseFactory *_instance;

@implementation DatabaseFactory

#pragma mark Setup, init and destruction

+(DatabaseFactory*)getInstance
{
    if(_instance == nil)
    {
        NSString *databaseFilePath = [[[NSBundle mainBundle] resourcePath] stringByAppendingString:@"/concerts.sqlite"];
        _instance = [[DatabaseFactory alloc] initWithContentsOfFile:databaseFilePath];
    }
    return _instance;
}

-(id)initWithContentsOfFile:(NSString*)filePath
{
    self = [super init];
    
    _dateFormatter = [[NSDateFormatter alloc] init];
    [_dateFormatter setFormatterBehavior:NSDateFormatterBehaviorDefault];
    [_dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    
    if(sqlite3_open([filePath UTF8String], &_database) == SQLITE_OK)
    {
        NSLog(@"Successfully opened %@", filePath);
    }
    else
    {
        NSString *err = [NSString stringWithCString:sqlite3_errmsg(_database) encoding:NSUTF8StringEncoding];
        [NSException raise:@"Error while opening database file." format:@"Error while opening database file %@", err];
    }
    return self;
}

-(void)dealloc
{
    sqlite3_close(_database);
    [_dateFormatter release];
    [_instance release];
    [super dealloc];
}

#pragma mark Scene

-(Scene*)getSceneWithId:(NSNumber*)sceneId
{
    Scene *scene = [[Scene alloc] init];
    const char *sqlStatement = [[NSString stringWithFormat:@"select * from scenes where _id = %i", [sceneId intValue]] UTF8String];
    sqlite3_stmt *compiledStatement;
    if(sqlite3_prepare_v2(_database, sqlStatement, -1, &compiledStatement, NULL) == SQLITE_OK) 
    {
        sqlite3_step(compiledStatement);
        scene = [self getSceneFromCompiledStatement:&compiledStatement];
    }
    return scene;
}

-(NSArray*)getAllScenes
{
    NSMutableArray* allScenes = [[NSMutableArray alloc] init];
    const char *sqlStatement = "select * from scenes";
    sqlite3_stmt *compiledStatement;
    if(sqlite3_prepare_v2(_database, sqlStatement, -1, &compiledStatement, NULL) == SQLITE_OK) 
    {
        while(sqlite3_step(compiledStatement) == SQLITE_ROW) 
        {
            Scene *scene = [self getSceneFromCompiledStatement:&compiledStatement];
            [allScenes addObject:scene];
        }
    }
    return allScenes;
}

-(Scene*)getSceneFromCompiledStatement:(sqlite3_stmt**)compiledStatement
{
    Scene *scene = [[Scene alloc] init];
    
    scene.id = [NSNumber numberWithInt:(int)sqlite3_column_int(*compiledStatement, 0)];
    
    char *title = (char *)sqlite3_column_text(*compiledStatement, 1);
    if(title == NULL)
        scene.title = nil;
    else
        scene.title = [NSString stringWithUTF8String:title];
    
    scene.latitude = [NSNumber numberWithInt:(int)sqlite3_column_int(*compiledStatement, 2)];
    scene.longitude = [NSNumber numberWithInt:(int)sqlite3_column_int(*compiledStatement, 3)];
    
    char *businessId = (char *)sqlite3_column_text(*compiledStatement, 4);
    if(businessId == NULL)
        scene.businessId = nil;
    else
        scene.businessId = [NSString stringWithUTF8String:businessId];
    
    char *description = (char *)sqlite3_column_text(*compiledStatement, 5);
    if(description == NULL)
        scene.description = nil;
    else
        scene.description = [NSString stringWithUTF8String:description];
    
    return scene;
}

#pragma mark Event

-(NSArray*)getEventsWithSceneId:(NSNumber*)sceneId
{
    NSMutableArray *sceneEvents = [[NSMutableArray alloc] init];
    const char *sqlStatement = [[NSString stringWithFormat:@"select * from events where SceneID = %i", [sceneId intValue]] UTF8String] ;
    sqlite3_stmt *compiledStatement;
    if(sqlite3_prepare_v2(_database, sqlStatement, -1, &compiledStatement, NULL) == SQLITE_OK) 
    {
        while(sqlite3_step(compiledStatement) == SQLITE_ROW) 
        {
            Event *event = [self getEventFromCompiledStatement:&compiledStatement];
            [sceneEvents addObject:event];
        }
    }
    return sceneEvents;
}

-(Event*)getEventWithId:(NSNumber*)eventId
{
    Event *event = [[Event alloc] init];
    const char *sqlStatement = [[NSString stringWithFormat:@"select * from events where _id = %i", [eventId intValue]] UTF8String];
    sqlite3_stmt *compiledStatement;
    if(sqlite3_prepare_v2(_database, sqlStatement, -1, &compiledStatement, NULL) == SQLITE_OK) 
    {
        sqlite3_step(compiledStatement);
        event = [self getEventFromCompiledStatement:&compiledStatement];
    }
    return event;
}

-(NSArray*)getAllEvents
{
    NSMutableArray *allEvents = [[NSMutableArray alloc] init];
    const char *sqlStatement = "select * from events";
    sqlite3_stmt *compiledStatement;
    if(sqlite3_prepare_v2(_database, sqlStatement, -1, &compiledStatement, NULL) == SQLITE_OK) 
    {
        while(sqlite3_step(compiledStatement) == SQLITE_ROW) 
        {
            
            Event *event = [self getEventFromCompiledStatement:&compiledStatement];
            [allEvents addObject:event];
        }
    }
    return allEvents;
}

-(Event*)getEventFromCompiledStatement:(sqlite3_stmt**)compiledStatement
{
    Event *event = [[Event alloc] init];
    
    event.id = [NSNumber numberWithInt:(int)sqlite3_column_int(*compiledStatement, 0)];
    
    char *title = (char *)sqlite3_column_text(*compiledStatement, 1);
    if(title == NULL)
        event.title = nil;
    else
        event.title = [NSString stringWithUTF8String:title];
    
    char *description = (char *)sqlite3_column_text(*compiledStatement, 2);
    if(description == NULL)
        event.title = nil;
    else
        event.description = [NSString stringWithUTF8String:description];
    
    event.sceneId = [NSNumber numberWithInt:(int)sqlite3_column_int(*compiledStatement, 3)];
    
    char *businessId = (char *)sqlite3_column_text(*compiledStatement, 4);
    if(businessId == NULL)
        event.businessId = nil;
    else
        event.businessId = [NSString stringWithUTF8String:businessId]; 
    
    char *linkSpotify = (char *)sqlite3_column_text(*compiledStatement, 5);
    if(linkSpotify == NULL)
        event.linkSpotify = nil;
    else
        event.linkSpotify = [NSString stringWithUTF8String:linkSpotify]; 
    
    char *linkMySpace = (char *)sqlite3_column_text(*compiledStatement, 6);
    if(linkMySpace == NULL)
        event.linkMySpace = nil;
    else
        event.linkMySpace = [NSString stringWithUTF8String:linkMySpace]; 
    
    char *linkOriginal = (char *)sqlite3_column_text(*compiledStatement, 7);
    if(linkOriginal == NULL)
        event.linkOriginal = nil;
    else
        event.linkOriginal = [NSString stringWithUTF8String:linkOriginal]; 
    
    char *linkReadMore = (char *)sqlite3_column_text(*compiledStatement, 8);
    if(linkReadMore == NULL)
        event.linkReadMore = nil;
    else
        event.linkReadMore = [NSString stringWithUTF8String:linkReadMore]; 
    
    char *startDate = (char *)sqlite3_column_text(*compiledStatement, 9);
    if(startDate == NULL)
        event.startDate = nil;
    else
        event.startDate = [_dateFormatter dateFromString:[NSString stringWithUTF8String:startDate]];
    
    char *endDate = (char *)sqlite3_column_text(*compiledStatement, 10);
    if(endDate == NULL)
        event.endDate = nil;
    else
        event.endDate = [_dateFormatter dateFromString:[NSString stringWithUTF8String:endDate]];
    
    char *shortDescription = (char *)sqlite3_column_text(*compiledStatement, 11);
    if(shortDescription == NULL)
        event.shortDescription = nil;
    else
        event.shortDescription = [NSString stringWithUTF8String:shortDescription]; 
    
    return event;
}

@end

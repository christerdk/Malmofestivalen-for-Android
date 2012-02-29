//
//  DatabaseFactory.h
//  Malmofestivalen
//
//  Created by Fredrik Glawe on 5/17/11.
//  Copyright 2011 mobiworks AB. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <sqlite3.h>
#import "Event.h"
#import "Scene.h"

@interface DatabaseFactory : NSObject 
{
@private
    NSDateFormatter *_dateFormatter;
    sqlite3 *_database;
}
+(DatabaseFactory*)getInstance;
-(Event*)getEventFromCompiledStatement:(sqlite3_stmt**)compiledStatement;
-(Event*)getEventWithId:(NSNumber*)eventId;
-(NSArray*)getAllEvents;
-(NSArray*)getEventsWithSceneId:(NSNumber*)sceneId;
-(Scene*)getSceneFromCompiledStatement:(sqlite3_stmt**)compiledStatement;
-(Scene*)getSceneWithId:(NSNumber*)sceneId;
-(NSArray*)getAllScenes;
@end

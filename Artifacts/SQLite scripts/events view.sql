DROP view IF EXISTS events;
create view if not exists events
as
select schedules._id as _id, acts.Title as Title, acts.ActId as ActId, acts.ShortDescription as Description, acts.SceneId as SceneId, acts.LinkOriginal as LinkOriginal, 

schedules.FavoriteId as BusinessId, schedules.StartUTC as StartDate, schedules.EndUTC as EndDate, acts.ShortDescription as ShortDescription, 

scenes.Title as SceneTitle from schedules
left join acts on acts.ActId = schedules.ActId
left join scenes on acts.SceneId = scenes.SceneId;

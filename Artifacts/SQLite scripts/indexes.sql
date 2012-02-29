create index if not exists actindex on acts(ActId);
create index if not exists scheduleactindex on schedules(ActId);
create index if not exists sceneidindex on Scenes(SceneID);
create index if not exists actssceneidindex on acts(SceneId);
create index if not exists categoryindex on categories(CategoryID);
create index if not exists actstocategoriescategoryid on actstocategories(CategoryID);
create index if not exists actstocategoriesactid on actstocategories(CategoryID);
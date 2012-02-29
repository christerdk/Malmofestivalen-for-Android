namespace  MalmoFestival.IoC
{
    using Ninject.Modules;
    using Microsoft.Devices;
    using MalmoFestival.Services.Impl;
    using MalmoFestival.Services;
    using MalmoFestival.ViewModels;
    using MalmoFestival.Services.Fakes;
    using MalmoFestival.Repositories;
    using MalmoFestival.Repositories.Fake;
    using MalmoFestival.Repositories.Impl;

    public class Module : NinjectModule
    {
        public override void Load()
        {
            switch (Environment.DeviceType)
            {
                case DeviceType.Device:
                    LoadForDevice();
                    break;
                default:
                    LoadForEmulator();
                    break;
            }
        }

        private void LoadCommon()
        {
            Bind<IFavoritesService>().To<FavoritesService>().InSingletonScope();
            Bind<IEventsService>().To<EventsService>().InSingletonScope();
            Bind<IScenesService>().To<ScenesService>().InSingletonScope();
            Bind<IMapService>().To<MapService>().InSingletonScope();
            Bind<IDownLoadService>().To<DownLoadService>().InSingletonScope();
            Bind<INavigationService>().To<NavigationService>().InSingletonScope();                                    

            // View models
            Bind<IEventsViewModel>().To<EventsViewModel>().InSingletonScope();
            Bind<IFavoritesViewModel>().To<FavoritesViewModel>().InSingletonScope();
            Bind<ISceneListViewModel>().To<SceneListViewModel>().InSingletonScope();
            Bind<ISceneMapViewModel>().To<SceneMapViewModel>().InSingletonScope();
            Bind<IUpcomingEventsViewModel>().To<UpcomingEventsViewModel>().InSingletonScope();                                    

            // Repository
            Bind<IEventRepository>().To<EventRepository>().InSingletonScope();
            Bind<IMetadataRepository>().To<MetadataRepository>().InSingletonScope();


        }
        private void LoadForDevice()
        {
            LoadCommon();
            Bind<IGeoLocationService>().To<GeoLocationService>().InSingletonScope();

        }

        private void LoadForEmulator()
        {
            LoadCommon();
            Bind<IGeoLocationService>().To<GeoLocationServiceFake>().InSingletonScope();
        }
    }
}

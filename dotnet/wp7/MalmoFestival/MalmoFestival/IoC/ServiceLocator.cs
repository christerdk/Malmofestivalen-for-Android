namespace MalmoFestival.IoC
{
    using Ninject;
    using Ninject.Planning.Bindings;
    using MalmoFestival.IoC;
    using MalmoFestival.ViewModels;
    using MalmoFestival.Services.Impl;

    public class ServiceLocator
    {
        private static readonly IKernel _kernel;
        static Module module = new Module();
        static ServiceLocator()
        {
            _kernel = new StandardKernel(module);
        }

        public MainViewModel MainViewModel
        {
            get
            {
                return _kernel.Get<MainViewModel>();
            }
        }

        public EventsViewModel EventsViewModel
        {
            get
            {
                return _kernel.Get<EventsViewModel>();
            }
        }

        public EventDetailsViewModel EventsDetailsModel
        {
            get
            {
                return _kernel.Get<EventDetailsViewModel>();
            }
        }
        
        public InitDatabaseService InitDatabaseService
        {
            get
            {
                return _kernel.Get<InitDatabaseService>();
            }
        }
    }
}

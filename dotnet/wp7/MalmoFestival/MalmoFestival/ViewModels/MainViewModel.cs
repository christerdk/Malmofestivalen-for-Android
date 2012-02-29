namespace MalmoFestival.ViewModels
{
    using System.Collections.ObjectModel;
    using System.ComponentModel;
    using GalaSoft.MvvmLight;
    using MalmoFestival.Content.Resources;

    public class MainViewModel : ViewModelBase
    {
        private readonly IEventsViewModel _eventViewModel;

        public MainViewModel(IEventsViewModel eventsViewModel, IFavoritesViewModel favoritesViewModel, ISceneListViewModel sceneListViewModel, ISceneMapViewModel sceneMapViewModel, IUpcomingEventsViewModel upcomingEventsViewModel)
        {
            _eventViewModel = eventsViewModel;

            PageCollection = new ObservableCollection<IPageViewModel> {
                favoritesViewModel,
                eventsViewModel,
                sceneListViewModel,
                sceneMapViewModel,
                upcomingEventsViewModel
            };
        }

        public IEventsViewModel EventViewModel { get { return _eventViewModel; } }

        private ObservableCollection<IPageViewModel> _pageCollection;

        public ObservableCollection<IPageViewModel> PageCollection
        {
            get { return _pageCollection; }
            set
            {
                if (_pageCollection != value)
                {
                    _pageCollection = value;
                    RaisePropertyChanged("PageCollection");
                }
            }
        }

        public void InitializeViewModels()
        {
            foreach (var model in PageCollection)
            {
                model.Initialize();
            }
        }

    }
}
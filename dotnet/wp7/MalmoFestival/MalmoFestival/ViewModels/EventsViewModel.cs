namespace MalmoFestival.ViewModels
{
    using System.Collections.ObjectModel;
    using System.Collections.Generic;
    using System.Linq;
    using GalaSoft.MvvmLight;
    using MalmoFestival.Services;
    using MalmoFestival.Domain;
    using MalmoFestival.Helpers;
    using MalmoFestival.Content.Resources;
    using System.Windows.Input;
    using GalaSoft.MvvmLight.Command;
    using GalaSoft.MvvmLight.Messaging;
    using System;

    public class EventsViewModel : PageViewModel, IEventsViewModel
    {
        private ObservableCollection<EventItem> _eventsCollection;
        private readonly IEventsService _eventService;
        private readonly INavigationService _navigationService;

        public EventsViewModel(IEventsService eventService, INavigationService navigationService)
        {
            TitleText = Labels.Events;

            _eventService = eventService;
            _navigationService = navigationService;

            WireEvents();
            WireCommands();

        }

        public override void Initialize()
        {
            _eventService.GetAll();
        }

        public ObservableCollection<EventItem> EventsCollection
        {
            get { return _eventsCollection; }
            set
            {
                if (_eventsCollection != value)
                {
                    _eventsCollection = value;
                    RaisePropertyChanged("EventsCollection");
                }
            }
        }

        #region Commands

        public ICommand ShowEventDetailCommand { get; private set; }

        private void WireCommands()
        {
            ShowEventDetailCommand = new RelayCommand<int>(ShowEventDetail);
        }

        public void ShowEventDetail(int id)
        {
            var uri = new Uri(string.Format(Constants.EventDetailsUri, id), UriKind.Relative);
            _navigationService.NavigateTo(uri);
        }
        #endregion

        private void WireEvents()
        {
            _eventService.GetEventItemsTaskCompleted += OnGetEventItemsTaskCompleted;
        }

        private void OnGetEventItemsTaskCompleted(object sender, EventArgs<IList<EventItem>> e)
        {
            EventsCollection = new ObservableCollection<EventItem>();
            foreach (var item in e.Value)
            {
                EventsCollection.Add(item);
            }                
        }

    }
}

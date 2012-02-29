using System;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using MalmoFestival.Services;
using MalmoFestival.Domain;
using GalaSoft.MvvmLight;

namespace MalmoFestival.ViewModels
{
    public class EventDetailsViewModel : ViewModelBase
    {
        private readonly IEventsService _eventService;
        private readonly INavigationService _navigationService;
        private EventItem _eventItem;

        public EventDetailsViewModel(IEventsService eventService, INavigationService navigationService)
        {
            _eventService = eventService;
            _navigationService = navigationService;
            WireEvents();
        }

        public EventItem EventItem {
            get
            {
                return _eventItem;
            }
            set
            {
                if (_eventItem != value)
                {
                    _eventItem = value;
                    RaisePropertyChanged("EventItem");
                }
            }
        }
        public  void UpdateEventItem(int id)
        {
            _eventService.GetBy(id);
        }

        private void WireEvents()
        {
            _eventService.GetEventItemTaskCompleted += (s,e) =>
                {
                  EventItem = e.Value;
                };
        }
    }
}

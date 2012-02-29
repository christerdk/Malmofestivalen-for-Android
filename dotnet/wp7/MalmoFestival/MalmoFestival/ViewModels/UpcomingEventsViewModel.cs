namespace MalmoFestival.ViewModels
{
    using System;
    using MalmoFestival.Content.Resources;

    public class UpcomingEventsViewModel : PageViewModel, IUpcomingEventsViewModel
    {
        public UpcomingEventsViewModel()
        {
            TitleText = Labels.UpcommingEvents;
        }
    }
}

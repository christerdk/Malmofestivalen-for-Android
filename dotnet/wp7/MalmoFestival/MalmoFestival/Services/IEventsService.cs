namespace MalmoFestival.Services
{
    using System;
    using System.Collections.Generic;
    using MalmoFestival.Helpers;
    using MalmoFestival.Domain;

    public interface IEventsService
    {
        void GetAll();
        void GetBy(int id);

        event EventHandler<EventArgs<IList<EventItem>>> GetEventItemsTaskCompleted;
        event EventHandler<EventArgs<Exception>> GetEventItemsTaskFailed;

        event EventHandler<EventArgs<EventItem>> GetEventItemTaskCompleted;
    }
}

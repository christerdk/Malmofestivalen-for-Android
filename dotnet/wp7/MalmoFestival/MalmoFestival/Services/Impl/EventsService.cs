namespace MalmoFestival.Services.Impl
{
    using System;
    using System.Linq;
    using System.Collections.Generic;
    using System.Device.Location;

    using MalmoFestival.Helpers;
    using MalmoFestival.Domain;
    using MalmoFestival.Repositories;

    public class EventsService : IEventsService
    {
        private readonly IEventRepository _eventRepository;

        public EventsService(IEventRepository eventRepository)
        {
            _eventRepository = eventRepository;
            WireEvents();
        }

        private void WireEvents()
        {
            _eventRepository.FetchAllTaskCompleted += (s,e) => 
                {
                    GetEventItemsTaskCompleted.FireEvent(this, () => new EventArgs<IList<EventItem>>(e.Value.ToList()));
                };
            _eventRepository.GetByTaskCompleted += (s, e) =>
                {
                    GetEventItemTaskCompleted.FireEvent(this, () => new EventArgs<EventItem>(e.Value));
                };
        }

        public void GetAll()
        {
            _eventRepository.FetchAll();
        }

        public void GetBy(int id)
        {
            _eventRepository.GetBy(id);
        }

        public event EventHandler<EventArgs<IList<EventItem>>> GetEventItemsTaskCompleted;

        public event EventHandler<EventArgs<Exception>> GetEventItemsTaskFailed;

        public event EventHandler<EventArgs<EventItem>> GetEventItemTaskCompleted;
    }
}

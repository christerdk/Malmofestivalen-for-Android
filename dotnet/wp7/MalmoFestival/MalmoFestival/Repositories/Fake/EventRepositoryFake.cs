namespace MalmoFestival.Repositories.Fake
{
    using System;
    using System.Collections.Generic;
    using System.Device.Location;
    using MalmoFestival.Domain;
    using MalmoFestival.Helpers;

    public class EventRepositoryFake : IEventRepository
    {
        private IList<EventItem> _items; 

        public EventRepositoryFake()
        {
            _items = new List<EventItem>()
            {
                new EventItem() {
                     StartDate = new DateTime(2011,8,12, 20,0,0),
                     EndDate = new DateTime(2011,8,12,21,0,0),
                      Title = "Best music ever",
                       //Location = new Scene() {
                       //     Name = "Stora scenen",
                       //      Coordinate = new GeoCoordinate(55.122, 13.5454)
                       //}
                },
                new EventItem() {
                     StartDate = new DateTime(2011,8,13, 20,0,0),
                     EndDate = new DateTime(2011,8,13,21,0,0),
                      Title = "Not so good music",
                       //Location = new Scene() {
                       //     Name = "Stora scenen",
                       //     Coordinate = new GeoCoordinate(55.122, 13.5454)
                       //}
                }
            };

        }
        //public void GetAll()
        //{
        //    GetEventItemsTaskCompleted.FireEvent(this, () => new EventArgs<IList<EventItem>>(_items));
        //}

        //public event EventHandler<EventArgs<IList<EventItem>>> GetEventItemsTaskCompleted;

        //public event EventHandler<EventArgs<Exception>> GetEventItemsTaskFailed;

        //public IEnumerable<EventItem> FetchAll(string where, string orderBy, int limit, string columns, params object[] args)
        //{
        //    throw new NotImplementedException();
        //}

        //public IEnumerable<EventItem> Query(string sql, params object[] args)
        //{
        //    throw new NotImplementedException();
        //}

        //public object Scalar(string sql, params object[] args)
        //{
        //    throw new NotImplementedException();
        //}

        public void FetchAll(string where, string orderBy, int limit, string columns, params object[] args)
        {
            FetchAllTaskCompleted.FireEvent(this, () => new EventArgs<IEnumerable<EventItem>>(_items));
        }

        public void Scalar(string sql, params object[] args)
        {
            throw new NotImplementedException();
        }

        public event EventHandler<EventArgs<IEnumerable<EventItem>>> FetchAllTaskCompleted;

        public event EventHandler<EventArgs<object>> ScalarTaskCompleted;

        public event EventHandler<EventArgs<Exception>> TaskFailed;


        public int Insert(EventItem obj)
        {
            throw new NotImplementedException();
        }

        public int Update(EventItem obj)
        {
            throw new NotImplementedException();
        }

        public int Save(params EventItem[] things)
        {
            throw new NotImplementedException();
        }

        public int Delete(object key, string where, params object[] args)
        {
            throw new NotImplementedException();
        }


        public void GetBy(int id)
        {
            throw new NotImplementedException();
        }

        public event EventHandler<EventArgs<EventItem>> GetByTaskCompleted;
    }
}

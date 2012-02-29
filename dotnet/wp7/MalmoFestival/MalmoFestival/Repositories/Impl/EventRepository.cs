namespace MalmoFestival.Repositories.Impl
{
    using System;
    using System.Linq;
    using System.IO.IsolatedStorage;
    using System.IO;
    using Community.CsharpSqlite.SQLiteClient;
    using MalmoFestival.Helpers;
    using System.Collections.Generic;
    using MalmoFestival.Domain;

    public class EventRepository : Repository<EventItem>, IEventRepository
    {
        public EventRepository()
             : base ("events")
        {
        }

    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using MalmoFestival.Helpers;
using MalmoFestival.Domain;

namespace MalmoFestival.Repositories
{
    public interface IEventRepository : IRepository<EventItem>
    {
    }
}

namespace MalmoFestival.Services
{
    using System;
    using System.Collections.Generic;
    using MalmoFestival.Domain;
    using MalmoFestival.Helpers;

    public interface IMapService
    {
        void GetAll();

        event EventHandler<EventArgs<IList<MapItem>>> GetMapItemsTaskCompleted;
        event EventHandler<EventArgs<Exception>> GetMapItemsTaskFailed;
    
    }
}

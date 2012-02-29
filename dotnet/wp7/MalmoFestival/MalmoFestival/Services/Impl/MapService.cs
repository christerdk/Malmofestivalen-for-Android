namespace MalmoFestival.Services.Impl
{
    using System;
    using System.Collections.Generic;
    using MalmoFestival.Domain;
    using MalmoFestival.Helpers;
    using System.Device.Location;
    using Microsoft.Phone.Controls.Maps;

    public class MapService : IMapService
    {
        // TODO Remove later
        IList<MapItem> _items; 

        public MapService()
        {
            _items = new List<MapItem>()
            {
                new MapItem() {
                     Location = new GeoCoordinate(55.60, 13.1)
                },
                new MapItem() {
                     Location = new GeoCoordinate(55.60, 13.0)
                }
            };

        }

        public void GetAll()
        {
            // TODO Fetch async from repo
            GetMapItemsTaskCompleted.FireEvent(this, () => new EventArgs<IList<MapItem>>(_items));
        }


        public event EventHandler<EventArgs<IList<MapItem>>> GetMapItemsTaskCompleted;

        public event EventHandler<EventArgs<Exception>> GetMapItemsTaskFailed;
    }
}

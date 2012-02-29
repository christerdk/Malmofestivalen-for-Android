using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Device.Location;

namespace MalmoFestival.Services
{
    public interface IGeoLocationService
    {
        event EventHandler<GeoPositionChangedEventArgs<GeoCoordinate>> GeoWatcherPositionChanged;

        void Start();
        void Stop();
    }
}

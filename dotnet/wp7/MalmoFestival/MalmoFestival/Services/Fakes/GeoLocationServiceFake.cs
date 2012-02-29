namespace MalmoFestival.Services.Fakes
{
    using System;
    using System.Device.Location;

    public class GeoLocationServiceFake : IGeoLocationService
    {
        private GeoCoordinate fakePos = new GeoCoordinate(55.60, 13.00);
        public event EventHandler<GeoPositionChangedEventArgs<GeoCoordinate>> GeoWatcherPositionChanged;

        public void Start()
        {
            OnPositionChanged(fakePos);
        }

        public void Stop()
        {
        }

        private void OnPositionChanged(GeoCoordinate location)
        {
            if (GeoWatcherPositionChanged != null)
            {
                var pos = new GeoPosition<GeoCoordinate>()
                {
                    Location = location
                };
                var eventArgs = new GeoPositionChangedEventArgs<GeoCoordinate>(pos);
                GeoWatcherPositionChanged(this, eventArgs);
            }
        }

    }
}

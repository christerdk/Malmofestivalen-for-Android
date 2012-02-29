namespace MalmoFestival.Services.Impl
{
    using System;
    using System.Device.Location;

    public class GeoLocationService : IGeoLocationService, IDisposable
    {
        bool isDisposed = false;
        private GeoCoordinateWatcher _watcher = new GeoCoordinateWatcher();
        public event EventHandler<GeoPositionChangedEventArgs<GeoCoordinate>> GeoWatcherPositionChanged;

        public void Start()
        {
            _watcher.PositionChanged += OnPositionChanged;
            _watcher.Start();
        }

        public void Stop()
        {
            _watcher.PositionChanged -= OnPositionChanged;
            _watcher.Stop();
        }

        void OnPositionChanged(object sender, GeoPositionChangedEventArgs<GeoCoordinate> e)
        {
            if (GeoWatcherPositionChanged != null)
            {
                GeoWatcherPositionChanged.Invoke(sender, e);
            }
        }

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        protected virtual void Dispose(bool disposing)
        {
            if (!isDisposed)
            {
                if (disposing)
                {
                    _watcher.Dispose();
                }
                isDisposed = true;
            }
        }
        ~GeoLocationService()
        {
            Dispose(false);
        }
    }
}

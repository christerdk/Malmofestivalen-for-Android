namespace MalmoFestival.ViewModels
{
    using System;
    using System.Collections.ObjectModel;
    using System.Collections.Generic;
    using System.Device.Location;
    using Microsoft.Phone.Controls.Maps;
    using Microsoft.Phone.Controls.Maps.Core;

    using MalmoFestival.Content.Resources;
    using MalmoFestival.Domain;
    using MalmoFestival.Services;
    using MalmoFestival.Helpers;
    using MalmoFestival.Extensions;

    public class SceneMapViewModel : PageViewModel, ISceneMapViewModel
    {
        #region Constants

        private const double DefaultZoomLevel = 15.0;

        private const double MaxZoomLevel = 21.0;

        private const double MinZoomLevel = 12.0;

        #endregion

        private ObservableCollection<MapItem> _mapItemCollection;
        private readonly IMapService _mapService;
        private GeoCoordinate _center;
        private GeoCoordinate _currentLocation;
        private double _zoom;

        private readonly CredentialsProvider _credentialProvider = new ApplicationIdCredentialsProvider(App.MapId);
        private readonly IGeoLocationService _locationService;

        public SceneMapViewModel(IMapService mapService, IGeoLocationService locationService)
        {
            TitleText = Labels.SceneMap;
            _mapService = mapService;
            _locationService = locationService;

            WireEvents();
            Zoom = DefaultZoomLevel;
        }

        public override void Initialize()
        {
            _locationService.Start();
            _mapService.GetAll();
        }

        public ObservableCollection<MapItem> MapItems
        {
            get { return _mapItemCollection; }
            set
            {
                if (_mapItemCollection != value)
                {
                    _mapItemCollection = value;
                    RaisePropertyChanged("MapItems");
                }
            }
        }

        public GeoCoordinate Center
        {
            get { return _center; }
            set
            {
                if (_center != value)
                {
                    _center = value;
                    RaisePropertyChanged("Center");
                }
            }
        }

        public double Zoom
        {
            get { return _zoom; }
            set
            {
                var coercedZoom = Math.Max(MinZoomLevel, Math.Min(MaxZoomLevel, value));
                if (_zoom != coercedZoom)
                {
                    _zoom = value;
                    RaisePropertyChanged("Zoom");
                }
            }
        }

        public GeoCoordinate CurrentLocation
        {
            get { return _currentLocation; }
            set
            {
                if (_currentLocation == null)
                {
                    _currentLocation = value;
                    RaisePropertyChanged("CurrentLocation");
                }
            }
        }

        public CredentialsProvider CredentialProvider { get { return _credentialProvider; } }

        private void WireEvents()
        {
            _mapService.GetMapItemsTaskCompleted += OnGetMaptemsTaskCompleted;
            _locationService.GeoWatcherPositionChanged += OnGeoWatcherPositionChanged;
        }

        private void OnGetMaptemsTaskCompleted(object sender, EventArgs<IList<MapItem>> e)
        {
            MapItems = new ObservableCollection<MapItem>();
            foreach (var item in e.Value)
            {
                MapItems.Add(item);
            }
        }

        private void OnGeoWatcherPositionChanged(object sender, GeoPositionChangedEventArgs<GeoCoordinate> e)
        {
            Center = e.Position.Location.Clone();
            CurrentLocation = e.Position.Location.Clone();
        }

    }
}

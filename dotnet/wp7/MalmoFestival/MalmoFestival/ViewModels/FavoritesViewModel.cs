namespace MalmoFestival.ViewModels
{
    using System.Collections.ObjectModel;
    using System.Collections.Generic;

    using GalaSoft.MvvmLight;
    
    using MalmoFestival.Services;
    using MalmoFestival.Content.Resources;

    public class FavoritesViewModel : PageViewModel, IFavoritesViewModel
    {
        public FavoritesViewModel()
        {
            FavoritesCollection = new ObservableCollection<string>();
            TitleText = Labels.Favorites;
        }

        public override void Initialize()
        {
            var list = new List<string>
            {
                "favorite1",
                "favorite2",
                "favorite3",
                "favorite4",
                "favorite5"
            };
            list.ForEach(f => FavoritesCollection.Add(f));
        }

        private ObservableCollection<string> _favoritesCollection;

        public ObservableCollection<string> FavoritesCollection
        {
            get { return _favoritesCollection; }
            set
            {
                if (_favoritesCollection != value)
                {
                    _favoritesCollection = value;
                    RaisePropertyChanged("FavoritesCollection");
                }
            }
        }
    }
}

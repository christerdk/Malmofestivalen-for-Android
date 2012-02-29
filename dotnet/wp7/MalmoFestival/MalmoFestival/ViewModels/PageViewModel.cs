namespace MalmoFestival.ViewModels
{
    using System.ComponentModel;
    using GalaSoft.MvvmLight;

    public abstract class PageViewModel : ViewModelBase, IPageViewModel
    {
        private string _titleText;

        public string TitleText
        {
            get { return _titleText; }
            set
            {
                if (_titleText != value)
                {
                    _titleText = value;
                    RaisePropertyChanged("TitleText");
                }
            }
        }

        public virtual void Initialize()
        {
        }
    }
}

namespace MalmoFestival.ViewModels
{
    using System.Windows.Input;

    public interface IPageViewModel
    {
        void Initialize();
    }

    public interface IEventsViewModel : IPageViewModel
    {
        ICommand ShowEventDetailCommand { get; }
    }

    public interface IFavoritesViewModel : IPageViewModel
    {
    }

    public interface ISceneListViewModel : IPageViewModel
    {

    }

    public interface ISceneMapViewModel : IPageViewModel
    {

    }

    public interface IUpcomingEventsViewModel : IPageViewModel
    {

    }

}

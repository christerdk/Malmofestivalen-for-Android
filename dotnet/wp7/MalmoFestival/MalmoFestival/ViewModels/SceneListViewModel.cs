namespace MalmoFestival.ViewModels
{
    using System;
    using MalmoFestival.Content.Resources;

    public class SceneListViewModel : PageViewModel, ISceneListViewModel
    {
        public SceneListViewModel()
        {
            TitleText = Labels.SceneList;
        }

    }
}

namespace MalmoFestival.Helpers
{
    using System.Windows;
    using System.Windows.Controls;

    public class DataTemplateSelector : ContentControl
    {
        protected override void OnContentChanged(object oldContent, object newContent)
        {
            ContentTemplate = this.FindResource<DataTemplate>(newContent.GetType().FullName);
        }
    }
}

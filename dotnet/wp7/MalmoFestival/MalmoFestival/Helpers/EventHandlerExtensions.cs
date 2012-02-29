namespace MalmoFestival.Helpers
{
    using System;

    public static class EventHandlerExtensions
    {
        public static void FireEvent<T>(this EventHandler<T> eventHandler, object sender, Func<T> createEventArgs)
            where T : EventArgs
        {
            EventHandler<T> handler = eventHandler;

            if (handler != null)
            {
                handler(sender, createEventArgs());
            }
        }

        public static void FireEvent(this EventHandler eventHandler, object sender, Func<EventArgs> createEventArgs)
        {
            EventHandler handler = eventHandler;

            if (handler != null)
            {
                handler(sender, createEventArgs());
            }
        }
    }
}

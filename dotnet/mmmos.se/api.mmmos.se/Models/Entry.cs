namespace api.mmmos.se.Models
{
    /// <summary>
    /// Representation of an entry in Google Analytics
    /// </summary>
    public class Entry
    {
        /// <summary>
        /// Url in Google Analytics
        /// </summary>
        public string PagePath { get; set; }
        /// <summary>
        /// Visits to the page
        /// </summary>
        public int Count { get; set; }
    }
}
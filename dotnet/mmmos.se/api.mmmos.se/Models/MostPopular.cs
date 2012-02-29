namespace api.mmmos.se.Models
{
    /// <summary>
    /// Container for a most popular entry
    /// </summary>
    public class MostPopular
    {
        /// <summary>
        /// Event id
        /// </summary>
        public string ScheduleId { get; set; }
        /// <summary>
        /// The "score" of the item
        /// </summary>
        public int Value { get; set; }
    }
}
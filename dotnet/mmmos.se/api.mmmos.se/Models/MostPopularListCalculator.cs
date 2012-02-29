using System.Collections.Generic;
using System.Linq;

namespace api.mmmos.se.Models
{
    /// <summary>
    /// Calculates the most popular list from a list of entries from Google Analytics
    /// </summary>
    public class MostPopularListCalculator
    {
        /// <summary>
        /// Will return a sorted list with from the favorite entries
        /// </summary>
        /// <param name="favoriteEntries"></param>
        /// <returns></returns>
        public IEnumerable<MostPopular> CalculateMostPopularList(IEnumerable<Entry> favoriteEntries)
        {
            var favorites = new Favorites(favoriteEntries);
            return from favorite in favorites
                   orderby favorite.Value descending
                   where favorite.Value > 0
                   select new MostPopular {ScheduleId = favorite.Key, Value = favorite.Value};
        }
    }
}
using System.Collections.Generic;
using System.Text.RegularExpressions;

namespace api.mmmos.se.Models
{
    /// <summary>
    /// Wrapper for favorites, will take a list of entries from Google Analytics and build a Favorites dictionary
    /// </summary>
    public class Favorites : Dictionary<string,int>
    {
        public Favorites(IEnumerable<Entry> entries)
        {
            const string pattern = @"\/data\/favorite\/(?<operation>(\w+))\/(?<scheduleId>(\w+))";

            foreach (var entry in entries)
            {
                var match = Regex.Match(entry.PagePath, pattern);
                if (!match.Success) continue;
                
                string operation = match.Groups["operation"].Value;
                string scheduleId = match.Groups["scheduleId"].Value;

                switch (operation)
                {
                    case "add":
                        Add(scheduleId, entry.Count);
                        break;
                    case "remove":
                        Remove(scheduleId, entry.Count);
                        break;
                }
            }
        }

        public new void Add(string id, int count)
        {
            if (ContainsKey(id))
                this[id] += count;
            else
                this[id] = count;
        }

        public void Remove(string id, int count)
        {
            if (ContainsKey(id))
                this[id] -= count;
            else
            {
                this[id] = -count;
            }
        }
    }
}
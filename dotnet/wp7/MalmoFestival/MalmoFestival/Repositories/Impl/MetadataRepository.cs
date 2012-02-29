namespace MalmoFestival.Repositories.Impl
{
    using System;
    using System.Linq;
    using MalmoFestival.Domain;

    public class MetadataRepository : Repository<Metadata> , IMetadataRepository
    {
        public MetadataRepository()
            : base("DatabaseMeta")
        {

        }
        public int GetVersion()
        {
            // Should be async 
            var result = FetchAllInternal("MetaKey = 'version'").SingleOrDefault();
            if (result == null)
                return 0;
            return Convert.ToInt32(result.MetaData);
        }
    }
}


namespace MalmoFestival.Repositories
{
    using System;
    using MalmoFestival.Domain;

    public interface IMetadataRepository : IRepository<Metadata>
    {
        int GetVersion();
    }
}

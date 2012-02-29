namespace MalmoFestival.Repositories
{
    using System;
    using System.Collections.Generic;
    using MalmoFestival.Helpers;

    public interface IRepository<T> where T : class, new()
    {
        void FetchAll(string where = "", string orderBy = "", int limit = 0, string columns = "*", params object[] args);
        void GetBy(int id);
        void Scalar(string sql, params object[] args);
        int Insert(T obj);
        int Update(T obj);
        int Save(params T[] things);
        int Delete(object key = null, string where = "", params object[] args);

        event EventHandler<EventArgs<IEnumerable<T>>> FetchAllTaskCompleted;
        event EventHandler<EventArgs<T>> GetByTaskCompleted;
        event EventHandler<EventArgs<object>> ScalarTaskCompleted;
        event EventHandler<EventArgs<Exception>> TaskFailed;

        // Missing save update and delete
    }
}

package com.lbd.batis.executor;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.lbd.batis.cache.Cache;
import com.lbd.batis.cache.CacheKey;
import com.lbd.batis.cache.CacheManage;
import com.lbd.batis.cache.impl.PerpetualCache;
import com.lbd.batis.mapping.MappedStatement;


public class CachingExecutor implements Executor {

    private Executor delegate;

    private static Cache cache = new PerpetualCache();

    private static CacheManage cacheManage = new CacheManage();

    public CachingExecutor(Executor executor) {
        this.delegate = executor;
    }

    private CacheKey generateCacheKey(MappedStatement ms, Map<String, Object> parameter) {
        CacheKey cacheKey = new CacheKey();

        cacheKey.update(ms.getOriginSql());

        Object[] keys = parameter.keySet().toArray();
        Arrays.sort(keys);

        for (Object key: keys) {
            cacheKey.update(key);
            cacheKey.update(parameter.get(key));
        }

        return cacheKey;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> doQuery(MappedStatement ms, Map<String, Object> parameter)
            throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        CacheKey cacheKey = generateCacheKey(ms, parameter);

        if (!cache.containsKey(cacheKey)) {
            cacheManage.putCacheKey(CacheManage.extractTableName(ms, parameter), cacheKey);
            Object obj = delegate.doQuery(ms, parameter);
            cache.putObject(cacheKey, obj);
            return (List<E>) obj;
        }

        return (List<E>) cache.getObject(cacheKey);
    }

    @Override
    public int doUpdate(MappedStatement ms, Map<String, Object> parameter) throws SQLException {
        cacheManage.clearCacheByTableName(CacheManage.extractTableName(ms, parameter), cache);
        return delegate.doUpdate(ms, parameter);
    }
}

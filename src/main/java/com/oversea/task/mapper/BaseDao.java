package com.oversea.task.mapper;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import java.util.Collection;

public class BaseDao extends SqlSessionDaoSupport {

    private static int BATCH_SIZE = 20;

    /**
     * 批量操作
     *
     * @param domains
     * @param statement
     * @param isUpdate
     */
    public <T> void batch(Collection<T> domains, String statement, boolean isUpdate) {
        if (domains == null || domains.size() == 0) {
            return;
        }
        SqlSession batchSqlSession = ((SqlSessionTemplate) super.getSqlSession()).getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        int num = 0;
        try {
            for (T t : domains) {
                if (isUpdate) {
                    batchSqlSession.update(statement, t);
                } else {
                    batchSqlSession.insert(statement, t);
                }
                num++;
                if (num % BATCH_SIZE == 0) {
                    batchSqlSession.commit();
                    batchSqlSession.clearCache();
                }
            }
            batchSqlSession.commit();
            batchSqlSession.clearCache();
        } finally {
            batchSqlSession.close();
        }
    }
}

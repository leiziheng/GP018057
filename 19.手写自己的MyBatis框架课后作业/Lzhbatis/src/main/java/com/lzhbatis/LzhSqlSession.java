package com.lzhbatis;

public class LzhSqlSession {

    private LzhConfiguration configuration;
    private LzhExecutor executor;

    public LzhSqlSession(LzhConfiguration configuration, LzhExecutor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    public <T> T selectOne(String statementId, Object parameter) {
        String sql = configuration.sqlMappings.getString(statementId);
        if (null != sql && !"".equals(sql)) {
            return executor.query(sql, parameter);
        }
        return null;
    }

    public <T> T getMapper(Class clazz) {
        return (T) configuration.getMapper(clazz, this);
    }
}

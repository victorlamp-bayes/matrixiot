package com.victorlamp.matrixiot.service.agent.utils;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.dynamic.datasource.destroyer.DataSourceDestroyer;
import com.baomidou.dynamic.datasource.destroyer.DefaultDataSourceDestroyer;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExternalDbUtil {
    private static final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    private static final boolean GRACE_DESTROY = false;

//    @Autowired
//    private DynamicRoutingDataSource dynamicRoutingDataSource;

    public static Connection getConnection(String type, DataSourceConfig dbConfig) throws Exception {
        DataSource dataSource = getDataSource(type);
        if (dataSource != null) {
            return dataSource.getConnection();
        }

        if (dbConfig != null) {
            addDataSource(type, dbConfig);
            dataSource = getDataSource(type);
            if (dataSource != null) {
                return dataSource.getConnection();
            }
        }

        return null;
    }

    public static void closeDataSource(String name) {
        DataSource dataSource = getDataSource(name);
        if (dataSource != null) {
            closeDataSource(name, dataSource, GRACE_DESTROY);
        }
    }

    public static void closeDataSource(String name, boolean graceDestroy) {
        DataSource dataSource = getDataSource(name);
        if (dataSource != null) {
            closeDataSource(name, dataSource, graceDestroy);
        }
    }

    private static void closeDataSource(String name, DataSource dataSource, boolean graceDestroy) {
        if (null != dataSource) {
            DataSourceDestroyer destroyer = new DefaultDataSourceDestroyer();
            if (graceDestroy) {
                destroyer.asyncDestroy(name, dataSource);
            } else {
                destroyer.destroy(name, dataSource);
            }
        }
    }

    private static DataSource getDataSource(String name) {
        if (!dataSourceMap.containsKey(name) || ObjUtil.isNull(dataSourceMap.get(name))) {
            return null;
        }

        return dataSourceMap.get(name);
    }

    private static synchronized void addDataSource(String name, DataSourceConfig dbConfig) {
        // 创建数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dbConfig.getJdbcUrl());
        dataSource.setUsername(dbConfig.getUsername());
        dataSource.setPassword(dbConfig.getPassword());

        // TODO: 根据不同的数据库类型使用不同的JDBC驱动
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        dataSource.setDriverClassName(driverName);

        DataSource oldDataSource = dataSourceMap.put(name, dataSource);

        if (ObjUtil.isNotNull(oldDataSource)) {
            closeDataSource(name, oldDataSource, GRACE_DESTROY);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataSourceConfig {
        private String jdbcUrl;
        private String username;
        private String password;
    }
}

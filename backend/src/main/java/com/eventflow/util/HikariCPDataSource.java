package com.eventflow.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class HikariCPDataSource {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        Properties props = new Properties();
        try (InputStream input = HikariCPDataSource.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
            }
            props.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.user"));
        config.setPassword(props.getProperty("db.password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    private HikariCPDataSource() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}

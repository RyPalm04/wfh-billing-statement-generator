package com.palmer.billingstatementgenerator.db;

import org.h2.jdbcx.JdbcDataSource;

public final class TestDatabase {

    private TestDatabase() {
    }

    public static void init() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");
        Database.initWithDataSource(ds);
    }
}
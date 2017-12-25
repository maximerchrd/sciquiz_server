package com.sciquizapp.sciquizserver.database_management;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Created by maximerichard on 24.11.17.
 */
public class DbTableClasses {
    static public void createTableSubject(Connection connection, Statement statement) {
        try {
            statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS classes " +
                    "(ID_CLASS       INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " ID_CLASS_GLOBAL      INT     NOT NULL, " +
                    " NAME      TEXT     NOT NULL, " +
                    " LEVEL      TEXT     NOT NULL, " +
                    " YEAR      TEXT     NOT NULL) ";
            statement.executeUpdate(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
}

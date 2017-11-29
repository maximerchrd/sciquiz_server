package com.sciquizapp.sciquizserver.database_management;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Created by maximerichard on 24.11.17.
 */
public class DbTableStudents {
    static public void createTableSubject(Connection connection, Statement statement) {
        try {
            statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS students " +
                    "(ID_STUDENT       INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " ID_STUDENT_GLOBAL      INT     NOT NULL, " +
                    " FIRST_NAME      TEXT     NOT NULL, " +
                    " SURNAME      TEXT     NOT NULL, " +
                    " DATE_BIRTH      TEXT     NOT NULL, " +
                    " QUANTITATIVE_EVAL      TEXT     NOT NULL, " +
                    " QUALITATIVE_EVAL           TEXT    NOT NULL) ";
            statement.executeUpdate(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
}

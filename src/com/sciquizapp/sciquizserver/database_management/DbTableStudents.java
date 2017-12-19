package com.sciquizapp.sciquizserver.database_management;

import java.sql.Connection;
import java.sql.DriverManager;
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
                    " MAC_ADDRESS      TEXT     NOT NULL, " +
                    " FIRST_NAME      TEXT     NOT NULL, " +
                    " SURNAME      TEXT     NOT NULL, " +
                    " DATE_BIRTH      TEXT     NOT NULL, " +
                    " QUANTITATIVE_EVAL      TEXT     NOT NULL, " +
                    " QUALITATIVE_EVAL           TEXT    NOT NULL, " +
                    " UNIQUE (MAC_ADDRESS))";
            statement.executeUpdate(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    static public void addStudent(String address, String name) {
        Connection c = null;
        Statement stmt = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String sql = 	"INSERT OR IGNORE INTO students (ID_STUDENT_GLOBAL,MAC_ADDRESS,FIRST_NAME,SURNAME,DATE_BIRTH,QUANTITATIVE_EVAL,QUALITATIVE_EVAL) " +
                    "VALUES ('" +
                    2000000 + "','" +
                    address + "','" +
                    name + "','none','none','none','none');";
            stmt.executeUpdate(sql);
            sql = "UPDATE students SET ID_STUDENT_GLOBAL = 2000000 + ID_STUDENT WHERE ID_STUDENT = (SELECT MAX(ID_STUDENT) FROM students)";
            stmt.executeUpdate(sql);
            sql = "UPDATE students SET FIRST_NAME = '" + name + "' WHERE MAC_ADDRESS = '" + address + "';";
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
}

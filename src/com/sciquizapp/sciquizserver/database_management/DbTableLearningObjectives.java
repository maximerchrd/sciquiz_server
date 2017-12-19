package com.sciquizapp.sciquizserver.database_management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by maximerichard on 24.11.17.
 */
public class DbTableLearningObjectives {
    static public void createTableSubject(Connection connection, Statement statement) {
        try {
            statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS learning_objectives " +
                    "(ID_OBJECTIVE       INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " ID_OBJECTIVE_GLOBAL      INT     NOT NULL, " +
                    " OBJECTIVE      TEXT     NOT NULL, " +
                    " LEVEL_COGNITIVE_ABILITY      INT     NOT NULL, " +
                    " UNIQUE (OBJECTIVE) ); ";
            statement.executeUpdate(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    static public void addObjective(String objective, int level_cognitive_ability) throws Exception {
        Connection c = null;
        Statement stmt = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String sql = 	"INSERT OR IGNORE INTO learning_objectives (ID_OBJECTIVE_GLOBAL,OBJECTIVE,LEVEL_COGNITIVE_ABILITY) " +
                    "VALUES ('" +
                    2000000 + "','" +
                    objective + "','" +
                    level_cognitive_ability +"');";
            stmt.executeUpdate(sql);
            sql = "UPDATE learning_objectives SET ID_OBJECTIVE_GLOBAL = 2000000 + ID_OBJECTIVE WHERE ID_OBJECTIVE = (SELECT MAX(ID_OBJECTIVE) FROM learning_objectives)";
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

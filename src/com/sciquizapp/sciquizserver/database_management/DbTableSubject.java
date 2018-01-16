package com.sciquizapp.sciquizserver.database_management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

/**
 * Created by maximerichard on 24.11.17.
 */
public class DbTableSubject {
    static public void createTableSubject(Connection connection, Statement statement) {
        try {
            statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS subjects " +
                    "(ID_SUBJECT       INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " ID_SUBJECT_GLOBAL      INT     NOT NULL, " +
                    " SUBJECT           TEXT    NOT NULL, UNIQUE (SUBJECT)); ";
            statement.executeUpdate(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    static public void addSubject(String subject) throws Exception {
        Connection c = null;
        Statement stmt = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String sql = 	"INSERT OR IGNORE INTO subjects (ID_SUBJECT_GLOBAL,SUBJECT) " +
                    "VALUES ('" +
                    2000000 + "','" +
                    subject +"');";
            stmt.executeUpdate(sql);
            sql = "UPDATE subjects SET ID_SUBJECT_GLOBAL = 2000000 + ID_SUBJECT WHERE ID_SUBJECT = (SELECT MAX(ID_SUBJECT) FROM subjects);";
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    static public Vector<String> getSubjectsForQuestionID(int questionID) {
        Vector<String> subjects = new Vector<>();
        Connection c = null;
        Statement stmt = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String query = "SELECT SUBJECT FROM subjects " +
                    "INNER JOIN question_subject_relation ON subjects.ID_SUBJECT_GLOBAL = question_subject_relation.ID_SUBJECT_GLOBAL " +
                    "INNER JOIN multiple_choice_questions ON multiple_choice_questions.ID_GLOBAL = question_subject_relation.ID_GLOBAL " +
                    "WHERE multiple_choice_questions.ID_GLOBAL = '" + questionID + "';";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                subjects.add(rs.getString("SUBJECT"));
            }
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        return subjects;
    }
}

package com.sciquizapp.sciquizserver.database_management;

import com.sciquizapp.sciquizserver.questions.QuestionMultipleChoice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by maximerichard on 24.11.17.
 */
public class DbTableRelationQuestionSubject {
    static public void createTableSubject(Connection connection, Statement statement) {
        try {
            statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS question_subject_relation " +
                    "(ID_SUBJ_REL       INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " ID_GLOBAL      INT     NOT NULL, " +
                    " ID_SUBJECT_GLOBAL      INT     NOT NULL, " +
                    " SUBJECT_LEVEL      INT NOT NULL) ";
            statement.executeUpdate(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    /**
     * Method that adds a relation between a question and a subject
     * by linking the last added question with the subject given as parameter
     * @param subject
     * @throws Exception
     */
    static public void addRelationQuestionSubject(String subject) throws Exception {
        Connection c = null;
        Statement stmt = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String sql = "INSERT INTO question_subject_relation (ID_GLOBAL, ID_SUBJECT_GLOBAL, SUBJECT_LEVEL) SELECT t1.ID_GLOBAL,t2.ID_SUBJECT_GLOBAL," +
                    "'1' FROM generic_questions t1, subjects t2 WHERE t1.ID_QUESTION = (SELECT MAX(ID_QUESTION) FROM generic_questions) " +
                    "AND t2.SUBJECT='" + subject + "';";
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

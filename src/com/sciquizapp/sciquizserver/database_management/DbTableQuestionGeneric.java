package com.sciquizapp.sciquizserver.database_management;

import com.sciquizapp.sciquizserver.questions.QuestionShortAnswer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by maximerichard on 24.11.17.
 * Question Type: 0 = question multiple choice; 1 = question short answer
 */
public class DbTableQuestionGeneric {
    static public void createTableQuestionGeneric(Connection connection, Statement statement) {
        try {
            statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS generic_questions " +
                    "(ID_QUESTION       INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " ID_GLOBAL           INT    NOT NULL, " +
                    " QUESTION_TYPE      INT     NOT NULL) ";
            statement.executeUpdate(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    static public String addGenericQuestion(int questionType) throws Exception {
        String questID = "-1";
        Connection c = null;
        Statement stmt = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String sql = 	"INSERT INTO generic_questions (ID_GLOBAL,QUESTION_TYPE) " +
                    "VALUES ('" +
                    2000000 + "','" +
                    questionType +"');";
            stmt.executeUpdate(sql);
            sql = "UPDATE generic_questions SET ID_GLOBAL = ID_GLOBAL + ID_QUESTION WHERE ID_QUESTION = (SELECT MAX(ID_QUESTION) FROM generic_questions)";
            stmt.executeUpdate(sql);

            sql = "SELECT ID_GLOBAL FROM generic_questions WHERE ID_GLOBAL = (SELECT MAX(ID_GLOBAL) FROM generic_questions)";
            ResultSet result_query = stmt.executeQuery(sql);
            questID = result_query.getString(1);
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return questID;
    }
}

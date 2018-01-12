package com.sciquizapp.sciquizserver.database_management;

import com.sciquizapp.sciquizserver.questions.QuestionMultipleChoice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by maximerichard on 24.11.17.
 */
public class DbTableQuestionMultipleChoice {
    static public void createTableQuestionMultipleChoice(Connection connection, Statement statement) {
        try {
            statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS multiple_choice_questions " +
                    "(ID_QUESTION       INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " LEVEL      INT     NOT NULL, " +
                    " QUESTION           TEXT    NOT NULL, " +
                    " OPTION0           TEXT    NOT NULL, " +
                    " OPTION1           TEXT    NOT NULL, " +
                    " OPTION2           TEXT    NOT NULL, " +
                    " OPTION3           TEXT    NOT NULL, " +
                    " OPTION4           TEXT    NOT NULL, " +
                    " OPTION5           TEXT    NOT NULL, " +
                    " OPTION6           TEXT    NOT NULL, " +
                    " OPTION7           TEXT    NOT NULL, " +
                    " OPTION8           TEXT    NOT NULL, " +
                    " OPTION9           TEXT    NOT NULL, " +
                    " TRIAL0           TEXT    NOT NULL, " +
                    " TRIAL1           TEXT    NOT NULL, " +
                    " TRIAL2           TEXT    NOT NULL, " +
                    " TRIAL3           TEXT    NOT NULL, " +
                    " TRIAL4           TEXT    NOT NULL, " +
                    " TRIAL5           TEXT    NOT NULL, " +
                    " TRIAL6           TEXT    NOT NULL, " +
                    " TRIAL7           TEXT    NOT NULL, " +
                    " TRIAL8           TEXT    NOT NULL, " +
                    " TRIAL9           TEXT    NOT NULL, " +
                    " NB_CORRECT_ANS        INT     NOT NULL, " +
                    " IMAGE_PATH           TEXT    NOT NULL, " +
                    " ID_GLOBAL      INT     NOT NULL) ";
            statement.executeUpdate(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    /**
     * method for inserting new question into table multiple_choice_question
     * @param quest
     * @throws Exception
     */
    static public void addMultipleChoiceQuestion(QuestionMultipleChoice quest) throws Exception {
        Connection c = null;
        Statement stmt = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String sql = 	"INSERT INTO multiple_choice_questions (LEVEL,QUESTION,OPTION0," +
                    "OPTION1,OPTION2,OPTION3,OPTION4,OPTION5,OPTION6,OPTION7,OPTION8,OPTION9,TRIAL0,TRIAL1,TRIAL2,TRIAL3,TRIAL4,TRIAL5,TRIAL6,TRIAL7," +
                    "TRIAL8,TRIAL9,NB_CORRECT_ANS,IMAGE_PATH,ID_GLOBAL) " +
                    "VALUES ('" +
                    quest.getLEVEL() + "','" +
                    quest.getQUESTION() + "','" +
                    quest.getOPT0() + "','" +
                    quest.getOPT1() + "','" +
                    quest.getOPT2() + "','" +
                    quest.getOPT3() + "','" +
                    quest.getOPT4() + "','" +
                    quest.getOPT5() + "','" +
                    quest.getOPT6() + "','" +
                    quest.getOPT7() + "','" +
                    quest.getOPT8() + "','" +
                    quest.getOPT9() + "','" +
                    quest.getTRIAL0() + "','" +
                    quest.getTRIAL1() + "','" +
                    quest.getTRIAL2() + "','" +
                    quest.getTRIAL3() + "','" +
                    quest.getTRIAL4() + "','" +
                    quest.getTRIAL5() + "','" +
                    quest.getTRIAL6() + "','" +
                    quest.getTRIAL7() + "','" +
                    quest.getTRIAL8() + "','" +
                    quest.getTRIAL9() + "','" +
                    quest.getNB_CORRECT_ANS() + "','" +
                    quest.getIMAGE() + "','" +
                    2000000 +"');";
            stmt.executeUpdate(sql);
            sql = "UPDATE multiple_choice_questions SET ID_GLOBAL = ID_GLOBAL + ID_QUESTION WHERE ID_QUESTION = (SELECT MAX(ID_QUESTION) FROM multiple_choice_questions)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    static public int getLastIDGlobal() throws Exception {
        Connection c = null;
        Statement stmt = null;
        stmt = null;
        int last_id_global = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String sql = 	"SELECT  ID_GLOBAL FROM multiple_choice_questions WHERE ID_QUESTION = (SELECT MAX(ID_QUESTION) FROM multiple_choice_questions);";
            ResultSet result_query = stmt.executeQuery(sql);
            last_id_global = Integer.parseInt(result_query.getString(1));
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return last_id_global;
    }
    static public void removeMultipleChoiceQuestionWithID(String ID) throws Exception {
        Connection c = null;
        Statement stmt = null;
        stmt = null;
        int last_id_global = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String sql = 	"DELETE FROM multiple_choice_questions WHERE ID_GLOBAL = '" + ID + "';";
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
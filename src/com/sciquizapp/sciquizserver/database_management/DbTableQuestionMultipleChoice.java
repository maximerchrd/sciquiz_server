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
        String globalID = DbTableQuestionGeneric.addGenericQuestion(0);
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
                    globalID +"');";
            System.out.println(globalID);
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    /**
     * method for inserting new question into table multiple_choice_question
     * @param questionID
     * @throws Exception
     */
    static public QuestionMultipleChoice getMultipleChoiceQuestionWithID(int questionID) throws Exception {
        QuestionMultipleChoice questionMultipleChoice = new QuestionMultipleChoice();
        questionMultipleChoice.setID(questionID);
        Connection c = null;
        Statement stmt = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String query = 	"SELECT LEVEL,QUESTION,OPTION0," +
                    "OPTION1,OPTION2,OPTION3,OPTION4,OPTION5,OPTION6,OPTION7,OPTION8,OPTION9,TRIAL0,TRIAL1,TRIAL2,TRIAL3,TRIAL4,TRIAL5,TRIAL6,TRIAL7," +
                    "TRIAL8,TRIAL9,NB_CORRECT_ANS,IMAGE_PATH FROM multiple_choice_questions WHERE ID_GLOBAL='" + questionID + "';";

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                questionMultipleChoice.setLEVEL(rs.getString("LEVEL"));
                questionMultipleChoice.setQUESTION(rs.getString("QUESTION"));
                questionMultipleChoice.setOPT0(rs.getString("OPTION0"));
                questionMultipleChoice.setOPT1(rs.getString("OPTION1"));
                questionMultipleChoice.setOPT2(rs.getString("OPTION2"));
                questionMultipleChoice.setOPT3(rs.getString("OPTION3"));
                questionMultipleChoice.setOPT4(rs.getString("OPTION4"));
                questionMultipleChoice.setOPT5(rs.getString("OPTION5"));
                questionMultipleChoice.setOPT6(rs.getString("OPTION6"));
                questionMultipleChoice.setOPT7(rs.getString("OPTION7"));
                questionMultipleChoice.setOPT8(rs.getString("OPTION8"));
                questionMultipleChoice.setOPT9(rs.getString("OPTION9"));
                questionMultipleChoice.setTRIAL0(rs.getString("TRIAL0"));
                questionMultipleChoice.setTRIAL1(rs.getString("TRIAL1"));
                questionMultipleChoice.setTRIAL2(rs.getString("TRIAL2"));
                questionMultipleChoice.setTRIAL3(rs.getString("TRIAL3"));
                questionMultipleChoice.setTRIAL4(rs.getString("TRIAL4"));
                questionMultipleChoice.setTRIAL5(rs.getString("TRIAL5"));
                questionMultipleChoice.setTRIAL6(rs.getString("TRIAL6"));
                questionMultipleChoice.setTRIAL7(rs.getString("TRIAL7"));
                questionMultipleChoice.setTRIAL8(rs.getString("TRIAL8"));
                questionMultipleChoice.setTRIAL9(rs.getString("TRIAL9"));
                questionMultipleChoice.setNB_CORRECT_ANS(rs.getInt("NB_CORRECT_ANS"));
                questionMultipleChoice.setIMAGE(rs.getString("IMAGE_PATH"));
            }
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return questionMultipleChoice;
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
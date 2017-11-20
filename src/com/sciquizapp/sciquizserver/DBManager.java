package com.sciquizapp.sciquizserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import com.sciquizapp.sciquizserver.Question;

public class DBManager {

	public void createDBIfNotExists() throws Exception {
		// connects to db and create it if necessary
		// closes db afterwards
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}
	public void createQuestionsTableIfNotExists() throws Exception {
		// First create the table if it doesn't exist
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");

			stmt = c.createStatement();
			String sql = "DROP TABLE IF EXISTS 'question'; CREATE TABLE IF NOT EXISTS question " +
					"(ID_QUESTION       INTEGER PRIMARY KEY AUTOINCREMENT," +
					" SUBJECT           TEXT    NOT NULL, " +
					" LEVEL      INT     NOT NULL, " +
					" QUESTION           TEXT    NOT NULL, " +
					" ANSWER           TEXT    NOT NULL, " +
					" OPTIONA           TEXT    NOT NULL, " +
					" OPTIONB           TEXT    NOT NULL, " +
					" OPTIONC           TEXT    NOT NULL, " +
					" OPTIOND           TEXT    NOT NULL, " +
					" TRIAL1           TEXT    NOT NULL, " +
					" TRIAL2           TEXT    NOT NULL, " +
					" TRIAL3           TEXT    NOT NULL, " +
					" TRIAL4           TEXT    NOT NULL, " +
					" IMAGE_PATH           TEXT    NOT NULL, " +
					" ID_GLOBAL      INT     NOT NULL) ";
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}

		// Then store the questions into the table
		Question q1=new Question("TP","1","Comment appelle-t-on l''instrument ci-dessous","burette","pipette","buchner", "compte-gouttes", "burette","res/drawable/burette.jpg");
		q1.setID(100);
		this.addQuestion(q1);
		Question q2=new Question("TP","1","Picture of sky","burette","pipette","buchner", "compte-gouttes", "burette","res/drawable/medium_pic.jpg");
		q2.setID(101);
		this.addQuestion(q2);
		Question q3=new Question("TP","1","small pic","pic1","pic2","pic3", "pic4", "pic1","res/drawable/small.jpg");
		q3.setID(102);
		this.addQuestion(q3);

		//Create multiple choice questions table if it doesn't exist
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");

			stmt = c.createStatement();
			String sql = "DROP TABLE IF EXISTS 'multiple_choice_questions'; CREATE TABLE IF NOT EXISTS multiple_choice_questions " +
					"(ID_QUESTION       INTEGER PRIMARY KEY AUTOINCREMENT," +
					" SUBJECT           TEXT    NOT NULL, " +
					" LEVEL      INT     NOT NULL, " +
					" QUESTION           TEXT    NOT NULL, " +
					" ANSWER           TEXT    NOT NULL, " +
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
					" IMAGE_PATH           TEXT    NOT NULL, " +
					" ID_GLOBAL      INT     NOT NULL) ";
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		//Create short answer questions table if it doesn't exist
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");

			stmt = c.createStatement();
			String sql = "DROP TABLE IF EXISTS 'short_answers_questions'; CREATE TABLE IF NOT EXISTS short_answers_questions " +
					"(ID_QUESTION       INTEGER PRIMARY KEY AUTOINCREMENT," +
					" ID_GLOBAL      INT     NOT NULL, " +
					" SUBJECT           TEXT    NOT NULL, " +
					" LEVEL      INT     NOT NULL, " +
					" QUESTION           TEXT    NOT NULL, " +
					" ANSWER           TEXT    NOT NULL, " +
					" IMAGE_PATH           TEXT    NOT NULL) ";
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}
	// Adding new question to database
	public void addQuestion(Question quest) throws Exception {
		List<Question> tempQuestList  = new ArrayList<Question>();
		List<Integer> tempGlobalidList = new ArrayList<Integer>();
		tempQuestList = getAllQuestions();
		if (tempQuestList.size() > 0) {
			int i;
			for (i = 0; i < tempQuestList.size() && tempQuestList.get(i).getID() != quest.getID(); i++) {	}
			if (i >= tempQuestList.size()) {
				quest.setID(tempQuestList.get(tempQuestList.size() - 1).getID() + 1);
			} else {

			}
			for (i = 0; i < tempQuestList.size(); i++) {
				tempGlobalidList.add(tempQuestList.get(i).getGLOBALID());
			}
			quest.setGLOBALID(Collections.max(tempGlobalidList) + 1);
		}
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = 	"INSERT INTO question (SUBJECT,LEVEL,QUESTION,ANSWER," +
					"OPTIONA,OPTIONB,OPTIONC,OPTIOND,TRIAL1,TRIAL2,TRIAL3,TRIAL4,IMAGE_PATH,ID_GLOBAL) " +
					"VALUES ('" +
					quest.getSUBJECT() + "','" +
					quest.getLEVEL() + "','" +
					quest.getQUESTION() + "','" +
					quest.getANSWER() + "','" +
					quest.getOPTA() + "','" +
					quest.getOPTB() + "','" +
					quest.getOPTC() + "','" +
					quest.getOPTD() + "','" +
					quest.getTRIAL1() + "','" +
					quest.getTRIAL2() + "','" +
					quest.getTRIAL3() + "','" +
					quest.getTRIAL4() + "','" +
					quest.getIMAGE() + "','" +
					quest.getGLOBALID() + "');";
			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}
	// Adding new question to database
	public void addMultipleChoiceQuestion(QuestionMultipleChoice quest) throws Exception {
		Connection c = null;
		Statement stmt = null;
		stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = 	"INSERT INTO multiple_choice_questions (SUBJECT,LEVEL,QUESTION,ANSWER," +
					"OPTION1,OPTION2,OPTION3,OPTION4,OPTION5,OPTION6,OPTION7,OPTION8,OPTION9,TRIAL0,TRIAL1,TRIAL2,TRIAL3,TRIAL4,TRIAL5,TRIAL6,TRIAL7," +
					"TRIAL8,TRIAL9,IMAGE_PATH,ID_GLOBAL) " +
					"VALUES ('" +
					quest.getSUBJECT() + "'," +
					quest.getLEVEL() + ",'" +
					quest.getQUESTION() + "','" +
					quest.getANSWER() + "','" +
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
					quest.getIMAGE() + "','" +
					2000000 + "');";
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
	 * get a List of all the Question in the database
 	 */
	public List<Question> getAllQuestions() throws Exception{
		List<Question> quesList = new ArrayList<Question>();
		// Select All Query
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM QUESTION;" );
			while ( rs.next() ) {
				Question quest = new Question();
				quest.setID(rs.getInt(1));
				quest.setSUBJECT(rs.getString(2));
				quest.setLEVEL(rs.getString(3));
				quest.setQUESTION(rs.getString(4));
				quest.setANSWER(rs.getString(5));
				quest.setOPTA(rs.getString(6));
				quest.setOPTB(rs.getString(7));
				quest.setOPTC(rs.getString(8));
				quest.setOPTD(rs.getString(9));
				quest.setTRIAL1(rs.getString(10));
				quest.setTRIAL2(rs.getString(11));
				quest.setTRIAL3(rs.getString(12));
				quest.setTRIAL4(rs.getString(13));
				quest.setIMAGE(rs.getString(14));  //13, because the trials are between OPTD and IMAGE
				quest.setGLOBALID(rs.getInt(15));
				quesList.add(quest);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		System.out.println("Operation done successfully");
		return quesList;
	}
	/**
	 * get a List of all the QuestionMultipleChoice in the database
	 */
	public List<QuestionMultipleChoice> getAllMultipleChoiceQuestions() throws Exception{
		List<QuestionMultipleChoice> multquestList = new ArrayList<QuestionMultipleChoice>();
		// Select All Query
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM multiple_choice_questions;" );
			while ( rs.next() ) {
				QuestionMultipleChoice quest = new QuestionMultipleChoice();
				quest.setSUBJECT(rs.getString(2));
				quest.setLEVEL(rs.getString(3));
				quest.setQUESTION(rs.getString(4));
				quest.setANSWER(rs.getString(5));
				quest.setOPT1(rs.getString(6));
				quest.setOPT2(rs.getString(7));
				quest.setOPT3(rs.getString(8));
				quest.setOPT4(rs.getString(9));
				quest.setOPT5(rs.getString(10));
				quest.setOPT6(rs.getString(11));
				quest.setOPT7(rs.getString(12));
				quest.setOPT8(rs.getString(13));
				quest.setOPT9(rs.getString(14));
				quest.setTRIAL0(rs.getString(15));
				quest.setTRIAL1(rs.getString(16));
				quest.setTRIAL2(rs.getString(17));
				quest.setTRIAL3(rs.getString(18));
				quest.setTRIAL4(rs.getString(19));
				quest.setTRIAL5(rs.getString(20));
				quest.setTRIAL6(rs.getString(21));
				quest.setTRIAL7(rs.getString(22));
				quest.setTRIAL8(rs.getString(23));
				quest.setTRIAL9(rs.getString(24));
				quest.setID(rs.getInt(25));
				multquestList.add(quest);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		System.out.println("Operation done successfully");
		return multquestList;
	}
} 

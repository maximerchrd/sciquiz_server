package com.sciquizapp.sciquizserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
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
		System.out.println("Opened database successfully");
	}
	public void createQuestionsTableIfNotExists() throws Exception {
		// First create the table if it doesn't exist
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			String sql = " DROP TABLE question; CREATE TABLE IF NOT EXISTS question " +
					"(ID_QUESTION       INT PRIMARY KEY     NOT NULL," +
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
					" IMAGE_PATH           TEXT    NOT NULL) "; 
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		System.out.println("Table dropped and created successfully");

		// Then store the questions into the table
		Question q1=new Question("TP","1","Comment appelle-t-on l''instrument ci-dessous","burette","pipette","buchner", "compte-gouttes", "burette","res/drawable/burette");
		this.addQuestion(q1);
		System.out.println("Records created successfully");
	}
	// Adding new question to database
	public void addQuestion(Question quest) throws Exception {
		Connection c = null;
		Statement stmt = null;
		stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");
			stmt = c.createStatement();
			String sql = 	"INSERT INTO question (ID_QUESTION,SUBJECT,LEVEL,QUESTION,ANSWER," +
					"OPTIONA,OPTIONB,OPTIONC,OPTIOND,TRIAL1,TRIAL2,TRIAL3,TRIAL4,IMAGE_PATH) " +
					"VALUES (" + quest.getID() + ",'" +
					quest.getSUBJECT() + "'," + 
					quest.getLEVEL() + ",'" +
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
					quest.getIMAGE() + "');"; 
			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		System.out.println("QUestion added successfully");	
	}

	// get a list of all questions in database
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
} 

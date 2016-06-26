package com.sciquizapp.sciquizserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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
			    
			    stmt = null;
			    try {
			      Class.forName("org.sqlite.JDBC");
			      c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
			      c.setAutoCommit(false);
			      System.out.println("Opened database successfully");
			      stmt = c.createStatement();
	    		  String sql = 	"INSERT INTO question (ID_QUESTION,SUBJECT,LEVEL,QUESTION,ANSWER," +
	    				  		"OPTIONA,OPTIONB,OPTIONC,OPTIOND,IMAGE_PATH) " +
	    				  		"VALUES (" + 1 + ",'" +
	    				  		"chimie" + "'," + 1 + ",'" +
	    				  		"Comment appelle-t-on l''instrument ci-dessous" + "','" +
	    				  		"burette" + "','" +
	    				  		"burette" + "','" +
	    				  		"pipette" + "','" +
	    				  		"buchner" + "','" +
	    				  		"compte gouttes" + "','" +
	    				  		"res/drawable/burette" + "');"; 
	    		  stmt.executeUpdate(sql);
			      
			      /*for (int i=0; i<sco.getMaxLevels(); i++) {
			    	  if (sco.getLevels()[i] != null) {
			    		  LevelInfo lvl = sco.getLevels()[i];
			    		  stmt = c.createStatement();
			    		  String sql = 	"INSERT INTO LEVEL (ID_LEVEL,NAME,NUM_BALLS,MIN_SPEED_BALL,MAX_SPEED_BALL," +
			    				  		"NUM_SELECTED,START_DELAY,RUN_DELAY) " +
			    				  		"VALUES (" + Integer.toString(i+1) + ",'" +
			    				  		lvl.getLevelName() + "'," + Integer.toString(lvl.getNumBalls()) + "," +
			    				  		Integer.toString(lvl.getMinSpeedBall()) + "," +
			    				  		Integer.toString(lvl.getMaxSpeedBall()) + "," +
			    				  		Integer.toString(lvl.getNumSelection()) + "," +
			    				  		Integer.toString(lvl.getInitialDelay()) + "," +
			    				  		Integer.toString(lvl.getRunnningDelay()) + ");"; 
			    		  stmt.executeUpdate(sql);
			    	  }
			      }*/
			      stmt.close();
			      c.commit();
			      c.close();
			    } catch ( Exception e ) {
			      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			      System.exit(0);
			    }
			    System.out.println("Records created successfully");
	}

} 

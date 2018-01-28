package com.sciquizapp.sciquizserver.questions;

import java.util.ArrayList;

public class QuestionShortAnswer {
	private int ID;
	private String SUBJECT;
	private String LEVEL;
	private String QUESTION;
	private ArrayList<String> ANSWERS;
	private String IMAGE;
	public QuestionShortAnswer()
	{
		ID=0;
		SUBJECT="";
		LEVEL="";
		QUESTION="";
		ANSWERS = null;
		IMAGE="none";
	}
	public QuestionShortAnswer(String sUBJECT, String lEVEL, String qUESTION, String iMAGE) {
		
		SUBJECT = sUBJECT;
		LEVEL = lEVEL;
		QUESTION = qUESTION;
		IMAGE = iMAGE;
	}
	public int getID()
	{
		return ID;
	}
	public String getSUBJECT() {
		return SUBJECT;
	}
	public String getLEVEL() {
		return LEVEL;
	}
	public String getQUESTION() {
		return QUESTION;
	}
	public ArrayList<String> getANSWER() {
		return ANSWERS;
	}
	public String getIMAGE() {
		return IMAGE;
	}
	public void setID(int id)
	{
		ID=id;
	}
	public void setSUBJECT(String sUBJECT) {
		SUBJECT = sUBJECT;
	}
	public void setLEVEL(String lEVEL) {
		LEVEL = lEVEL;
	}
	public void setQUESTION(String qUESTION) {
		QUESTION = qUESTION;
	}
	public void setANSWER(ArrayList<String> aNSWERS) {
		ANSWERS = aNSWERS;
	}
	public void setIMAGE(String iMAGE) {
		IMAGE = iMAGE;
	}
	
}

package com.sciquizapp.sciquizserver.questions;

public class QuestionShortAnswer {
	private int ID;
	private String SUBJECT;
	private String LEVEL;
	private String QUESTION;
	private String ANSWER;
	private String IMAGE;
	public QuestionShortAnswer()
	{
		ID=0;
		SUBJECT="";
		LEVEL="";
		QUESTION="";
		ANSWER="";
		IMAGE="none";
	}
	public QuestionShortAnswer(String sUBJECT, String lEVEL, String qUESTION, String aNSWER, String iMAGE) {
		
		SUBJECT = sUBJECT;
		LEVEL = lEVEL;
		QUESTION = qUESTION;
		ANSWER = aNSWER;
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
	public String getANSWER() {
		return ANSWER;
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
	public void setANSWER(String aNSWER) {
		ANSWER = aNSWER;
	}
	public void setIMAGE(String iMAGE) {
		IMAGE = iMAGE;
	}
	
}

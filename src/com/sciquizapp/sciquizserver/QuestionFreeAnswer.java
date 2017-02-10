package com.sciquizapp.sciquizserver;

public class QuestionFreeAnswer {
	private int ID;
	private String SUBJECT;
	private String LEVEL;
	private String QUESTION;
	private String IMAGE;
	public QuestionFreeAnswer()
	{
		ID=0;
		SUBJECT="";
		LEVEL="";
		QUESTION="";
		IMAGE="none";
	}
	public QuestionFreeAnswer(String sUBJECT, String lEVEL, String qUESTION, String iMAGE) {
		
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
	public void setIMAGE(String iMAGE) {
		IMAGE = iMAGE;
	}
	
}

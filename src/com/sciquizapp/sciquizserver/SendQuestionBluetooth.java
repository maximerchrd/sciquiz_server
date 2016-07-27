package com.sciquizapp.sciquizserver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.bluetooth.*;
import javax.microedition.io.*;

/**
 * Class that implements an SPP Server which accepts single line of
 * message from an SPP client and sends a single line of response to the client.
 */

public class SendQuestionBluetooth {
	private UUID uuid = null;
	private String connectionString;
	private StreamConnection connection = null;
	private StreamConnectionNotifier streamConnNotifier = null;
	private OutputStream outStream = null;
	private FileInputStream fis = null;
	private BufferedInputStream bis = null;

	//start server
	public void startServer() throws IOException {

		//Create a UUID for SPP
		uuid = new UUID("1101", true);
		//Create the servicve url
		connectionString = "btspp://localhost:" + uuid +";name=Sample SPP Server";

		//open server url
		streamConnNotifier = (StreamConnectionNotifier)Connector.open( connectionString );

		//Wait for client connection
		System.out.println("\nServer Started. Waiting for clients to connect...");
		connection = streamConnNotifier.acceptAndOpen();

		RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
		System.out.println("Remote device address: "+dev.getBluetoothAddress());
		System.out.println("Remote device name: "+dev.getFriendlyName(true));

		//read string from spp client
		InputStream inStream=connection.openInputStream();
		BufferedReader bReader=new BufferedReader(new InputStreamReader(inStream));
		String lineRead=bReader.readLine();
		System.out.println(lineRead);

		//open outputstream
		outStream = connection.openOutputStream();

		//send response to spp client
		/*outStream = connection.openOutputStream();
		PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));
		pWriter.write("un truc rigolo Response String from SPP Server\r\n");
		pWriter.flush();*/


		//streamConnNotifier.close();		//closed because connection problems occured suddenly 
	}
	public void SendQuestion(Question arg_quest) throws IOException {
		//send question to spp client
		if (connection != null) {
			
			//make string and bytearray from question and answers
			String question_text = arg_quest.getQUESTION() + "///";
			question_text += arg_quest.getOPTA() + "///";
			question_text += arg_quest.getOPTB() + "///";
			question_text += arg_quest.getOPTC() + "///";
			question_text += arg_quest.getOPTD() + "///";
			question_text += arg_quest.getIMAGE().split("/")[2];
			byte [] bytearraytext = question_text.getBytes(Charset.forName("UTF-8"));
			
			// send file : the sizes of the text and of the file are given in the first 20 bytes
			
			//writing of the first 20 bytes
			File myFile = new File (arg_quest.getIMAGE());
			int intfileLength = (int)myFile.length();
			int textbyteslength = bytearraytext.length;
			byte [] bytearray  = new byte [20 + textbyteslength + intfileLength];
			String fileLength = String.valueOf((int)myFile.length());
			fileLength += ":" + String.valueOf(textbyteslength);
			byte [] bytearraystring = fileLength.getBytes(Charset.forName("UTF-8"));
			for (int i = 0; i < bytearraystring.length; i++) {
				bytearray[i] = bytearraystring[i];
			}
			
			//copy the textbytes into the array which will be sent
			for (int i = 0; i < bytearraytext.length; i++) {
				bytearray[i+20] = bytearraytext[i];
			}
			
			//write the file into the bytearray   !!! tested up to 630000 bytes, does not work with file of 4,7MB
			fis = new FileInputStream(myFile);
			bis = new BufferedInputStream(fis);
			int arraylength = bytearray.length;
			bis.read(bytearray,20 + textbyteslength,intfileLength);
			//os = sock.getOutputStream();
			System.out.println("Sending " + arg_quest.getIMAGE() + "(" + (int)myFile.length() + " bytes)");
			System.out.println("Sending " + arraylength + " bytes in total");
			outStream.write(bytearray,0,arraylength);
			outStream.flush();
			System.out.println("Done.");

		} else {
			System.out.println("StreamConnection variable is null. No device connected in mode intéractif. \n");
		}


	}
}
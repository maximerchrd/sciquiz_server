package com.sciquizapp.sciquizserver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.bluetooth.*;
import javax.microedition.io.*;

/**
 * com.sciquizapp.sciquizserver.Classroom that implements an SPP Server which accepts single line of
 * message from an SPP client and sends a single line of response to the client.
 */

public class SendQuestionBluetooth {
	private UUID uuid = null;
	private String connectionString;
	private StreamConnection connection = null;
	private StreamConnectionNotifier streamConnNotifier = null;
	private FileInputStream fis = null;
	private BufferedInputStream bis = null;
	private OutputStream outStream = null;
	private ArrayList<OutputStream> outstream_list;
	private InputStream inStream = null;
	private ArrayList<InputStream> instream_list;
	private static final int MAX_NUMBER_OF_CLIENTS = 1;
	private int number_of_clients = 0;
	//temporary ArrayList of strings containing Client's mac addresse
	private ArrayList<String> mClientsAddresses;

	//start server
	public void startServer() throws IOException {

		//Create a UUID for SPP
		uuid = new UUID("1101", true);

		//Create the servicve url
		connectionString = "btspp://localhost:" + uuid +";name=Sample SPP Server";

		//open server url
		streamConnNotifier = (StreamConnectionNotifier)Connector.open( connectionString );

//		//Wait for client connection
//		System.out.println("\nServer Started. Waiting for clients to connect...");
//		outstream_list = new ArrayList<OutputStream>();
//		instream_list = new ArrayList<InputStream>();
//		mClientsAddresses = new ArrayList<String>();
//
//		Thread connectionthread = new Thread() {
//			public void run() {
//				while (true) {
//					try {
//						System.out.println("waiting for next client to connect");
//						connection = streamConnNotifier.acceptAndOpen();
//
//						RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
//						System.out.println("Remote device address: "+dev.getBluetoothAddress());
//						System.out.println("Remote device name: "+dev.getFriendlyName(true));
//
//						//open outputstream and instream
//						outStream = connection.openOutputStream();
//						inStream = connection.openInputStream();
//						SendNewConnectionResponse(outStream);
////						if (number_of_clients <= MAX_NUMBER_OF_CLIENTS) outstream_list.add(outStream);
//
//						//read string from spp client
//						BufferedReader bReader=new BufferedReader(new InputStreamReader(inStream));
//						String lineRead=bReader.readLine();
//						System.out.println(lineRead);
////						if (number_of_clients <= MAX_NUMBER_OF_CLIENTS) instream_list.add(inStream);
//						mClientsAddresses.add(lineRead);
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//				}
//			}
//		};
//		connectionthread.start();

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
			for (int i = 0; i < outstream_list.size(); i++) {
				outstream_list.get(i).write(bytearray,0,arraylength);
				outstream_list.get(i).flush();
			}
			System.out.println("Done.");
		} else {
			System.out.println("StreamConnection variable is null. No device connected in mode int�ractif. \n");
		}
		listenForAnswer();
	}
	private void listenForAnswer() throws IOException {
		for (int i = 0; i < instream_list.size(); i++) {
			final InputStream answerInStream = instream_list.get(i);
			Thread listeningthread = new Thread() {
				public void run() {
					try {
						byte [] in_bytearray  = new byte[1000];
						int bytesread = answerInStream.read(in_bytearray);
						if (bytesread >= 1000) System.out.println("Answer too large for bytearray");
						if (bytesread >= 0) {
							String answerString = new String(in_bytearray, 0, bytesread, "UTF-8");
							System.out.println(answerString);
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			};
			listeningthread.start();
		}
	}
	
	private void SendNewConnectionResponse(OutputStream outStreamToNewClient) throws IOException {
		String response = "A PROBLEM OCCURED";
		if (number_of_clients < MAX_NUMBER_OF_CLIENTS) {
			response = "SERVER///OK///" + String.valueOf(MAX_NUMBER_OF_CLIENTS) + "///";
			number_of_clients++;
			outstream_list.add(outStream);
			instream_list.add(inStream);
		} else if (number_of_clients > 0) {
			response = "SERVER///" + mClientsAddresses.get(0) + "///" + String.valueOf(MAX_NUMBER_OF_CLIENTS) + "///";
		}
			
		byte[] bytes = new byte[40];
		int bytes_length = response.getBytes("UTF-8").length;
		for (int i = 0; i < bytes_length; i++) {
			bytes[i] = response.getBytes("UTF-8")[i];
		}
		outStreamToNewClient.write(bytes, 0, bytes.length);
		outStreamToNewClient.flush();
	}
}
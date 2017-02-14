package com.sciquizapp.sciquizserver;

import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.bluetooth.*;


/**
 * Created by maximerichard on 03/02/17.
 *
 */
public class NetworkCommunication {
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
		LocalDevice local = null;

		try {
			local = LocalDevice.getLocalDevice();
			local.setDiscoverable(DiscoveryAgent.GIAC);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		//Create a UUID for SPP
		//uuid = new UUID("1101", true);
		uuid = new UUID("d0c722b07e1511e1b0c40800200c9a66", false);

		//Create the servicve url
		connectionString = "btspp://localhost:" + uuid.toString() +";name=Sample SPP Server";

		//open server url
		streamConnNotifier = (StreamConnectionNotifier) Connector.open( connectionString );

		//Wait for client connection
		System.out.println("\nServer Started. Waiting for clients to connect...");
		outstream_list = new ArrayList<OutputStream>();
		instream_list = new ArrayList<InputStream>();
		mClientsAddresses = new ArrayList<String>();

		while (true) {
			try {
				System.out.println("waiting for next client to connect");
				connection = streamConnNotifier.acceptAndOpen();

				Thread connectionthread = new Thread() {
					public void run() {
						try {
							RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
							System.out.println("Remote device address: "+dev.getBluetoothAddress());
							//System.out.println("Remote device name: "+dev.getFriendlyName(true));

							//open outputstream and instream
							outStream = connection.openOutputStream();
                            outstream_list.add(outStream);

							inStream = connection.openInputStream();
                            instream_list.add(inStream);
							//SendNewConnectionResponse(outStream);
//						if (number_of_clients <= MAX_NUMBER_OF_CLIENTS) outstream_list.add(outStream);

							//read string from spp client
							BufferedReader bReader=new BufferedReader(new InputStreamReader(inStream));
							String lineRead = "empty";
							while (true && lineRead != null) {
								lineRead=bReader.readLine();
								System.out.println(lineRead);
								mClientsAddresses.add(lineRead);
							}

//						if (number_of_clients <= MAX_NUMBER_OF_CLIENTS) instream_list.add(inStream);

						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				};
				connectionthread.start();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}



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

			// send file : the sizes of the file and of the text are given in the first 20 bytes (separated by ":")

			//writing of the first 20 bytes
			File myFile = new File (arg_quest.getIMAGE());
			int intfileLength = (int)myFile.length();
			int textbyteslength = bytearraytext.length;
			byte [] bytearray  = new byte [20 + textbyteslength + intfileLength];
			String fileLength = "QUEST";
			fileLength += ":" + String.valueOf((int)myFile.length());
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
//			String array_string = "";
//			for (int i = 0; i < bytearray.length; i++) {
//				array_string += bytearray[i];
//			}
//			System.out.print(array_string);
		} else {
			System.out.println("StreamConnection variable is null. No device connected in mode int�ractif. \n");
		}
		//listenForAnswer();
	}

}

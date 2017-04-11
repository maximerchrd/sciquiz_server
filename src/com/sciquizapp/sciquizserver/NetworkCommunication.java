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
 */
public class NetworkCommunication {
    private Table mTableQuestionVsUser = null;
    private UUID uuid = null;
    private String connectionString;
    private StreamConnection connection = null;
    private StreamConnectionNotifier streamConnNotifier = null;
    private FileInputStream fis = null;
    private BufferedInputStream bis = null;
    private OutputStream serverOutStream = null;
    private ArrayList<OutputStream> outstream_list;
    private InputStream inStream = null;
    private ArrayList<InputStream> instream_list;
    private static final int MAX_NUMBER_OF_CLIENTS = 1;
    private int number_of_clients = 0;
    private ArrayList<Student> students_array;
    private Classroom aClass = null;
    //temporary ArrayList of strings containing Client's mac addresse
    private ArrayList<String> mClientsAddresses;
    private DiscoveryAgent discoveryAgent = null;
    private LocalDevice localDevice = null;
    private StreamConnection streamConnection = null;
    private static Object lock = new Object();    //object used for waiting

    public NetworkCommunication(Table TableQuestionVsUser) {
        mTableQuestionVsUser = TableQuestionVsUser;
    }

    public NetworkCommunication() {
    }

    /**
     * starts the bluetooth server
     *
     * @throws IOException
     */
    public void startServer() throws IOException {
        localDevice = null;

        try {
            localDevice = LocalDevice.getLocalDevice();
            localDevice.setDiscoverable(DiscoveryAgent.GIAC);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        //Create a UUID for SPP
        //uuid = new UUID("1101", true);
        uuid = new UUID("d0c722b07e1511e1b0c40800200c9a66", false);
        //uuid = new UUID("0000110100001000800000805f9b34fb", false);

        //Create the servicve url
        connectionString = "btspp://localhost:" + uuid.toString() + ";name=Sample SPP Server";

        //open server url
        streamConnNotifier = (StreamConnectionNotifier) Connector.open(connectionString);

        //Wait for client connection
        System.out.println("\nServer Started. Waiting for clients to connect...");
        outstream_list = new ArrayList<OutputStream>();
        instream_list = new ArrayList<InputStream>();
        mClientsAddresses = new ArrayList<String>();
        //students_array = new ArrayList<Student>();
        aClass = new Classroom();
        Thread connectionthread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        System.out.println("waiting for next client to connect");
                        Student student = new Student();
                        student.setConnection(streamConnNotifier.acceptAndOpen());


                        try {
                            RemoteDevice remoteDevice = RemoteDevice.getRemoteDevice(student.getConnection());
                            student.setRemoteDevice(remoteDevice);
                            student.setAddress(remoteDevice.getBluetoothAddress());
                            if (!aClass.studentAlreadyInClass(student)) {
                                student.openInputStream();
                                aClass.addStudentIfNotInClass(student);
                                System.out.println("aClass.size() = " + aClass.getClassSize() + " adding student: " + student.getAddress());
                                //mTableQuestionVsUser.addUser(remoteDevice.getBluetoothAddress());
                            } else {
                                aClass.updateStudent(student);
                                listenForClient(aClass.getStudents_array().get(aClass.indexOfStudentWithAddress(student.getAddress())));
                            }

                            //open outputstream and instream
                            //aClass.getStudents_array().get(aClass.getClassSize() - 1).openStreams();
                            //serverOutStream = connection.openOutputStream();
                            //inStream = connection.openInputStream();


                            //SendNewConnectionResponse();
                            //while (true) {

                            //}


                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                    } catch (IOException e2) {
                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }

                }
            }
        };
        connectionthread.start();

        //send response to spp client
        /*serverOutStream = connection.openOutputStream();
        PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(serverOutStream));
		pWriter.write("un truc rigolo Response String from SPP Server\r\n");
		pWriter.flush();*/


        //streamConnNotifier.close();		//closed because connection problems occured suddenly
    }

    public void SendQuestion(Question arg_quest) throws IOException {
        //send question to spp client

        //add a row in the table for the new question and answers

        //mTableQuestionVsUser.addQuestion(arg_quest.getQUESTION());
        //make string and bytearray from question and answers
        String question_text = arg_quest.getQUESTION() + "///";
        question_text += arg_quest.getOPTA() + "///";
        question_text += arg_quest.getOPTB() + "///";
        question_text += arg_quest.getOPTC() + "///";
        question_text += arg_quest.getOPTD() + "///";
        question_text += arg_quest.getIMAGE().split("/")[2];
        byte[] bytearraytext = question_text.getBytes(Charset.forName("UTF-8"));

        // send file : the sizes of the file and of the text are given in the first 20 bytes (separated by ":")

        //writing of the first 20 bytes
        File myFile = new File(arg_quest.getIMAGE());
        int intfileLength = (int) myFile.length();
        int textbyteslength = bytearraytext.length;
        byte[] bytearray = new byte[20 + textbyteslength + intfileLength];
        String fileLength = "QUEST";
        fileLength += ":" + String.valueOf((int) myFile.length());
        fileLength += ":" + String.valueOf(textbyteslength);
        byte[] bytearraystring = fileLength.getBytes(Charset.forName("UTF-8"));
        for (int i = 0; i < bytearraystring.length; i++) {
            bytearray[i] = bytearraystring[i];
        }

        //copy the textbytes into the array which will be sent
        for (int i = 0; i < bytearraytext.length; i++) {
            bytearray[i + 20] = bytearraytext[i];
        }

        //write the file into the bytearray   !!! tested up to 630000 bytes, does not work with file of 4,7MB
        fis = new FileInputStream(myFile);
        bis = new BufferedInputStream(fis);
        int arraylength = bytearray.length;
        bis.read(bytearray, 20 + textbyteslength, intfileLength);
        //os = sock.getOutputStream();
        System.out.println("Sending " + arg_quest.getIMAGE() + "(" + (int) myFile.length() + " bytes)");
        System.out.println("Sending " + arraylength + " bytes in total");
//			for (int i = 0; i < outstream_list.size(); i++) {
//				outstream_list.get(i).write(bytearray,0,arraylength);
//				outstream_list.get(i).flush();
//			}

        Thread sendingthread = new Thread() {
            public void run() {
                for (int i = 0; i < aClass.getClassSize(); i++) {
//                    try {
//                        aClass.getStudents_array().get(i).getOutputStream().write(bytearray, 0, arraylength);
//                        aClass.getStudents_array().get(i).getOutputStream().flush();
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
                        connectToSmartphone(aClass.getStudents_array().get(i).getRemoteDevice());
                        try {
                            serverOutStream.write(bytearray, 0, arraylength);
                            Thread.sleep(1000);     //wait to be sure that the smartphone has enough time to read
                            serverOutStream.flush();
                            serverOutStream.close();
                            serverOutStream = null;
                            streamConnection.close();
                            streamConnection = null;
                        } catch (IOException ex2) {
                            ex2.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                    }
                /*try {
					synchronized(lock){
						lock.wait();
					}
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}*/
                }
            }
        };
        sendingthread.start();
        System.out.println("Done.");
        //} else {
        //	System.out.println("StreamConnection variable is null. No device connected in mode int�ractif. \n");
        //}
    }

    /**
     * method that listen for the client data transfers
     *
     * @throws IOException
     */
    private void listenForClient(final Student arg_student) throws IOException {
        //for (int i = 0; i < aClass.getClassSize(); i++) {
        final InputStream answerInStream = arg_student.getInputStream();
        //Thread listeningthread = new Thread() {
        //	public void run() {
        //while(true) {
        try {
            byte[] in_bytearray = new byte[1000];
            //System.out.println("listening to " + aClass.getStudents_array().get(j).getAddress());
            int bytesread = answerInStream.read(in_bytearray);
            System.out.println(bytesread + " bytes read");
            if (bytesread >= 1000) System.out.println("Answer too large for bytearray");
            if (bytesread >= 0) {
                String answerString = new String(in_bytearray, 0, bytesread, "UTF-8");
                System.out.println(answerString);
                if (answerString.split("///")[0].contains("ANSW")) {
                    //Student student = new Student(answerString.split("///")[1], answerString.split("///")[2]);
                    arg_student.setName(answerString.split("///")[2]);
                    //Student student = aClass.getStudents_array().get(j);
                    //mTableQuestionVsUser.addAnswerForUser(student, answerString.split("///")[3]);

                }
            } else {

            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        //}
        //	}
        //};
        //listeningthread.start();
        //}
    }

    private void SendNewConnectionResponse() throws IOException {
        String response = "A PROBLEM OCCURED";
        if (number_of_clients < MAX_NUMBER_OF_CLIENTS) {
            response = "SERVR///OK///" + String.valueOf(MAX_NUMBER_OF_CLIENTS) + "///";
            number_of_clients++;
            //outstream_list.add(serverOutStream);
            //instream_list.add(inStream);
        } else {
            response = "SERVR///MAX///";
        }

        byte[] bytes = new byte[20];
        int bytes_length = response.getBytes("UTF-8").length;
        for (int i = 0; i < bytes_length; i++) {
            bytes[i] = response.getBytes("UTF-8")[i];
        }
        aClass.getStudents_array().get(aClass.getClassSize() - 1).getOutputStream().write(bytes, 0, bytes.length);
        aClass.getStudents_array().get(aClass.getClassSize() - 1).getOutputStream().flush();
        //serverOutStream.write(bytes, 0, bytes.length);
        //serverOutStream.flush();
    }

    private Boolean connectToSmartphone(RemoteDevice clientDevice) {
        System.out.print("connectToSmartphone");
        UUID[] uuidSet = new UUID[1];
        uuidSet[0] = new UUID("4c98e69fb27e415ab3e4769cf2baf51e", false);
        try {
            streamConnection = (StreamConnection) Connector.open("btspp://" + clientDevice.getBluetoothAddress() + ":5;authenticate=false;encrypt=false;master=false");
            serverOutStream = streamConnection.openOutputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}

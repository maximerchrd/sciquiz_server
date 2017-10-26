package com.sciquizapp.sciquizserver;

import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private static final int MAX_NUMBER_OF_CLIENTS = 40;
    private int number_of_clients = 0;
    private ArrayList<Student> students_array;
    private Classroom aClass = null;
    //temporary ArrayList of strings containing Client's mac addresse
    private ArrayList<String> mClientsAddresses;
    private DiscoveryAgent discoveryAgent = null;
    private LocalDevice localDevice = null;
    private StreamConnection streamConnection = null;
    private static Object lock = new Object();    //object used for waiting
    private int network_solution = 0; //0: all devices connected to same wifi router


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

        if (network_solution == 0) {
            // First we create a server socket and bind it to port 9090.
            ServerSocket myServerSocket = new ServerSocket(9090);


            // wait for an incoming connection...
            System.out.println("Server is waiting for an incoming connection on host="
                    + InetAddress.getLocalHost().getHostAddress() + "; "
                    + InetAddress.getLocalHost().getCanonicalHostName() + "; "
                    + InetAddress.getLocalHost().getHostName()
                    + " port=" + myServerSocket.getLocalPort());


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
                            Socket skt = myServerSocket.accept();
                            System.out.println("waiting for next client to connect");
                            Student student = new Student();
                            student.setInetAddress(skt.getInetAddress());

                            if (number_of_clients < MAX_NUMBER_OF_CLIENTS) {
                                try {
                                    number_of_clients++;
                                    if (!aClass.studentAlreadyInClass(student)) {
                                        student.setInputStream(skt.getInputStream());
                                        student.setOutputStream(skt.getOutputStream());
                                        aClass.addStudentIfNotInClass(student);
                                        System.out.println("aClass.size() = " + aClass.getClassSize() + " adding student: " + student.getInetAddress().toString());
                                        mTableQuestionVsUser.addUser(student.getInetAddress().toString());
                                        SendNewConnectionResponse(student.getOutputStream(), false);
                                        listenForClient(aClass.getStudents_array().get(aClass.indexOfStudentWithAddress(student.getAddress())));
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
                            } else {
                                System.out.println("max clients reached");
                            }


                        } catch (IOException e2) {
                            // TODO Auto-generated catch block
                            e2.printStackTrace();
                        }

                    }
                }
            };
            connectionthread.start();
        }
    }

    public void SendQuestionID(int QuestID) throws IOException {
        String questIDString = "QUEID///" + String.valueOf(QuestID) + "///";
        byte[] bytearraystring = questIDString.getBytes(Charset.forName("UTF-8"));
        ArrayList<Student> StudentsArray = aClass.getStudents_array();
        System.out.println(StudentsArray.size());
        for (int i = 0; i < StudentsArray.size(); i++) {
            OutputStream tempOutputStream = StudentsArray.get(i).getOutputStream();
            try {
                tempOutputStream.write(bytearraystring, 0, bytearraystring.length);
                tempOutputStream.flush();
            } catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
    }
    public void SendQuestion(Question arg_quest, Boolean isQuiz) throws IOException {
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
        String fileLength;
        if (isQuiz) {
            fileLength = "QUIZZ";
        } else {
            fileLength = "QUEST";
        }
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

        //Thread sendingthread = new Thread() {
         //   public void run() {
                ArrayList<Student> StudentsArray = aClass.getStudents_array();
                System.out.println(StudentsArray.size());
                for (int i = 0; i < StudentsArray.size(); i++) {
//                    try {
//                        aClass.getStudents_array().get(i).getOutputStream().write(bytearray, 0, arraylength);
//                        aClass.getStudents_array().get(i).getOutputStream().flush();
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
                    OutputStream tempOutputStream = StudentsArray.get(i).getOutputStream();
                        try {
                            //for ( int j = 0; j < arraylength; j++) {
                                //tempOutputStream.write(bytearray[j]);
                                tempOutputStream.write(bytearray, 0, arraylength);
                            //}
                            //Thread.sleep(1000);     //wait to be sure that the smartphone has enough time to read
                            tempOutputStream.flush();
                        } catch (IOException ex2) {
                            ex2.printStackTrace();
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
        //    }
        //};
        //sendingthread.start();
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
        Thread listeningthread = new Thread() {
        	public void run() {
        while(true) {
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
                    //mTableQuestionVsUser.addAnswerForUser(arg_student, answerString.split("///")[3]);

                } else if (answerString.split("///")[0].contains("CONN")) {
                    Student student = new Student(answerString.split("///")[1], answerString.split("///")[2]);
                    if (!aClass.studentAlreadyInClass(student)) {
                        student.openStreams();
                        //aClass.addStudentIfNotInClass(student);
                        //System.out.println("aClass.size() = " + aClass.getClassSize() + " adding student: " + student.getAddress());
                        //mTableQuestionVsUser.addUser(arg_student.getAddress());
                    } else {
                        aClass.updateStudent(student);
                        //listenForClient(aClass.getStudents_array().get(aClass.indexOfStudentWithAddress(student.getAddress())));
                    }
                }
            } else {

            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        }
        	}
        };
        listeningthread.start();
        //}
    }

    private void SendNewConnectionResponse(OutputStream arg_outputStream, Boolean maximum) throws IOException {
        String response;
        if (maximum) {
            response = "SERVR///MAX///";
        } else {
            response = "SERVR///OK///";
        }
        byte[] bytes = new byte[20];
        int bytes_length = response.getBytes("UTF-8").length;
        for (int i = 0; i < bytes_length; i++) {
            bytes[i] = response.getBytes("UTF-8")[i];
        }
        arg_outputStream.write(bytes, 0, bytes.length);
        arg_outputStream.flush();
        //serverOutStream.write(bytes, 0, bytes.length);
        //serverOutStream.flush();
    }

}

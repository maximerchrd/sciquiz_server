package com.sciquizapp.sciquizserver;

import com.sciquizapp.sciquizserver.database_management.*;
import com.sciquizapp.sciquizserver.questions.Question;
import com.sciquizapp.sciquizserver.questions.QuestionMultipleChoice;
import com.sciquizapp.sciquizserver.questions.QuestionShortAnswer;

import javax.bluetooth.UUID;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.bluetooth.*;
import javax.swing.*;


/**
 * Created by maximerichard on 03/02/17.
 */
public class NetworkCommunication {
    static public NetworkCommunication networkCommunicationSingleton;
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
    private static final int MAX_NUMBER_OF_CLIENTS = 1000000;
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
    final private int PORTNUMBER = 9090;


    public NetworkCommunication(Table TableQuestionVsUser) {
        mTableQuestionVsUser = TableQuestionVsUser;
        networkCommunicationSingleton = this;
    }

    public NetworkCommunication() {
        networkCommunicationSingleton = this;
    }

    /**
     * starts the bluetooth server
     *
     * @throws IOException
     */
    public void startServer() throws IOException {

        if (network_solution == 0) {
            // First we create a server socket and bind it to port 9090.
            ServerSocket myServerSocket = new ServerSocket(PORTNUMBER);


            // wait for an incoming connection...
            /*System.out.println("Server is waiting for an incoming connection on host="
                    + InetAddress.getLocalHost().getHostAddress() + "; "
                    + InetAddress.getLocalHost().getCanonicalHostName() + "; "
                    + InetAddress.getLocalHost().getHostName()
                    + " port=" + myServerSocket.getLocalPort());*/


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
                                        //mTableQuestionVsUser.addUser(student.getInetAddress().toString());
                                        SendNewConnectionResponse(student.getOutputStream(), false);
                                        SendQuestionList(null, null, null);
                                        for (int i = 0; i < QuestionsBrowser.IDsFromBroadcastedQuestions.size(); i++) {
                                            try {
                                                sendMultipleChoiceWithID(Integer.parseInt(QuestionsBrowser.IDsFromBroadcastedQuestions.get(i)),student.getOutputStream());
                                                sendShortAnswerQuestionWithID(Integer.parseInt(QuestionsBrowser.IDsFromBroadcastedQuestions.get(i)),student.getOutputStream());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        listenForClient(aClass.getStudents_array().get(aClass.indexOfStudentWithAddress(student.getInetAddress().toString())));
                                    } else {
                                        student.setInputStream(skt.getInputStream());
                                        student.setOutputStream(skt.getOutputStream());
                                        aClass.updateStudent(student);
                                        listenForClient(aClass.getStudents_array().get(aClass.indexOfStudentWithAddress(student.getInetAddress().toString())));
                                        for (int i = 0; i < QuestionsBrowser.IDsFromBroadcastedQuestions.size(); i++) {
                                            try {
                                                sendMultipleChoiceWithID(Integer.parseInt(QuestionsBrowser.IDsFromBroadcastedQuestions.get(i)),student.getOutputStream());
                                                sendShortAnswerQuestionWithID(Integer.parseInt(QuestionsBrowser.IDsFromBroadcastedQuestions.get(i)),student.getOutputStream());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
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
        String questIDString = "QID:MLT///" + String.valueOf(QuestID) + "///";
        byte[] bytearraystring = questIDString.getBytes(Charset.forName("UTF-8"));
        ArrayList<Student> StudentsArray = aClass.getStudents_array();
        System.out.println(questIDString);
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


        mTableQuestionVsUser.addQuestion(arg_quest.getQUESTION());
        //make string and bytearray from question and answers
        String question_text = arg_quest.getQUESTION() + "///";
        question_text += arg_quest.getOPTA() + "///";
        question_text += arg_quest.getOPTB() + "///";
        question_text += arg_quest.getOPTC() + "///";
        question_text += arg_quest.getOPTD() + "///";
        question_text += arg_quest.getIMAGE().split("/")[2];
        byte[] bytearraytext = question_text.getBytes(Charset.forName("UTF-8"));

        // send file : the sizes of the file and of the text are given in the first 40 bytes (separated by ":")

        //writing of the first 40 bytes
        File myFile = new File(arg_quest.getIMAGE());
        int intfileLength = (int) myFile.length();
        int textbyteslength = bytearraytext.length;
        byte[] bytearray = new byte[40 + textbyteslength + intfileLength];
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
            bytearray[i + 40] = bytearraytext[i];
        }

        //write the file into the bytearray   !!! tested up to 630000 bytes, does not work with file of 4,7MB
        fis = new FileInputStream(myFile);
        bis = new BufferedInputStream(fis);
        int arraylength = bytearray.length;
        bis.read(bytearray, 40 + textbyteslength, intfileLength);
        System.out.println("Sending " + arg_quest.getIMAGE() + "(" + (int) myFile.length() + " bytes)");
        System.out.println("Sending " + arraylength + " bytes in total");
        ArrayList<Student> StudentsArray = aClass.getStudents_array();
        System.out.println(StudentsArray.size());
        for (int i = 0; i < StudentsArray.size(); i++) {
            OutputStream tempOutputStream = StudentsArray.get(i).getOutputStream();
            try {
                tempOutputStream.write(bytearray, 0, arraylength);
                tempOutputStream.flush();
            } catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
        System.out.println("Done.");
        //} else {
        //	System.out.println("StreamConnection variable is null. No device connected in mode int�ractif. \n");
        //}
    }

    /**
     * method that sends the questions listed on the right panel to the clients
     *
     * @throws IOException
     */
    public void SendQuestionList(List<Question> questionList, List<QuestionMultipleChoice> multipleChoiceQuestionList, ArrayList<String> question_IDs) throws IOException {
        if (questionList != null) {
            for (int j = 0; j < questionList.size(); j++) {
                String question_text = questionList.get(j).getQUESTION() + "///";
                question_text += questionList.get(j).getOPTA() + "///";
                question_text += questionList.get(j).getOPTB() + "///";
                question_text += questionList.get(j).getOPTC() + "///";
                question_text += questionList.get(j).getOPTD() + "///";
                if (!questionList.get(j).getIMAGE().equals("none")) {
                    question_text += questionList.get(j).getIMAGE().split("/")[2];
                }
                byte[] bytearraytext = question_text.getBytes(Charset.forName("UTF-8"));

                // send file : the sizes of the file and of the text are given in the first 40 bytes (separated by ":")

                //writing of the first 40 bytes
                File myFile = new File(questionList.get(j).getIMAGE());
                int intfileLength = (int) myFile.length();
                int textbyteslength = bytearraytext.length;
                byte[] bytearray = new byte[40 + textbyteslength + intfileLength];
                String fileLength;
                fileLength = "QUEST";
                fileLength += ":" + String.valueOf((int) myFile.length());
                fileLength += ":" + String.valueOf(textbyteslength);
                byte[] bytearraystring = fileLength.getBytes(Charset.forName("UTF-8"));
                for (int k = 0; k < bytearraystring.length; k++) {
                    bytearray[k] = bytearraystring[k];
                }

                //copy the textbytes into the array which will be sent
                for (int k = 0; k < bytearraytext.length; k++) {
                    bytearray[k + 40] = bytearraytext[k];
                }

                //write the file into the bytearray   !!! tested up to 630000 bytes, does not work with file of 4,7MB
                fis = new FileInputStream(myFile);
                bis = new BufferedInputStream(fis);
                int arraylength = bytearray.length;
                bis.read(bytearray, 40 + textbyteslength, intfileLength);
                System.out.println("Sending " + questionList.get(j).getIMAGE() + "(" + (int) myFile.length() + " bytes)");
                System.out.println("Sending " + arraylength + " bytes in total");
                for (int i = 0; i < aClass.getClassSize(); i++) {
                    OutputStream tempOutputStream = aClass.getStudents_array().get(i).getOutputStream();
                    try {
                        tempOutputStream.write(bytearray, 0, arraylength);
                        tempOutputStream.flush();
                    } catch (IOException ex2) {
                        ex2.printStackTrace();
                    }
                }
                System.out.println("Done.");
                mTableQuestionVsUser.addQuestion(questionList.get(j).getQUESTION());
            }
        }
        if (multipleChoiceQuestionList != null) {
            for (int j = 0; j < multipleChoiceQuestionList.size(); j++) {
                if (question_IDs.contains(String.valueOf(multipleChoiceQuestionList.get(j).getID()))) {
                    String question_text = multipleChoiceQuestionList.get(j).getQUESTION() + "///";
                    question_text += multipleChoiceQuestionList.get(j).getOPT0() + "///";
                    question_text += multipleChoiceQuestionList.get(j).getOPT1() + "///";
                    question_text += multipleChoiceQuestionList.get(j).getOPT2() + "///";
                    question_text += multipleChoiceQuestionList.get(j).getOPT3() + "///";
                    question_text += multipleChoiceQuestionList.get(j).getOPT4() + "///";
                    question_text += multipleChoiceQuestionList.get(j).getOPT5() + "///";
                    question_text += multipleChoiceQuestionList.get(j).getOPT6() + "///";
                    question_text += multipleChoiceQuestionList.get(j).getOPT7() + "///";
                    question_text += multipleChoiceQuestionList.get(j).getOPT8() + "///";
                    question_text += multipleChoiceQuestionList.get(j).getOPT9() + "///";
                    question_text += multipleChoiceQuestionList.get(j).getID() + "///";
                    Vector<String> subjectsVector = DbTableSubject.getSubjectsForQuestionID(multipleChoiceQuestionList.get(j).getID());
                    int l = 0;
                    for(l = 0; l < subjectsVector.size(); l++) {
                        question_text += subjectsVector.get(l) + "|||";
                    }
                    if (l == 0) question_text += " ";
                    question_text += "///";
                    Vector<String> objectivesVector = DbTableLearningObjectives.getObjectiveForQuestionID(multipleChoiceQuestionList.get(j).getID());
                    for(l = 0; l < objectivesVector.size(); l++) {
                        question_text += objectivesVector.get(l) + "|||";
                    }
                    if (l == 0) question_text += " ";
                    question_text += "///";
                    System.out.println(question_text);

                    // send file : the sizes of the file and of the text are given in the first 40 bytes (separated by ":")
                    int intfileLength = 0;
                    File myFile = null;
                    if (!multipleChoiceQuestionList.get(j).getIMAGE().equals("none")) {
                        question_text += multipleChoiceQuestionList.get(j).getIMAGE().split("/")[2];
                        myFile = new File(multipleChoiceQuestionList.get(j).getIMAGE());
                        intfileLength = (int) myFile.length();
                    } else {
                        question_text += multipleChoiceQuestionList.get(j).getIMAGE() + "///";
                    }

                    //writing of the first 40 bytes
                    byte[] bytearraytext = question_text.getBytes(Charset.forName("UTF-8"));
                    int textbyteslength = bytearraytext.length;
                    byte[] bytearray = new byte[40 + textbyteslength + intfileLength];
                    String fileLength;
                    fileLength = "MULTQ";
                    fileLength += ":" + String.valueOf(intfileLength);
                    fileLength += ":" + String.valueOf(textbyteslength);
                    byte[] bytearraystring = fileLength.getBytes(Charset.forName("UTF-8"));
                    for (int k = 0; k < bytearraystring.length; k++) {
                        bytearray[k] = bytearraystring[k];
                    }

                    //copy the textbytes into the array which will be sent
                    for (int k = 0; k < bytearraytext.length; k++) {
                        bytearray[k + 40] = bytearraytext[k];
                    }

                    //write the file into the bytearray   !!! tested up to 630000 bytes, does not work with file of 4,7MB
                    if (!multipleChoiceQuestionList.get(j).getIMAGE().equals("none")) {
                        fis = new FileInputStream(myFile);
                        bis = new BufferedInputStream(fis);
                        bis.read(bytearray, 40 + textbyteslength, intfileLength);
                    }
                    System.out.println("Sending " + multipleChoiceQuestionList.get(j).getIMAGE() + "(" + intfileLength + " bytes)");
                    int arraylength = bytearray.length;
                    System.out.println("Sending " + arraylength + " bytes in total");
                    for (int i = 0; i < aClass.getClassSize(); i++) {
                        OutputStream tempOutputStream = aClass.getStudents_array().get(i).getOutputStream();
                        try {
                            tempOutputStream.write(bytearray, 0, arraylength);
                            tempOutputStream.flush();
                        } catch (IOException ex2) {
                            ex2.printStackTrace();
                        }
                    }
                    System.out.println("Done.");
                }
            }
        }
    }

    public void sendMultipleChoiceWithID (int questionID, OutputStream singleStudentOutputStream) throws IOException {
        QuestionMultipleChoice questionMultipleChoice= null;
        try {
            questionMultipleChoice = DbTableQuestionMultipleChoice.getMultipleChoiceQuestionWithID(questionID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (questionMultipleChoice.getQUESTION().length() > 0) {
            String question_text = questionMultipleChoice.getQUESTION() + "///";
            question_text += questionMultipleChoice.getOPT0() + "///";
            question_text += questionMultipleChoice.getOPT1() + "///";
            question_text += questionMultipleChoice.getOPT2() + "///";
            question_text += questionMultipleChoice.getOPT3() + "///";
            question_text += questionMultipleChoice.getOPT4() + "///";
            question_text += questionMultipleChoice.getOPT5() + "///";
            question_text += questionMultipleChoice.getOPT6() + "///";
            question_text += questionMultipleChoice.getOPT7() + "///";
            question_text += questionMultipleChoice.getOPT8() + "///";
            question_text += questionMultipleChoice.getOPT9() + "///";
            question_text += questionMultipleChoice.getID() + "///";
            question_text += questionMultipleChoice.getNB_CORRECT_ANS() + "///";
            Vector<String> subjectsVector = DbTableSubject.getSubjectsForQuestionID(questionID);
            int l = 0;
            for(l = 0; l < subjectsVector.size(); l++) {
                question_text += subjectsVector.get(l) + "|||";
            }
            if (l == 0) question_text += " ";
            question_text += "///";
            Vector<String> objectivesVector = DbTableLearningObjectives.getObjectiveForQuestionID(questionMultipleChoice.getID());
            for(l = 0; l < objectivesVector.size(); l++) {
                question_text += objectivesVector.get(l) + "|||";
            }
            if (l == 0) question_text += " ";
            question_text += "///";
            System.out.println(question_text);

            // send file : the sizes of the file and of the text are given in the first 40 bytes (separated by ":")
            int intfileLength = 0;
            File myFile = null;
            if (!questionMultipleChoice.getIMAGE().equals("none")) {
                question_text += questionMultipleChoice.getIMAGE().split("/")[2];
                myFile = new File(questionMultipleChoice.getIMAGE());
                intfileLength = (int) myFile.length();
            } else {
                question_text += questionMultipleChoice.getIMAGE() + "///";
            }

            //writing of the first 40 bytes
            byte[] bytearraytext = question_text.getBytes(Charset.forName("UTF-8"));
            int textbyteslength = bytearraytext.length;
            byte[] bytearray = new byte[40 + textbyteslength + intfileLength];
            String fileLength;
            fileLength = "MULTQ";
            fileLength += ":" + String.valueOf(intfileLength);
            fileLength += ":" + String.valueOf(textbyteslength);
            byte[] bytearraystring = fileLength.getBytes(Charset.forName("UTF-8"));
            for (int k = 0; k < bytearraystring.length; k++) {
                bytearray[k] = bytearraystring[k];
            }

            //copy the textbytes into the array which will be sent
            for (int k = 0; k < bytearraytext.length; k++) {
                bytearray[k + 40] = bytearraytext[k];
            }

            //write the file into the bytearray   !!! tested up to 630000 bytes, does not work with file of 4,7MB
            if (!questionMultipleChoice.getIMAGE().equals("none")) {
                fis = new FileInputStream(myFile);
                bis = new BufferedInputStream(fis);
                bis.read(bytearray, 40 + textbyteslength, intfileLength);
            }
            System.out.println("Sending " + questionMultipleChoice.getIMAGE() + "(" + intfileLength + " bytes)");
            int arraylength = bytearray.length;
            System.out.println("Sending " + arraylength + " bytes in total");
            if (singleStudentOutputStream == null) {
                for (int i = 0; i < aClass.getClassSize(); i++) {
                    OutputStream tempOutputStream = aClass.getStudents_array().get(i).getOutputStream();
                    try {
                        tempOutputStream.write(bytearray, 0, arraylength);
                        tempOutputStream.flush();
                    } catch (IOException ex2) {
                        ex2.printStackTrace();
                    }
                }
            } else {
                try {
                    singleStudentOutputStream.write(bytearray, 0, arraylength);
                    singleStudentOutputStream.flush();
                } catch (IOException ex2) {
                    ex2.printStackTrace();
                }
            }

            System.out.println("Done.");
        }
    }

    public void sendShortAnswerQuestionWithID (int questionID, OutputStream singleStudentOutputStream) throws IOException {
        QuestionShortAnswer questionShortAnswer = null;
        try {
            questionShortAnswer = DbTableQuestionShortAnswer.getShortAnswerQuestionWithId(questionID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (questionShortAnswer.getQUESTION().length() > 0) {
            String question_text = questionShortAnswer.getQUESTION() + "///";
            question_text += questionShortAnswer.getID() + "///";

            //add answers
            ArrayList<String> answersArray = questionShortAnswer.getANSWER();
            for (int i = 0; i < answersArray.size(); i++) {
                question_text += answersArray.get(i) + "|||";
            }
            if (answersArray.size() == 0) question_text += " ";
            question_text += "///";

            //add subjects
            Vector<String> subjectsVector = DbTableSubject.getSubjectsForQuestionID(questionID);
            int l = 0;
            for(l = 0; l < subjectsVector.size(); l++) {
                question_text += subjectsVector.get(l) + "|||";
            }
            if (l == 0) question_text += " ";
            question_text += "///";

            //add objectives
            Vector<String> objectivesVector = DbTableLearningObjectives.getObjectiveForQuestionID(questionShortAnswer.getID());
            for(l = 0; l < objectivesVector.size(); l++) {
                question_text += objectivesVector.get(l) + "|||";
            }
            if (l == 0) question_text += " ";
            question_text += "///";
            System.out.println(question_text);

            // send file : the sizes of the file and of the text are given in the first 40 bytes (separated by ":")
            int intfileLength = 0;
            File myFile = null;
            if (!questionShortAnswer.getIMAGE().equals("none")) {
                question_text += questionShortAnswer.getIMAGE().split("/")[2];
                myFile = new File(questionShortAnswer.getIMAGE());
                intfileLength = (int) myFile.length();
            } else {
                question_text += questionShortAnswer.getIMAGE() + "///";
            }

            //writing of the first 40 bytes
            byte[] bytearraytext = question_text.getBytes(Charset.forName("UTF-8"));
            int textbyteslength = bytearraytext.length;
            byte[] bytearray = new byte[40 + textbyteslength + intfileLength];
            String fileLength;
            fileLength = "SHRTA";
            fileLength += ":" + String.valueOf(intfileLength);
            fileLength += ":" + String.valueOf(textbyteslength);
            byte[] bytearraystring = fileLength.getBytes(Charset.forName("UTF-8"));
            for (int k = 0; k < bytearraystring.length; k++) {
                bytearray[k] = bytearraystring[k];
            }

            //copy the textbytes into the array which will be sent
            for (int k = 0; k < bytearraytext.length; k++) {
                bytearray[k + 40] = bytearraytext[k];
            }

            //write the file into the bytearray   !!! tested up to 630000 bytes, does not work with file of 4,7MB
            if (!questionShortAnswer.getIMAGE().equals("none")) {
                fis = new FileInputStream(myFile);
                bis = new BufferedInputStream(fis);
                bis.read(bytearray, 40 + textbyteslength, intfileLength);
            }
            System.out.println("Sending " + questionShortAnswer.getIMAGE() + "(" + intfileLength + " bytes)");
            int arraylength = bytearray.length;
            System.out.println("Sending " + arraylength + " bytes in total");
            if (singleStudentOutputStream == null) {
                for (int i = 0; i < aClass.getClassSize(); i++) {
                    OutputStream tempOutputStream = aClass.getStudents_array().get(i).getOutputStream();
                    try {
                        tempOutputStream.write(bytearray, 0, arraylength);
                        tempOutputStream.flush();
                    } catch (IOException ex2) {
                        ex2.printStackTrace();
                    }
                }
            } else {
                try {
                    singleStudentOutputStream.write(bytearray, 0, arraylength);
                    singleStudentOutputStream.flush();
                } catch (IOException ex2) {
                    ex2.printStackTrace();
                }
            }

            System.out.println("Done.");
        }
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
                int bytesread = 0;
                while (bytesread >= 0) {
                    try {
                        byte[] in_bytearray = new byte[1000];
                        bytesread = answerInStream.read(in_bytearray);
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
                                double eval = DbTableIndividualQuestionForStudentResult.addIndividualQuestionForStudentResult(Integer.valueOf(answerString.split("///")[5]),answerString.split("///")[2],answerString.split("///")[3],answerString.split("///")[0]);
                                SendEvaluation(eval,Integer.valueOf(answerString.split("///")[5]), arg_student);
                                mTableQuestionVsUser.addAnswerForUser(arg_student, answerString.split("///")[3],answerString.split("///")[4], eval);
                            } else if (answerString.split("///")[0].contains("CONN")) {
                                Student student = arg_student;
                                student.setAddress(answerString.split("///")[1]);
                                student.setName(answerString.split("///")[2]);
                                Integer studentID = DbTableStudents.addStudent(answerString.split("///")[1], answerString.split("///")[2]);
                                if (studentID == -2) {
                                    JOptionPane.showMessageDialog(mTableQuestionVsUser,
                                            student.getName() + " is trying to connect but has a different " +
                                                    "device identifier than the student with the same name already registered.");
                                }
                                student.setStudentID(studentID);
                                mTableQuestionVsUser.addUser(student, true);
                                aClass.updateStudent(student);
                            } else if (answerString.split("///")[0].contains("DISC")) {
                                Student student = new Student(answerString.split("///")[1], answerString.split("///")[2]);
                                mTableQuestionVsUser.userDisconnected(student);
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

    private void SendEvaluation(double evaluation, int questionID, Student student) {
        String evalToSend = "EVAL///" + evaluation + "///" + questionID + "///";
        System.out.println("sending: " + evalToSend);
        byte[] bytes = new byte[40];
        int bytes_length = 0;
        try {
            bytes_length = evalToSend.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < bytes_length; i++) {
            try {
                bytes[i] = evalToSend.getBytes("UTF-8")[i];
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            student.getOutputStream().write(bytes, 0, bytes.length);
            student.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void UpdateEvaluation(double evaluation, Integer questionID, Integer studentID) {
        Student student = aClass.getStudentWithID(studentID);
        String evalToSend = "UPDEV///" + evaluation + "///" + questionID + "///";
        System.out.println("sending: " + evalToSend);
        byte[] bytes = new byte[40];
        int bytes_length = 0;
        try {
            bytes_length = evalToSend.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < bytes_length; i++) {
            try {
                bytes[i] = evalToSend.getBytes("UTF-8")[i];
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            student.getOutputStream().write(bytes, 0, bytes.length);
            student.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void SendNewConnectionResponse(OutputStream arg_outputStream, Boolean maximum) throws IOException {
        String response;
        if (maximum) {
            response = "SERVR///MAX///";
        } else {
            response = "SERVR///OK///";
        }
        byte[] bytes = new byte[40];
        int bytes_length = response.getBytes("UTF-8").length;
        for (int i = 0; i < bytes_length; i++) {
            bytes[i] = response.getBytes("UTF-8")[i];
        }
        arg_outputStream.write(bytes, 0, bytes.length);
        arg_outputStream.flush();
        //serverOutStream.write(bytes, 0, bytes.length);
        //serverOutStream.flush();
    }

    public Classroom getClassroom() {
        return aClass;
    }

    public void removeQuestion(int index) {
        mTableQuestionVsUser.removeQuestion(index);
    }
    public void addQuestion(String question) {
        mTableQuestionVsUser.addQuestion(question);
    }
}

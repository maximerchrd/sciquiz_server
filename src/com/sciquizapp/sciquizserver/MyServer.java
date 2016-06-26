/*
 * TO DO LIST
 * - afficher une liste des appareils connectés
 * - afficher un avertissement quand un appareil quitte l'app
 * - pouvoir répondre à plusieurs questions de manière non sychronisée (entre les utilisateurs)
 * - écrire un fichier pour excel avec utilisateurs vs questions (et réponses dans les cases)
 * ->1 dans l'interface, faire un tableau pour la session ac utilisateurs vs questions
 * - interface pour choisir les questions: liste
 * - pouvoir choisir à l'avance plusieurs question et faire ensuite "question suivante"
 * - donner le résultat sur chaque appareil utilisateur à la fin de la session
 * - définir une "séquence de questions" (= session)
 */

package com.sciquizapp.sciquizserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.sciquizapp.sciquizserver.ChooseDropActionDemo;
import com.sciquizapp.sciquizserver.AWTCounter;
import com.sciquizapp.sciquizserver.DBManager;

public class MyServer {

	public static void main(String[] args) throws Exception {

		DBManager dao = new DBManager();
	    dao.createDBIfNotExists();
	    dao.createQuestionsTableIfNotExists();
	    
		//Setup the table

		//Create and set up the window.
		JFrame frame = new JFrame("Table");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		Table TableUserVsQuest = new Table();
		TableUserVsQuest.setOpaque(true); //content panes must be opaque
		frame.setContentPane(TableUserVsQuest);
		//Display the window.
		frame.pack();
		frame.setVisible(true);

		//Turn off metal's use of bold fonts
		ChooseDropActionDemo newChooseDropAction = new ChooseDropActionDemo();
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        newChooseDropAction.createAndShowGUI();
		
		AWTCounter app = new AWTCounter(TableUserVsQuest);

		ServerSocket serverSocket = null;
		Socket socket = null;
		DataInputStream dataInputStream = null;
		DataOutputStream dataOutputStream = null;

		try {
			serverSocket = new ServerSocket(8080);
			System.out.println("Listening :8080");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while(true){
			try {
				socket = serverSocket.accept();
				dataInputStream = new DataInputStream(socket.getInputStream());
				dataOutputStream = new DataOutputStream(socket.getOutputStream());
				System.out.println("ip: " + socket.getInetAddress());
				String textToWrite = dataInputStream.readUTF();
				//TableUserVsQuest.addUser(String.valueOf(socket.getInetAddress()));
				if (!TableUserVsQuest.IsUserInTable(textToWrite)) {
					TableUserVsQuest.addUser(textToWrite.split(";")[0]);
				}
				
				app.editTextField(textToWrite);
				dataOutputStream.writeUTF(String.valueOf(app.getQuestionNumber()));
				if (textToWrite.endsWith(";")) {
					TableUserVsQuest.addAnswerForUser(textToWrite);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				if( socket!= null){
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if( dataInputStream!= null){
					try {
						dataInputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if( dataOutputStream!= null){
					try {
						dataOutputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
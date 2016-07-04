package com.sciquizapp.sciquizserver;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.SwingWorker;

public class SendQuestion extends SwingWorker {
	public SendQuestion(Question question_to_send) throws IOException {

		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(8081);
			System.out.println("Listening :8081");
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("Can't setup server on this port number. ");
		}

		Socket socket = null;
		InputStream in = null;
		OutputStream out = null;
		
		/*while(true){
			try {
				socket = serverSocket.accept();
			} catch (IOException ex) {
				System.out.println("Can't accept client connection. ");
			}

			try {
			in = socket.getInputStream();
		} catch (IOException ex) {
			System.out.println("Can't get socket input stream. ");
		}

		try {
			out = new FileOutputStream(question_to_send.getIMAGE());
		} catch (FileNotFoundException ex) {
			System.out.println("File not found. ");
		}

		byte[] bytes = new byte[16*1024];

		int count;
		while ((count = in.read(bytes)) > 0) {
			out.write(bytes, 0, count);
		}
			
			out.close();
			in.close();
			socket.close();
			serverSocket.close();
		}*/
	}

	@Override
	protected Object doInBackground() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}

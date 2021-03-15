package com.iskenderov.client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client extends JFrame {

	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;

	// constructor
	public Client(String host) {
		super("Client mofo!");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				sendMessage(event.getActionCommand());
				userText.setText("");

			}
		});
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(300, 150);
		setVisible(true);
	}

	// connect to server
	public void startRunning() {
		try {
			connectToServer();
			setupStreams();
			whileChating();
		} catch (EOFException eofException) {
			showMessage("\n Client terminated connection");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			closeCrap();
		}
	}

	private void connectToServer() throws IOException {
		showMessage("Attempting connection...");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("coonected to:" + connection.getInetAddress().getHostName());
	}

	// setup streams to send and receive messages
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Dude your streams are now to go \n");
	}

	// while chating with server
	private void whileChating() throws IOException {
		ableToType(true);

		do {
			try {
				message = (String) input.readObject();
				showMessage("\n " + message);
			} catch (ClassNotFoundException e) {
				showMessage("\n I dont know object type");
			}
		} while (!message.equals("SERVER - END"));
	}

	// close the streams and sockets
	private void closeCrap() {
		showMessage("\n closing crap done!");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// send messages to server
	private void sendMessage(String message) {
		try {
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\nCLIENT - " + message);
		} catch (IOException e) {
			chatWindow.append("\n something messed up sending message hoss!");
		}
	}

	// change or update chat window
	private void showMessage(final String message) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				chatWindow.append(message);

			}
		});
	}

	// gives user permission to type create the text box
	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				userText.setEditable(tof);

			}
		});
	}

}

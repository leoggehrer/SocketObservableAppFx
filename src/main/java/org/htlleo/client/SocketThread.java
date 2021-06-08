package org.htlleo.client;

import org.htlleo.models.Message;
import org.htlleo.models.MessageDistributor;
import org.htlleo.pattern.Observer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketThread extends Thread implements Observer {
    private static final String QuitCommand = "quit";
    private static final String ServerInfoCommand = "serverinfo";
    private static final String DisconnectedCommand = "disconnected";
    private Socket socket;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    public SocketThread(Socket socket) {
        if (socket == null)
            throw new IllegalArgumentException("socket");

        this.socket = socket;
        try {
            oos = new ObjectOutputStream(this.socket.getOutputStream());
            ois = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        setDaemon(true);
    }
    @Override
    public void run() {
        boolean error = false;
        String command = "";

        while (error == false && command.equals(QuitCommand) == false) {
            try {
                Message clientMessage = (Message)ois.readObject();

                System.out.printf("%s\n", clientMessage.toString());
                command = clientMessage.getCommand();
                if (command.equals(QuitCommand)) {
                    String from  = clientMessage.getFrom();
                    MessageDistributor.getInstance().removeObserver(this);

                    clientMessage.setCommand(DisconnectedCommand);
                    clientMessage.setFrom("Chat-Server");
                    clientMessage.setBody("");
                    synchronized (this) {
                        oos.writeObject(clientMessage);
                        oos.flush();
                    }
                    clientMessage.setCommand(ServerInfoCommand);
                    clientMessage.setBody(String.format("%s left the chat", from));
                    MessageDistributor.getInstance().addMessage(clientMessage);
                }
                else {
                    clientMessage.setCommand("dispatch");
                    MessageDistributor.getInstance().addMessage(clientMessage);
                }
            } catch (IOException e) {
                error = true;
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                error = true;
                e.printStackTrace();
            }
        }
        try {
            oos.close();
            oos = null;
            ois.close();
            ois = null;
            socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notify(Object sender, Object args) {
        if (sender instanceof MessageDistributor
            && args instanceof Message
            && oos != null)
        {
            try {
                synchronized (this) {
                    oos.writeObject(args);
                    oos.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

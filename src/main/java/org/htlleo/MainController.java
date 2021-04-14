package org.htlleo;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.htlleo.chatserver.SocketThread;
import org.htlleo.models.Message;
import org.htlleo.models.MessageDistributor;
import org.htlleo.pattern.Observer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainController implements Observer, Initializable {

    @FXML
    public TextArea txtContent;
    @FXML
    public TextField txtPort;
    @FXML
    public Button btnStart;
    @FXML
    public Button btnStop;

    private ServerSocket serverSocket;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtContent.setDisable(false);
        txtContent.setEditable(false);

        txtPort.setText("3333");
        txtPort.setDisable(false);

        btnStart.setDefaultButton(true);
        btnStart.setDisable(false);

        btnStop.setDisable(true);

        MessageDistributor.getInstance().addObserver(this);
    }

    @Override
    public void notify(Object sender, Object args) {
        if (args instanceof Message) {
            Platform.runLater(() -> txtContent.appendText(args.toString() + "\n"));
        }
    }

    public void onStart(ActionEvent actionEvent) {
        if (serverSocket == null) {

            int port = Integer.parseInt(txtPort.getText());

            txtContent.clear();
            Thread t = new Thread(() -> {
                try {
                    serverSocket = new ServerSocket(port);
                    while (true) {
                        Socket socket = serverSocket.accept();
                        SocketThread socketThread = new SocketThread(socket);

                        MessageDistributor.getInstance().addObserver(socketThread);
                        socketThread.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                serverSocket = null;
            });

            t.setDaemon(true);
            t.start();

            txtPort.setDisable(true);
            btnStart.setDisable(true);
            btnStop.setDisable(false);
            System.out.println( "Chat Server is running..." );
        }
    }

    public void onStop(ActionEvent actionEvent) {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            txtPort.setDisable(false);
            btnStart.setDisable(false);
            btnStop.setDisable(true);
        }
    }
}

package info.brandonharris.SimplePackets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by brandon on 6/24/16.
 */
public class SimpleServer extends Thread {
    private boolean running = true;
    private int port;
    private ClientCallback callback;
    private ServerSocket serverSocket;

    public SimpleServer(int port, ClientCallback callback) {
        this.port = port;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (serverSocket == null) {
            return;
        }
        while (running) {
            try {
                final Socket socket = serverSocket.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onClientConnected(socket);
                    }
                }).start();
            } catch (Exception e) {
                if (!e.getLocalizedMessage().equals("Socket closed")) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void close() throws IOException {
        running = false;
        serverSocket.close();
    }
}

package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {

    private final String host;
    private final int port;
    private PrintWriter out;

    public GameClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try {
            Socket socket = new Socket(host, port);

            System.out.println("Conectado ao servidor " + host + ":" + port);

            out = new PrintWriter(socket.getOutputStream(), true);

            ServerListener listener = new ServerListener(socket, this);
            Thread listenerThread = new Thread(listener);
            listenerThread.start();

            listenerThread.join();

        } catch (IOException e) {
            System.out.println("Erro ao conectar ao servidor: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Cliente interrompido.");
            Thread.currentThread().interrupt();
        }
    }

    public void sendMove(int row, int col) {
        out.println("JOGADA " + row + " " + col);
    }
}
package server;

import shared.MessageProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final char playerSymbol;
    private final GameSession session;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket, char playerSymbol, GameSession session) {
        this.socket = socket;
        this.playerSymbol = playerSymbol;
        this.session = session;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            send(MessageProtocol.BEM_VINDO + " " + playerSymbol);

            String message;

            while ((message = in.readLine()) != null) {
                handleMessage(message);
            }

        } catch (IOException e) {
            System.out.println("Jogador " + playerSymbol + " desconectado.");
        } finally {
            closeConnection();
        }
    }

    private void handleMessage(String message) {
        String[] parts = message.split(" ");

        if (parts.length == 3 && parts[0].equals(MessageProtocol.JOGADA)) {
            try {
                int row = Integer.parseInt(parts[1]);
                int col = Integer.parseInt(parts[2]);

                session.handleMove(this, row, col);

            } catch (NumberFormatException e) {
                send(MessageProtocol.JOGADA_INVALIDA + " Linha e coluna devem ser números.");
            }
        } else {
            send(MessageProtocol.ERRO + " Mensagem inválida.");
        }
    }

    public void send(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public char getPlayerSymbol() {
        return playerSymbol;
    }

    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Erro ao fechar conexão do jogador " + playerSymbol + ".");
        }
    }
}
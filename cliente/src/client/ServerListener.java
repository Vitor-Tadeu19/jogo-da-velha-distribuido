package client;

import shared.Board;
import shared.MessageProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerListener implements Runnable {

    private final Socket socket;
    private final GameClient client;
    private final ConsoleInput consoleInput;

    public ServerListener(Socket socket, GameClient client) {
        this.socket = socket;
        this.client = client;
        this.consoleInput = new ConsoleInput();
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;

            while ((message = in.readLine()) != null) {
                handleMessage(message);
            }

        } catch (IOException e) {
            System.out.println("Conexão com o servidor encerrada.");
        }
    }

    private void handleMessage(String message) {

        if (message.startsWith(MessageProtocol.BEM_VINDO)) {
            String[] parts = message.split(" ");

            System.out.println();
            System.out.println("=================================");
            System.out.println("       JOGO DA VELHA ONLINE");
            System.out.println("=================================");
            System.out.println();
            System.out.println("Você é o jogador " + parts[1] + ".");
            System.out.println();

            return;
        }

        if (message.startsWith(MessageProtocol.TABULEIRO)) {
            String[] parts = message.split(" ");

            if (parts.length >= 2) {
                Board board = Board.fromSerialized(parts[1]);
                System.out.println(board.toDisplayString());
            }

            return;
        }

        if (message.startsWith(MessageProtocol.SUA_VEZ)) {

            System.out.println();
            System.out.println("=================================");
            System.out.println("            SUA VEZ");
            System.out.println("=================================");

            int[] move = consoleInput.readMove();
            client.sendMove(move[0], move[1]);

            return;
        }

        if (message.startsWith(MessageProtocol.AGUARDE)) {

            System.out.println();
            System.out.println("=================================");
            System.out.println("        VEZ DO ADVERSÁRIO");
            System.out.println("=================================");
            System.out.println();

            System.out.println(
                    message.substring(MessageProtocol.AGUARDE.length()).trim()
            );

            return;
        }

        if (message.startsWith(MessageProtocol.JOGADA_INVALIDA)) {

            System.out.println();
            System.out.println("=================================");
            System.out.println("        JOGADA INVÁLIDA");
            System.out.println("=================================");
            System.out.println();

            System.out.println(
                    message.substring(MessageProtocol.JOGADA_INVALIDA.length()).trim()
            );

            return;
        }

        if (message.startsWith(MessageProtocol.FIM)) {

            System.out.println();
            System.out.println("=================================");
            System.out.println("          FIM DE JOGO");
            System.out.println("=================================");
            System.out.println();

            System.out.println(
                    message.substring(MessageProtocol.FIM.length()).trim()
            );

            return;
        }

        if (message.startsWith(MessageProtocol.ERRO)) {
            System.out.println(message.substring(MessageProtocol.ERRO.length()).trim());
            return;
        }

        System.out.println(message);
    }
}
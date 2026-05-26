package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {

    private final int port;

    public GameServer(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("Servidor iniciado na porta " + port + ".");
        System.out.println("Aguardando dois jogadores...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            Socket playerOneSocket = serverSocket.accept();
            System.out.println("Jogador 1 conectado.");

            Socket playerTwoSocket = serverSocket.accept();
            System.out.println("Jogador 2 conectado.");

            GameSession session = new GameSession();

            ClientHandler playerOne = new ClientHandler(playerOneSocket, 'X', session);
            ClientHandler playerTwo = new ClientHandler(playerTwoSocket, 'O', session);

            session.setPlayers(playerOne, playerTwo);

            new Thread(playerOne).start();
            new Thread(playerTwo).start();

            session.startGame();

        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }
}
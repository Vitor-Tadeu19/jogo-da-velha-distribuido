package server;

import shared.Board;
import shared.MessageProtocol;

public class GameSession {

    private final Board board;
    private ClientHandler playerX;
    private ClientHandler playerO;
    private ClientHandler currentPlayer;
    private boolean gameFinished;

    public GameSession() {
        this.board = new Board();
        this.gameFinished = false;
    }

    public void setPlayers(ClientHandler playerX, ClientHandler playerO) {
        this.playerX = playerX;
        this.playerO = playerO;
        this.currentPlayer = playerX;
    }

    public void startGame() {
        broadcast(MessageProtocol.TABULEIRO + " " + board.serialize());

        playerX.send(MessageProtocol.SUA_VEZ);
        playerO.send(
                MessageProtocol.AGUARDE +
                        " Aguarde a jogada do jogador X."
        );

        System.out.println("Partida iniciada.");
    }

    public synchronized void handleMove(ClientHandler player, int row, int col) {
        if (gameFinished) {
            player.send(MessageProtocol.FIM + " A partida já foi encerrada.");
            return;
        }

        if (player != currentPlayer) {
            player.send(MessageProtocol.AGUARDE + " Ainda não é sua vez.");
            return;
        }

        char symbol = player.getPlayerSymbol();

        boolean validMove = board.makeMove(row, col, symbol);

        if (!validMove) {
            player.send(MessageProtocol.JOGADA_INVALIDA + " Posição inválida ou já ocupada.");
            player.send(MessageProtocol.SUA_VEZ);
            return;
        }

        broadcast(MessageProtocol.TABULEIRO + " " + board.serialize());

        if (board.hasWinner(symbol)) {
            gameFinished = true;
            broadcast(MessageProtocol.FIM + " Jogador " + symbol + " venceu!");
            closePlayers();
            return;
        }

        if (board.isFull()) {
            gameFinished = true;
            broadcast(MessageProtocol.FIM + " A partida terminou empatada!");
            closePlayers();
            return;
        }

        switchTurn();
    }

    private void switchTurn() {

        if (currentPlayer == playerX) {

            currentPlayer = playerO;

            playerO.send(MessageProtocol.SUA_VEZ);

            playerX.send(
                    MessageProtocol.AGUARDE +
                            " Aguarde a jogada do jogador O."
            );

        } else {

            currentPlayer = playerX;

            playerX.send(MessageProtocol.SUA_VEZ);

            playerO.send(
                    MessageProtocol.AGUARDE +
                            " Aguarde a jogada do jogador X."
            );
        }
    }

    private void broadcast(String message) {
        playerX.send(message);
        playerO.send(message);
    }

    private void closePlayers() {
        playerX.closeConnection();
        playerO.closeConnection();
    }
}
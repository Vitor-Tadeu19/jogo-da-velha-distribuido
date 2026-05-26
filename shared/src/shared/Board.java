package shared;

public class Board {

    private final char[][] cells;

    public Board() {
        cells = new char[3][3];
        clear();
    }

    public void clear() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j] = ' ';
            }
        }
    }

    public boolean makeMove(int row, int col, char player) {
        if (!isValidPosition(row, col)) {
            return false;
        }

        if (cells[row][col] != ' ') {
            return false;
        }

        cells[row][col] = player;
        return true;
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 3 && col >= 0 && col < 3;
    }

    public boolean hasWinner(char player) {
        for (int i = 0; i < 3; i++) {
            if (cells[i][0] == player && cells[i][1] == player && cells[i][2] == player) {
                return true;
            }

            if (cells[0][i] == player && cells[1][i] == player && cells[2][i] == player) {
                return true;
            }
        }

        if (cells[0][0] == player && cells[1][1] == player && cells[2][2] == player) {
            return true;
        }

        return cells[0][2] == player && cells[1][1] == player && cells[2][0] == player;
    }

    public boolean isFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (cells[i][j] == ' ') {
                    return false;
                }
            }
        }

        return true;
    }

    public String serialize() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                builder.append(cells[i][j] == ' ' ? '-' : cells[i][j]);
            }
        }

        return builder.toString();
    }

    public static Board fromSerialized(String data) {
        Board board = new Board();

        if (data == null || data.length() != 9) {
            return board;
        }

        int index = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                char value = data.charAt(index++);
                board.cells[i][j] = value == '-' ? ' ' : value;
            }
        }

        return board;
    }

    public String toDisplayString() {
        return """
            
            =========================
                 JOGO DA VELHA
            =========================
            
                 0   1   2
            
            0    %c | %c | %c
                ---+---+---
            
            1    %c | %c | %c
                ---+---+---
            
            2    %c | %c | %c
            
            """
                .formatted(
                        cells[0][0], cells[0][1], cells[0][2],
                        cells[1][0], cells[1][1], cells[1][2],
                        cells[2][0], cells[2][1], cells[2][2]
                );
    }
}
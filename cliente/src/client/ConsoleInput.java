package client;

import java.util.Scanner;

public class ConsoleInput {

    private final Scanner scanner;

    public ConsoleInput() {
        this.scanner = new Scanner(System.in);
    }

    public int[] readMove() {

        System.out.println();
        System.out.println("=================================");
        System.out.println("          SUA JOGADA");
        System.out.println("=================================");
        System.out.println("Escolha uma posição do tabuleiro.");
        System.out.println();

        int row = readNumber("Linha (0 a 2): ");
        int col = readNumber("Coluna (0 a 2): ");

        return new int[]{row, col};
    }

    private int readNumber(String message) {
        while (true) {
            System.out.print(message);

            String input = scanner.nextLine();

            try {
                int number = Integer.parseInt(input);

                if (number >= 0 && number <= 2) {
                    return number;
                }

                System.out.println("Posição inválida. Digite um número entre 0 e 2.");

            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número.");
            }
        }
    }
}
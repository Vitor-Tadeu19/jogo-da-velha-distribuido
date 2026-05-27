package client;

public class ClientMain {

    public static void main(String[] args) {
        String host = "10.8.184.12";
        int port = 5000;

        if (args.length >= 1) {
            host = args[0];
        }

        if (args.length >= 2) {
            port = Integer.parseInt(args[1]);
        }

        GameClient client = new GameClient(host, port);
        client.start();
    }
}
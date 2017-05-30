package jpclient;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Provide program arguments <HOSTNAME> <PORT>");
            System.out.println("exiting...");
            System.exit(-1);
        }

        final String hostName = args[0];
        final int port = Integer.parseInt(args[1]);

        try (Scanner stdin = new Scanner(System.in);
             Socket sock = new Socket(hostName, port);)
        {
            final PrintWriter sockOut = new PrintWriter(sock.getOutputStream());
            boolean exitRead = false;

            while (stdin.hasNextLine()) {
                final String[] words = stdin.nextLine().split(" ");

                if (words.length <= 0) {
                    continue;
                }

                switch (words[0]) {
                case "sort":
                    final Request req = new Request(words[0], Integer.parseInt(words[1]));
                    sockOut.write(req.getContent());
                    break;
                case "exit":
                    exitRead = true;
                    break;
                default:
                    break;
                }

                if (exitRead) { break; }
            }
        }
        catch (Exception ex) {
            System.out.println("Caught something in Client.main: " + ex.toString());
        }

        System.out.println("exiting...");
    }

    private static final class Request {
        private String content;
        
        private Request(String action, int num) {
            switch (action) {
            case "sort":
                content = "sort " + genIntegerListStr(num);
                break;
            default:
                System.out.println("WARNING: Unhandled case in Request ctor");
                content = "noop";
                break;
            }
        }

        private String getContent() {
            return content;
        }

        private String genIntegerListStr(int num) {
            final int min = 0;
            final int max = 999;

            if (num <= 0) {
                return "0";
            }

            final Random rand = new Random();
            final int reserveAmount = (num + 1) * 2;
            final StringBuilder sb = new StringBuilder(reserveAmount);

            sb.append(Integer.toString(num));
            for (int i = 0; i < num; i++) {
                final int nextVal = rand.nextInt(max - min + 1) + min;
                sb.append(' ');
                sb.append(Integer.toString(nextVal));
            }

            return sb.toString();
        }
    }
}

package jpclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import jputils.Functions;

public class Client {
    private static final String EXIT_MSG = "exiting...";

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Provide program arguments <HOSTNAME> <PORT>");
            System.out.println(EXIT_MSG);
            System.exit(-1);
        }

        final String hostName = args[0];
        final int port = Integer.parseInt(args[1]);

        try (Scanner stdin = new Scanner(System.in);)
        {
            final StringBuilder sb = new StringBuilder();
            boolean exitRead = false;

            while (stdin.hasNextLine()) {
                final String[] words = stdin.nextLine().split(" ");

                if (words.length <= 0) {
                    continue;
                }

                switch (words[0]) {
                case "sort":
                    try (Socket sock = new Socket(hostName, port);
                         PrintWriter sockOut = new PrintWriter(sock.getOutputStream(), true);
                         BufferedReader sockIn = new BufferedReader( new InputStreamReader(sock.getInputStream()));)
                    {
                        // Build a request, send it, wait for a response, print the response
                        final Request req = new Request(words[0], Integer.parseInt(words[1]));
                        sockOut.println(req.getContent());

                        final String serverResp = getResponse(sockIn, sb);
                        if (serverResp == null) {
                            System.out.println("Error reading response from server");
                        } else {
                            System.out.println("Server response: " + serverResp);
                        }
                    } catch (Exception e) {
                        System.out.println("Error performing sort request");
                    }

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

        System.out.println(EXIT_MSG);
    }
    
    // Blocks until response is read from socket. Returns null if an error occurred.
    private static String getResponse(BufferedReader sockReader, StringBuilder sb0) {
        String retstr = null;
        try {
            while (retstr == null) {
                retstr = Functions.readLine(sockReader, sb0);
            }
        } catch (IOException e) {
            System.out.println("Failed to get response");
        }
        return retstr;
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

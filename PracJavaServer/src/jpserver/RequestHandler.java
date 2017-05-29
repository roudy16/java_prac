package jpserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.ServerSocket;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private static final String SUCCESS = "SUCCESS";
    private static final String ERROR = "ERROR";
    private final Socket client;
    private ServerSocket serverSocket;

    public RequestHandler(Socket clientSock) {
        this.client = clientSock;
    }

    @Override
    public final void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));)
        {
            System.out.println("Processing request on ThreadID: " + Thread.currentThread().getName());
            final String reqContent = in.readLine();

            if (reqContent != null && !"".equals(reqContent)) {
                final UUID reqID = UUID.randomUUID();
                final RequestResult res = processReq(reqContent, reqID);
                
                if (res.getErrCode() != 0) {
                    System.out.println("Error processing request ID: " + reqID.toString()
                                      + " Err: " + Integer.toString(res.getErrCode()));
                }
                
                writer.write(res.getResponse());
            } else {
                writer.write(ERROR + " content empty");
            }

            writer.flush();
        }
        catch (IOException e) {
            System.out.println("I/O exception: " + e);
        }
        catch (Exception ex) {
            System.out.println("Exception in Thread Run: " + ex);
        }
    }

    public static void main(String[] args) {
        final RequestHandler rh = new RequestHandler(null);
        final String request = "sort 6 4 11 21 102 3 37";
        final String[] reqWords = request.split(" ");

        final Action act = rh.parseAction(reqWords[0]);
        System.out.println(act);

        final ArrayList<Integer> sortedNums = rh.sortRequest(reqWords);
        System.out.println(sortedNums);
    }

    private RequestResult processReq(String reqContent, final UUID reqID) {
        System.out.println("Processing request ID: " + reqID.toString() + " on thread: "
                          + Thread.currentThread().getName() + " : " + reqContent);

        final String[] reqWords = reqContent.split(" ");

        if (reqWords.length < 2) {
            return new RequestResult(ERROR + " request did not contain minimum <ACTION> <SIZE>", -1);
        }

        final Action act = parseAction(reqWords[0]);
        String response = "";
        int retCode = 0;

        switch (act) {
        case SORT:
            final ArrayList<Integer> sortedNums = sortRequest(reqWords);
            if (sortedNums == null) {
                retCode = -1;
                break;
            }

            final String numStr = toResponseFormat(sortedNums);
            if (numStr == null || "".equals(numStr)) {
                retCode = -1;
                break;
            }

            response = SUCCESS + ' ' + numStr;
            break;
        case NONE:
        default:
            retCode = -1;
            response = ERROR + " Action not read properly";

        }

        return new RequestResult(response, retCode);
    }

    private final class RequestResult {
        private final String response;
        private final int errCode;

        private RequestResult(String resp, int err) {
            response = resp;
            errCode = err;
        }

        private String getResponse() {
            return response;
        }

        private int getErrCode() {
            return errCode;
        }
    }

    private ArrayList<Integer> sortRequest(String[] reqWords) {
        final int size = Integer.parseInt(reqWords[1]); // may throw if no number read
        final int startIdx = 2;
        final int endIdx = startIdx + size;

        if (size == 0 || endIdx != reqWords.length) {
            System.out.println("WARNING: Expected different number of elements in request, doing nothing");
            return null;
        }

        final ArrayList<Integer> nums = new ArrayList<>(size);

        for (int i = startIdx; i < endIdx; i++) {
            nums.add(Integer.valueOf(Integer.parseInt(reqWords[i]))); // may throw
        }

        Collections.sort(nums);

        return nums;
    }

    private String toResponseFormat(ArrayList<Integer> nums) {
        final int size = nums.size();

        if (size <= 0) {
            return "";
        }

        final StringBuilder sb = new StringBuilder(size * 2);
        sb.append(nums.get(0).toString());
        for (int i = 1; i < size; i++) {
            sb.append(' ');
            sb.append(nums.get(i).toString());
        }

        final String resp = sb.toString();
        return resp;
    }

    private enum Action {
        NONE,
        SORT
    }

    private Action parseAction(String str) {
        Action retval = Action.NONE;
        switch (str) {
        case "sort":
            retval = Action.SORT;
            break;
        default:
            // noop
        }

        return retval;
    }

    public final ServerSocket getServerSocket() {
        return serverSocket;
    }

    public final void setServerSocket(ServerSocket sock) {
        this.serverSocket = sock;
    }
}

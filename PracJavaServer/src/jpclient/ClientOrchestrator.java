package jpclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.UUID;

import jputils.Constants;
import jputils.Functions;

public final class ClientOrchestrator {
    // Singleton instance
    public static final ClientOrchestrator INSTANCE = new ClientOrchestrator();

    // Ensure no public instantiation
    private ClientOrchestrator() {
        clients = new LinkedHashMap<>();
        recycleIndices = new TreeSet<>();
        nextNewIndex = 0;
        
        consoleInLines = new ConcurrentLinkedDeque<>();
        consoleThread = new ConsoleInputReaderThread(System.in);
        consoleThread.start();
    }
    

    private static final String HOSTNAME = "localhost";
    private static final String PORT = Integer.toString(Constants.PORT);

    private final LinkedHashMap<Integer, ClientProcess> clients;
    private final TreeSet<Integer> recycleIndices;
    private int nextNewIndex;

    private final ConcurrentLinkedDeque<String> consoleInLines;
    private final ConsoleInputReaderThread consoleThread;

    public static void main (String[] args) {
        // Launch some number of client processes on startup if integer argument provided
        if (args.length != 0) {
            try {
                final int numStartupProcesses = Integer.parseInt(args[0]);
                for (int i = 0; i < numStartupProcesses; i++) {
                    INSTANCE.launchNewClientProcess();
                }
            } catch (Exception e) {
                System.out.println("Failed to parse program argument. USAGE program [num start processes]");
            }
        }

        boolean exitRead = false;
        while (!exitRead) {
            /* 1. Read and process console input
             * 2. Read output from child processes
             * Repeat
             */

            while(!INSTANCE.consoleInLines.isEmpty()) {
                final String cmd = INSTANCE.consoleInLines.removeFirst();
                exitRead = processCommand(cmd);
            }

            try {
                final StringBuilder sb = new StringBuilder();
                for (Map.Entry<Integer, ClientProcess> entry : INSTANCE.clients.entrySet()) {
                    final ClientProcess cp = entry.getValue();
                    printProcessOutput(cp, sb);
                    cp.state = ClientState.STANDBY_FOR_INPUT;
                }
            } catch (IOException e) {
                System.out.println("Exception thrown reading process output");
            }
        }
    }

    private static boolean processCommand(String cmd) {
        if (cmd == null) {
            cmd = "";
        }

        boolean exitRead = false;

        final String[] words = cmd.split(" ");

        if (words.length < 1) {
            System.out.println("No content in command passed to processCommand()");
            return exitRead;
        }

        switch (words[0]) {
        case "exit":
            exitRead = true;
            processExitCommand();
            INSTANCE.consoleThread.interrupt();
            break;
        case "kill":
            processKillCommand(words);
            break;
        case "show":
            processShowCommand(words);
            break;
        case "sort":
            processSortCommand(words);
            break;
        case "spawn":
            processSpawnCommand(words);
            break;
        default:
            System.out.println("No case to process command: " + cmd);
            break;
        }

        return exitRead;
    }

    private static void printProcessOutput(ClientProcess cp, StringBuilder sb) throws IOException {
        String output = null;
        while ((output = cp.getOutput()) != null) {
            sb.append("Client Output ");
            sb.append(cp.id.toString());
            sb.append(" : ");
            sb.append(output);
            System.out.println(sb.toString());
            sb.delete(0, sb.length());
        }
    }

    private static void processExitCommand() {
        try {
            final StringBuilder sb = new StringBuilder();
            for (Map.Entry<Integer, ClientProcess> entry : INSTANCE.clients.entrySet()) {
                final ClientProcess cp = entry.getValue();

                printProcessOutput(cp, sb);
                cp.endProcess();
            }
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void processKillCommand(String[] words) {
        if (words.length != 2) {
            System.out.println("Expected kill command with exactly one argument");
            return;
        }

        try {
            final int procIdx = Integer.parseInt(words[1]);
            final ClientProcess cproc = INSTANCE.removeProcFromMap(procIdx);
            if (cproc != null) {
                cproc.endProcess();
            } else {
                System.out.println("No process with index: " + words[1]);
            }
        } catch (NumberFormatException e) {
            System.out.println("kill command expects exactly one integer argument");
        }
    }

    private static void processSortCommand(String[] words) {
        if (words.length != 2) {
            System.out.println("Expected sort command with exactly one argument");
            return;
        }

        try {
            final int num = Integer.parseInt(words[1]);
            if (num < 1) {
                System.out.println("Expect a positive integer for sort command");
            } else {
                issueSortCommands(num);
            }
        } catch (NumberFormatException e) {
            System.out.println("sort command expects exactly one integer argument");
        }
    }

    private static final String SHOW_FORMAT = "%-4s %-37s %-12s\n";

    private static void printShowHeader() {
        System.out.printf(SHOW_FORMAT, "IDX", "UUID", "STATUS");
    }

    private static void processShowCommand(String[] words) {
        printShowHeader();

        for (Map.Entry<Integer, ClientProcess> entry : INSTANCE.clients.entrySet()) {
            final ClientProcess cp = entry.getValue();
            System.out.printf(SHOW_FORMAT, entry.getKey(), cp.id, cp.state);
        }
    }

    private static void processSpawnCommand(String[] words) {
        if (words.length != 2) {
            System.out.println("Expected spawn command with exactly one argument");
            return;
        }

        try {
            final int num = Integer.parseInt(words[1]);
            if (num < 1) {
                System.out.println("Expect a positive integer for spawn command");
            } else {
                for (int i = 0; i < num; i++) {
                    INSTANCE.launchNewClientProcess();
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("spawn command expects exactly one integer argument");
        }
    }

    // Distribute sort commands evenly across all client processes
    // TODO Could make this better with a queue and sending out jobs to processes
    // as they finish their previous jobs.
    private static void issueSortCommands(int num) {
        if (num == 0) {
            return;
        }

        final int sz = INSTANCE.clients.size();
        final int minNumEach = num / sz;
        final int plus1End = num % sz;

        final int max = 20;
        final int min = 5;
        final Random rand = new Random();

        try {
            int i = 0;
            for (Map.Entry<Integer, ClientProcess> entry : INSTANCE.clients.entrySet()) {
                final ClientProcess cp = entry.getValue();

                for (int j = 0; j < minNumEach; j++) {
                    final String cmd = "sort " + Integer.toString(rand.nextInt(max - min + 1) + min);
                    cp.sendCommand(cmd);
                }

                if (i < plus1End) {
                    final String cmd = "sort " + Integer.toString(rand.nextInt(max - min + 1) + min);
                    cp.sendCommand(cmd);

                    if (minNumEach == 0) {
                        cp.state = ClientState.WAITING_FOR_SERVER;
                    }
                }

                if (minNumEach != 0) {
                    cp.state = ClientState.WAITING_FOR_SERVER;
                }

                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void launchNewClientProcess() {
        final String javaHome = System.getProperty("java.home");
        final String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        final String classPath = System.getProperty("java.class.path");
        final String className = Client.class.getCanonicalName();

        final ProcessBuilder pb = new ProcessBuilder(javaBin, "-cp", classPath, className, HOSTNAME, PORT);
        final ClientProcess cproc = new ClientProcess();

        try {
            cproc.setProc(pb.start());
            if (!cproc.setStdIn()) {
                // TODO some error
            }

            if (!cproc.setStdOut()) {
                // TODO some error
            }

            if (!addProcToMap(cproc)) {
                // TODO more error
            }

            cproc.state = ClientState.STANDBY_FOR_INPUT;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean addProcToMap(ClientProcess cproc) {
        boolean success = false;

        final Integer index = getNextAvailableIndex();
        final ClientProcess res = clients.putIfAbsent(index, cproc);
        if (res != null) {
            System.out.println("WARNING: Process already present in client process map, this should never happen");
            // TODO better error handling
        } else {
            success = true;
        }

        return success;
    }

    private ClientProcess removeProcFromMap(Integer index) {
        final ClientProcess res = clients.remove(index);
        if (res == null) {
            System.out.println("WARNING: Can't remove process, none with index: " + index.toString());
            // TODO better error handling
        } else {
            recycleIndices.add(index);
        }
        return res;
    }

    private Integer getNextAvailableIndex() {
        final Integer retval = recycleIndices.pollFirst();
        if (retval == null) {
            return Integer.valueOf(nextNewIndex++);
        }
        return retval;
    }

    private final class ClientProcess {
        private final UUID id;
        private Process proc;
        private BufferedWriter stdin;
        private BufferedReader stdout;
        private StringBuilder sb;
        private ClientState state;

        private ClientProcess() {
            id = UUID.randomUUID();
            sb = new StringBuilder();
            state = ClientState.PREEXECUTE;
        }

        @Override
        public void finalize() {
            try {
                if (stdin != null) {
                    stdin.close();
                }
                if (stdout != null) {
                    stdout.close();
                }
            } catch (IOException e) {
                System.out.println("Error finalizing ClientProcess Object: " + e.getMessage());
            } finally {
                if (proc != null) {
                    endProcess();
                }
            }
        }

        private void setProc(Process pc) {
            proc = pc;
        }

        private boolean setStdIn() {
            boolean success = false;
            if (proc != null) {
                stdin = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
                success = true;
            }
            return success;
        }

        private boolean setStdOut() {
            boolean success = false;
            if (proc != null) {
                stdout = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                success = true;
            }
            return success;
        }

        private void sendCommand(String cmd) throws IOException {
            if (stdin == null) {
                System.out.println("Stdin not set in process: " + id);
            }
            stdin.write(cmd);
            stdin.newLine();
            stdin.flush();
        }
        
        private String getOutput() throws IOException {
            return Functions.readLine(stdout, sb);
        }
        
        private void endProcess() {
            proc.destroyForcibly();
            state = ClientState.TERMINATED;
        }
    }

    private enum ClientState {
        PREEXECUTE,
        WAITING_FOR_SERVER,
        STANDBY_FOR_INPUT,
        TERMINATED
    }

    private final class ConsoleInputReaderThread extends Thread {
        private static final int WAIT_TIME = 100; // milliseconds to wait for input if none read

        private BufferedReader in;
        private StringBuilder sb;

        private ConsoleInputReaderThread(InputStream is) {
            in = new BufferedReader(new InputStreamReader(is));
            sb = new StringBuilder();
        }

        @Override
        public void finalize() {
            if (in == null) {
                return;
            }

            try {
                in.close();
            } catch (IOException e) {
                System.out.println("Failed to close console reader thread's reader obj");
            }
        }

        public void run() {
            try {
                while (!isInterrupted()) {
                    final String line = Functions.readLine(in, sb);

                    // wait to check input again if no complete line was read
                    if (line == null) {
                        sleep(WAIT_TIME);
                    } else {
                        consoleInLines.addLast(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // Seems bad to use try/catch for expected logic. Is this a Java issue or do I
                // need to find a better way to this? Could is be viewed as good because interrupt
                // wakes thread?
                System.out.println("Sleep was interrupted, continuing...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

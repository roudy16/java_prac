package jpclient;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.UUID;

public final class ClientOrchestrator {
    public static final ClientOrchestrator INSTANCE = new ClientOrchestrator();
    // Ensure no public instantiation
    private ClientOrchestrator() {
        clients = new HashMap<>();
        recycleIndices = new TreeSet<>();
        nextNewIndex = 0;
    }
    
    private static final String HOSTNAME = "localhost";
    private static final String PORT = "8010";

    private HashMap<Integer, ClientProcess> clients;
    private TreeSet<Integer> recycleIndices;
    private int nextNewIndex;

    public static void main (String[] args) {
        INSTANCE.launchNewClientProcess();
    }

    private void launchNewClientProcess() {
        final ProcessBuilder pb = new ProcessBuilder("java", "Client", HOSTNAME, PORT);
        final ClientProcess cproc = new ClientProcess();

        try {
            cproc.proc = pb.start();
            if (!cproc.setStdIn()) {
                // TODO some error
            }

            if (!cproc.setStdOut()) {
                // TODO some error
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

    private boolean removeProcFromMap(Integer index) {
        boolean success = false;

        final ClientProcess res = clients.remove(index);
        if (res == null) {
            System.out.println("WARNING: No process with that index found for removal, this should never happen");
            // TODO better error handling
        } else {
            recycleIndices.add(index);
            success = true;
        }
        
        return success;
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
        private ClientState state;

        private ClientProcess() {
            id = UUID.randomUUID();
            state = ClientState.PREEXECUTE;
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
    }

    private enum ClientState {
        PREEXECUTE,
        WAITING_FOR_SERVER,
        STANDBY_FOR_INPUT,
        TERMINATED
    }
}

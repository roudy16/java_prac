 This project is a client/server system. The two main classes are jpserver.Server
 and jpclient.ClientOrchestrator. There is a single server process that handles
 requests from clients. The ClientOrchestrator can be used to create many client
 processes. The ClientOrchestrator can also control the issuing of requests in
 batches. The user can specify a large number of requests and those get distributed
 among the active client processes. I wanted to do something that was somewhat
 scalable so I implemented this little scheme.

The main classes must be started as separate processes. The Server process takes
no console input, it just prints some stuff when things happen. The Client-
Orchestrator process accepts some commands to control starting and stopping of
multiple client processes and issuing requests to the server via the client
processes.


Build - On Linux I'm able to build from the project directory with:

    javac -d bin src/*/*.java

There is a Makefile with targets for building and running on most Linux distros:
    make all  - builds all the .class files
    make demo - runs a brief demo using demo_input.txt as the input
    make run  - launches main classes so system is ready to accept input from
                console running ClientOrchestrator


ClientOrchestrator Commands:  (Note there is exactly one single space between args)

spawn <int>  - launches <int>  new client processes
sort <int>   - issues <pos int> new sort requests, the request are randomly generated
show         - displays a table of info on the active client processes
kill <index> - terminates the client process <index> as shown via the "show" command
exit         - exits the ClientOrchestrator


KNOWN ISSUES:
- Issuing very large numbers of requests (20,000+) hangs on my Windows laptop but
  runs fine on my Linux desktop.
- Lacking robustness:
    - Check for processes that terminate unexpectedly
    - Timeout for network communications
    - Heartbeat for requests that may take a long time to complete
    - Watchdogs for server crashes
    - Server-side scaling
        - Currently, there is a limit to simultaneous request handling, 7 worker
          threads serve requests.

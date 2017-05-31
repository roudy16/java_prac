package jputils;

import java.io.BufferedReader;
import java.io.IOException;

public class Functions {
    // Returns the next line read from the process' stdout or null if no line could be read.
    // Stores partially read lines in the string buffer so this method can be called periodically
    // to check for complete lines.
    public static String readLine(BufferedReader reader, StringBuilder sb0) throws IOException {
        final int readAheadLimit = 2;

        int val = 0;
        String retstr = null;
        while (reader.ready()) {
            val = reader.read();

            // Stop reading if end of file seen
            if (val == -1) {
                break;
            }

            final char c = (char)val;

            // Stop reading if we find a line ending and set return string
            if (c == '\n') {
                retstr = sb0.toString();
                clearAndResizeSbIfNeeded(sb0);
                break;
            } else if (c == '\r') {
                // Check for Windows line ending, CRLF
                reader.mark(readAheadLimit);
                if (reader.read() != '\n') {
                    reader.reset();
                }

                retstr = sb0.toString();
                clearAndResizeSbIfNeeded(sb0);
                break;
            } else {
                sb0.append(c);
            }
        }

        return retstr;
    }

    // Deletes content of string buffer and reduces capacity if capacity is large
    private static void clearAndResizeSbIfNeeded(StringBuilder sb0) {
        final int limit = 512;
        final int reducedSize = 64;

        if (sb0.capacity() > limit) {
            sb0.setLength(reducedSize);
        }

        sb0.delete(0, sb0.length());
    }

}

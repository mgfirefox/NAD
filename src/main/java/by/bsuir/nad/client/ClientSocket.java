package by.bsuir.nad.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

@NoArgsConstructor
@Getter
public class ClientSocket {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 27015;

    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    public void openIfClosed() throws IOException {
        if (socket == null || socket.isClosed()) {
            open();
        }
    }

    @SneakyThrows(UnknownHostException.class)
    private void open() throws IOException {
        socket = new Socket(SERVER_IP, SERVER_PORT);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    }

    public void closeIfOpened() {
        if (socket != null) {
            close();
        }
    }

    private void close() {
        try {
            bufferedReader.close();
            printWriter.close();
            socket.close();
        } catch (IOException _) {
        }

        bufferedReader = null;
        printWriter = null;
        socket = null;
    }
}

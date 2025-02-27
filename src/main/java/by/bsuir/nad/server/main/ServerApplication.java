package by.bsuir.nad.server.main;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApplication {
    private static final int LISTENING_PORT = 27015;
    private static final int QUEUE_SIZE = 10;

    @Getter
    @Setter
    private static ApplicationContext context = null;

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        context = new ClassPathXmlApplicationContext("server-context.xml");

        try (ServerSocket serverSocket = new ServerSocket(LISTENING_PORT, QUEUE_SIZE)) {
            System.out.println("Ожидание подключения Клиента...");

            while (!serverSocket.isClosed()) {
                Socket clientSocket;
                try {
                    clientSocket = serverSocket.accept();

                    System.out.println("Клиент " + clientSocket.getInetAddress().getHostAddress() + ':' + (clientSocket.getPort()) + " успешно подключился");
                } catch (IOException | SecurityException e) {
                    e.printStackTrace();
                    System.out.println("Не удалось принять подключение Клиента: " + e.getMessage());
                    continue;
                }

                try {
                    new ClientThread(clientSocket);

                } catch (IOException e) {
                    System.out.println("Клиент " + clientSocket.getInetAddress().getHostAddress() + ':' + clientSocket.getPort() + " отключился из-за ошибки: " + e.getMessage());
                    continue;
                }
            }
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
            System.out.println("Не удалось запустить Сервер: " + e.getMessage());
        }
    }
}

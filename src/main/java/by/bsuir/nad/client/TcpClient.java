package by.bsuir.nad.client;

import by.bsuir.nad.server.db.entity.Person;
import by.bsuir.nad.server.db.entity.Person.Gender;
import by.bsuir.nad.tcp.Entity;
import by.bsuir.nad.tcp.Entity.EntityType;
import by.bsuir.nad.tcp.ServerRequest;
import by.bsuir.nad.tcp.ServerRequest.ServerRequestType;
import by.bsuir.nad.tcp.ServerResponse;
import by.bsuir.nad.tcp.ServerResponse.ServerResponseStatus;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class TcpClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 27015;

    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    private static Socket socket = null;

    private static BufferedReader in = null;
    private static PrintWriter out = null;

    public static void main(String[] args) {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //out = new PrintWriter(socket.getOutputStream(), true);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            System.out.println("Подключение к Серверу " + SERVER_IP + ":" + SERVER_PORT + " прошло успешно");

            start();
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
            System.out.println("Не удалось подключиться к Серверу: " + e.getMessage());
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                    System.out.println("Отключение от Сервера " + SERVER_IP + ":" + SERVER_PORT + " прошло успешно");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Не удалось отключиться от Сервера: " + e.getMessage());
            }
        }
    }

    private static void start() {
        try {
            while (true) {
                System.out.print("""
                        Меню администратора
                        1. Обработать данные пользователей
                        2. Обработать личные данные пользователей
                        0. Выход
                        Выбор:\s""");
                String choice = br.readLine();
                if (choice.equals("0")) {
                    break;
                }
                switch (choice) {
                    case "1" -> usersMenu();
                    case "2" -> personsMenu();
                    default -> System.out.println("Некорректный ввод данных");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Произошла ошибка ввода/вывода: " + e.getMessage());
        }
    }

    private static void usersMenu() {

    }

    private static void personsMenu() {
        try {
            while (true) {
                System.out.print("""
                        Меню администратора для обработки личных данных пользователей
                        1. Вывести личные данные пользователей
                        2. Добавить личные данные пользователя
                        3. Обновить личные данные пользователя
                        4. Удалить личные данные пользователя
                        0. Выход
                        Выбор:\s""");
                String choice = br.readLine();
                if (choice.equals("0")) {
                    break;
                }
                switch (choice) {
                    case "1" -> printPersons();
                    case "2" -> addPerson();
                    case "3" -> editPerson();
                    case "4" -> removePerson();
                    default -> System.out.println("Некорректный ввод данных");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Произошла ошибка ввода/вывода: " + e.getMessage());
        }
    }

    public static void printPersons() throws IOException {
        Entity entity = new Entity();
        entity.setType(EntityType.PERSON);

        ServerRequest request = new ServerRequest();
        request.setType(ServerRequestType.GET);
        request.setEntity(entity);
        ServerRequest.writeJson(request, out);

        ServerResponse response = ServerResponse.readJson(in);
        if (response.getStatus() != ServerResponseStatus.OK) {
            return;
        }

        entity = response.getEntity();

        List<Person> persons = Entity.PersonConverter.getList(entity);
        if (persons.isEmpty()) {
            System.out.println("Нет личных данных пользователей");
        } else {
            for (Person person : persons) {
                System.out.println(person);
            }
            System.out.println();
        }
    }

    public static void addPerson() throws IOException {
        System.out.println("Добавление личных данных");

        Entity entity = new Entity();
        Entity.PersonConverter.set(entity, getPersonData());

        ServerRequest request = new ServerRequest();
        request.setType(ServerRequestType.ADD);
        request.setEntity(entity);
        ServerRequest.writeJson(request, out);

        ServerResponse response = ServerResponse.readJson(in);
        if (response.getStatus() != ServerResponseStatus.OK) {
            return;
        }
    }

    private static void editPerson() throws IOException {
        System.out.println("Редактирование личных данных");

        Person originalPerson = findPerson();
        if (originalPerson == null) {
            return;
        }

        Person person = getPersonData();
        person.setId(originalPerson.getId());
        if (person.getLastName().isEmpty()) {
            person.setLastName(originalPerson.getLastName());
        }
        if (person.getFirstName().isEmpty()) {
            person.setFirstName(originalPerson.getFirstName());
        }
        if (person.getMiddleName().isEmpty()) {
            person.setMiddleName(originalPerson.getMiddleName());
        }
        if (person.getGender() == Gender.UNDEFINED) {
            person.setGender(originalPerson.getGender());
        }
        if (person.getPhoneNumber().isEmpty()) {
            person.setPhoneNumber(originalPerson.getPhoneNumber());
        }
        if (person.getEmail().isEmpty()) {
            person.setEmail(originalPerson.getEmail());
        }

        Entity entity = new Entity();
        Entity.PersonConverter.set(entity, person);

        ServerRequest request = new ServerRequest();
        request.setType(ServerRequestType.EDIT);
        request.setEntity(entity);
        ServerRequest.writeJson(request, out);

        ServerResponse response = ServerResponse.readJson(in);
        if (response.getStatus() != ServerResponseStatus.OK) {
            return;
        }
    }

    public static Person getPersonData() throws IOException {
        Person person = new Person();

        System.out.print("Введите фамилию: ");
        person.setLastName(br.readLine().strip());
        System.out.print("Введите имя: ");
        person.setFirstName(br.readLine().strip());
        System.out.print("Введите отчество: ");
        person.setMiddleName(br.readLine().strip());
        System.out.print("Введите пол: ");
        String gender = br.readLine().strip();
        switch (gender) {
            case "М" -> person.setGender(Gender.MALE);
            case "Ж" -> person.setGender(Gender.FEMALE);
            default -> person.setGender(Gender.UNDEFINED);
        }
        System.out.print("Введите телефон: ");
        person.setPhoneNumber(br.readLine().strip());
        System.out.print("Введите почту: ");
        person.setEmail(br.readLine().strip());

        return person;
    }

    public static void removePerson() throws IOException {
        System.out.println("Удаление личных данных");

        Person person = findPerson();
        if (person == null) {
            return;
        }

        Entity entity = new Entity();
        Entity.PersonConverter.set(entity, person);

        ServerRequest request = new ServerRequest();
        request.setType(ServerRequestType.REMOVE);
        request.setEntity(entity);
        ServerRequest.writeJson(request, out);

        ServerResponse response = ServerResponse.readJson(in);
        if (response.getStatus() != ServerResponseStatus.OK) {
            return;
        }
    }

    private static Person findPerson() throws IOException {
        System.out.print("Введите ID личных данных: ");

        Entity entity = new Entity();
        Entity.PersonConverter.setPrimaryKey(entity, Long.parseLong(br.readLine()));

        ServerRequest request = new ServerRequest();
        request.setType(ServerRequestType.FIND);
        request.setEntity(entity);
        ServerRequest.writeJson(request, out);

        ServerResponse response = ServerResponse.readJson(in);
        if (response.getStatus() != ServerResponseStatus.OK) {
            return null;
        }

        entity = response.getEntity();

        return Entity.PersonConverter.get(entity);
    }
}

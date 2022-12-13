package com.mx.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class Server extends DataBase implements ServerConfig {
    private ServerSocket serverSocket;

    private static int NUMBER_OF_CLIENTS = 0;

    public static void main(String[] args) throws SQLException {
        new Server();
    }

    public Server() throws SQLException {
        super("jdbc:mysql://localhost:3306/course_db", "root", "ivantishkevich");
        try {
            serverSocket = new ServerSocket(readConfig());
            System.out.println("Сервер запущен...");
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                NUMBER_OF_CLIENTS++;
                showServerInfo();

                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

                Thread thread = new Thread(() -> {
                    System.out.println("Клиент подключен!");
                    try {

                        while (true) {
                            String receivedStr = (String) inputStream.readObject();
                            System.out.println("Пришло: " + receivedStr);

                            manageRequest(receivedStr, inputStream, outputStream);
                        }
                    } catch (IOException | ClassNotFoundException | SQLException e) {
                        System.out.println(e.getLocalizedMessage());
                    } finally {
                        try {
                            inputStream.close();
                            outputStream.close();
                            socket.close();
                            NUMBER_OF_CLIENTS--;
                            showServerInfo();
                        } catch (IOException e) {
                        }
                    }
                });
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void manageRequest(String request, ObjectInputStream inputStream, ObjectOutputStream outputStream) throws SQLException, IOException, ClassNotFoundException {
        switch (request) {
            case "authorize" -> authorization(inputStream, outputStream);
            case "reg" -> registration(inputStream, outputStream);
            case "editPersInfo" -> editPersInfo(inputStream);
            case "showUsers" -> showUsers(outputStream);
            case "delUser" -> deleteUser(inputStream);
            case "editRole" -> editRoleUser(inputStream);
            case "showTech" -> showTech(outputStream);
            case "addTech" -> addTech(inputStream);
            case "editTech" -> editTech(inputStream);
            case "delTech" -> deleteTech(inputStream);
            case "showProviders" -> showProviders(outputStream);
            case "addProvider" -> addProvider(inputStream);
            case "editProvider" -> editProvider(inputStream);
            case "delProvider" -> delProvider(inputStream);
            case "showSupplies" -> showSupplies(outputStream);
            case "addSupply" -> addSupply(inputStream);
            case "editSupply" -> editSupply(inputStream);
            case "delSupply" -> deleteSupply(inputStream);
            case "showSelling" -> showSelling(outputStream);
            case "addSelling" -> addSale(inputStream);
            case "editSelling" -> editSelling(inputStream);
            case "acceptSupply" -> acceptSupply(inputStream);
            case "delSelling" -> delSelling(inputStream);
            case "showChart" -> showChart(inputStream, outputStream);
            case "showMonthStats" -> monthStats(inputStream, outputStream);
            case "showTopProvider" -> showTopProvider(outputStream);
            case "showSaleTech" -> showSaleTech(outputStream);
            case "showSupplyTech" -> showSupplyTech(outputStream);
        }
    }

    //Server
    private void showServerInfo() {
        System.out.println("--------СЕРВЕР--------");
        System.out.println("ПОДКЛЮЧЕННЫХ КЛИЕНТОВ: " + NUMBER_OF_CLIENTS);
        System.out.println("----------------------");
    }
}

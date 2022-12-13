package com.mx.server;

import com.mx.data.*;
import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

//класс, реализующий запросы в БД и отправку информации из БД клиенту
public class DataBase {
    private Connection connection;
    private Statement statement;

    public DataBase(String url,String user,String password) throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
        statement = connection.createStatement();
    }

    public DataBase(){
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/course_db", "root", "ivantishkevich");
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void showSupplyTech(ObjectOutputStream outputStream) throws SQLException, IOException {
        String sql = "SELECT technique.typeName,technique.brand,technique.model,technique.quantityInStock  FROM supplies" +
                " JOIN technique ON technique.techID=supplies.techID" +
                " GROUP BY supplies.techID" +
                " HAVING supplies.techID in (SELECT techID FROM technique WHERE quantityInStock<=5) AND" +
                " supplies.techID NOT in (SELECT supplies.techID FROM supplies WHERE supplies.status='Ожидается');";
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        ArrayList<Technique> arrayList = new ArrayList<>();
        while (resultSet.next()) {
            arrayList.add(new Technique(resultSet.getString(1),
                    resultSet.getString(2), resultSet.getString(3), resultSet.getInt(4)));
        }
        outputStream.writeObject(arrayList);
    }

    protected void showSaleTech(ObjectOutputStream outputStream) throws SQLException, IOException {
        String sql = "SELECT SUM(selling.quantitySold) as counts, technique.typeName,technique.brand, technique.model  FROM selling" +
                " JOIN technique ON technique.techID = selling.techID" +
                " GROUP BY selling.techID" +
                " HAVING counts >(SELECT AVG(counts) FROM (SELECT techID, SUM(quantitySold) AS counts FROM selling GROUP BY techID) a);";
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        ArrayList<Selling> arrayList = new ArrayList<>();
        while (resultSet.next()) {
            Technique technique = new Technique(resultSet.getString(2), resultSet.getString(3),
                    resultSet.getString(4));
            arrayList.add(new Selling(resultSet.getInt(1),technique));
        }
        outputStream.writeObject(arrayList);
    }

    protected void showTopProvider(ObjectOutputStream outputStream) throws SQLException, IOException {
        String sql = "SELECT providers.name, MAX(counts) FROM (" +
                " SELECT supplies.providerID, SUM(quantitySupplied*purchasePrice) as counts FROM supplies" +
                " GROUP BY supplies.providerID) a" +
                " JOIN providers ON providers.providerID=a.providerID;";
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        Providers provider = null;
        if (resultSet.next()) {
            provider = new Providers(resultSet.getString(1), resultSet.getInt(2));
        }
        outputStream.writeObject(provider);
    }

    protected void monthStats(ObjectInputStream inputStream, ObjectOutputStream outputStream) throws IOException, ClassNotFoundException, SQLException {
        String receivedString = (String) inputStream.readObject();
        String[] arr = receivedString.split(" ");
        String year = arr[0];
        String month = arr[1];

        showSellingMonth(year, month, outputStream);
        showSuppliesMonth(year, month, outputStream);
    }

    protected void showChart(ObjectInputStream inputStream, ObjectOutputStream outputStream) throws IOException, ClassNotFoundException, SQLException {
        String receivedString = (String) inputStream.readObject();
        String[] arr = receivedString.split(" ");
        String year = arr[0];
        String month = arr[1];
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(sumSalePrice(year, month));
        arrayList.add(sumPurchasePrice(year, month));

        outputStream.writeObject(arrayList);
    }

    protected int sumSalePrice(String year, String month) throws SQLException {
        String sql = "SELECT sum(salePrice*quantitySold) FROM selling WHERE YEAR(saleDate)=? AND MONTH(saleDate)=?;";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setString(1, year);
        prSt.setString(2, month);
        ResultSet resultSet = prSt.executeQuery();
        int salePrice = 0;
        if (resultSet.next()) {
            salePrice = resultSet.getInt(1);
        }
        prSt.close();
        return salePrice;
    }

    protected int sumPurchasePrice(String year, String month) throws SQLException {
        String sql = "SELECT sum(purchasePrice*quantitySupplied) FROM supplies where status='Принято' AND YEAR(supplyDate)=? AND MONTH(supplyDate)=?;";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setString(1, year);
        prSt.setString(2, month);
        ResultSet resultSet = prSt.executeQuery();
        int purchasePrice = 0;
        if (resultSet.next()) {
            purchasePrice = resultSet.getInt(1);
        }
        prSt.close();
        return purchasePrice;
    }

    //Technique
    protected void showTech(ObjectOutputStream outputStream) throws IOException, SQLException {
        String sql = "SELECT techID,typeName, brand, model, releaseYear, quantityInStock FROM technique";
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        ArrayList<Technique> arrayList = new ArrayList<>();
        while (resultSet.next()) {
            arrayList.add(new Technique(resultSet.getInt(1), resultSet.getString(2),
                    resultSet.getString(3), resultSet.getString(4), resultSet.getInt(5),
                    resultSet.getInt(6)));
        }
        outputStream.writeObject(arrayList);
    }

    protected void addTech(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException {
        Technique receivedTech = (Technique) inputStream.readObject();
        Integer receivedUserID = (Integer) inputStream.readObject();

        String sql = "INSERT INTO technique (userID, typeName, brand, model, releaseYear, quantityInStock) values (?, ?, ?, ?, ?, ?);";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setInt(1, receivedUserID);
        prSt.setString(2, receivedTech.getTypeName());
        prSt.setString(3, receivedTech.getBrand());
        prSt.setString(4, receivedTech.getModel());
        prSt.setInt(5, receivedTech.getReleaseYear());
        prSt.setInt(6, receivedTech.getQuantityInStock());
        prSt.executeUpdate();
        prSt.close();
    }

    protected void editTech(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException {
        Technique receivedTech = (Technique) inputStream.readObject();

        String sql = "UPDATE technique SET typeName=?, brand=?, model=?, releaseYear=?,quantityInStock=? WHERE techID=?";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setString(1, receivedTech.getTypeName());
        prSt.setString(2, receivedTech.getBrand());
        prSt.setString(3, receivedTech.getModel());
        prSt.setInt(4, receivedTech.getReleaseYear());
        prSt.setInt(5, receivedTech.getQuantityInStock());
        prSt.setInt(6, receivedTech.getTechID());
        prSt.executeUpdate();
        prSt.close();
    }

    protected void deleteTech(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException {
        Technique receivedTech = (Technique) inputStream.readObject();
        String sql = "DELETE FROM technique WHERE techID=?;";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setInt(1, receivedTech.getTechID());
        prSt.executeUpdate();
        prSt.close();
    }

    protected void updateQuantityTech(Technique tech) throws SQLException {
        String sql = "UPDATE technique SET quantityInStock = ? WHERE techID = ?";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setInt(1, tech.getQuantityInStock());
        prSt.setInt(2, tech.getTechID());
        prSt.executeUpdate();
        prSt.close();
    }

    protected void addQuantityTech(Technique tech) throws SQLException {
        String sql = "UPDATE technique SET quantityInStock = quantityInStock+? WHERE techID = ?";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setInt(1, tech.getQuantityInStock());
        prSt.setInt(2, tech.getTechID());
        prSt.executeUpdate();
        prSt.close();
    }

    //Providers
    protected void showProviders(ObjectOutputStream outputStream) throws IOException, SQLException {
        String sql = "SELECT* FROM providers;";
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        ArrayList<Providers> arrayList = new ArrayList<>();
        while (resultSet.next()) {
            arrayList.add(new Providers(resultSet.getInt(1), resultSet.getString(2),
                    resultSet.getString(3), resultSet.getString(4)));
        }
        outputStream.writeObject(arrayList);
    }

    protected void addProvider(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException {
        Providers receivedProvider = (Providers) inputStream.readObject();
        //Integer receivedUserID = (Integer) inputStream.readObject();

        String sql = "INSERT INTO providers (name, address, telephone) values (?, ?, ?);";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setString(1, receivedProvider.getName());
        prSt.setString(2, receivedProvider.getAddress());
        prSt.setString(3, receivedProvider.getTelephone());
        prSt.executeUpdate();
        prSt.close();
    }

    protected void editProvider(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException {
        Providers receivedProvider = (Providers) inputStream.readObject();

        String sql = "UPDATE providers SET name=?, address=?, telephone=? WHERE providerID=?";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setString(1, receivedProvider.getName());
        prSt.setString(2, receivedProvider.getAddress());
        prSt.setString(3, receivedProvider.getTelephone());
        prSt.setInt(4, receivedProvider.getProviderID());
        prSt.executeUpdate();
        prSt.close();
    }

    protected void delProvider(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException {
        Providers receivedProvider = (Providers) inputStream.readObject();
        System.out.println(receivedProvider.getProviderID());

        String sql = "DELETE FROM providers WHERE providerID=?;";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setInt(1, receivedProvider.getProviderID());
        prSt.executeUpdate();
        prSt.close();
    }

    //Supplies
    protected void showSupplies(ObjectOutputStream outputStream) throws IOException, SQLException {
        String sql = "SELECT supplies.*, technique.brand, technique.model, providers.name"
                + " FROM supplies"
                + " JOIN technique ON technique.techID = supplies.techID"
                + " JOIN providers ON providers.providerID = supplies.providerID;";
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        ArrayList<Supplies> arrayList = new ArrayList<>();
        while (resultSet.next()) {
            Technique tech = new Technique(resultSet.getInt(2), resultSet.getString(8), resultSet.getString(9));
            Providers provider = new Providers(resultSet.getInt(3), resultSet.getString(10));
            arrayList.add(new Supplies(resultSet.getInt(1), resultSet.getDate(4), resultSet.getInt(5),
                    resultSet.getInt(6), resultSet.getString(7), tech, provider));
        }
        outputStream.writeObject(arrayList);
    }

    protected void showSuppliesMonth(String year, String month, ObjectOutputStream outputStream) throws IOException, SQLException {
        String sql = "SELECT supplies.*, technique.brand, technique.model, providers.name"
                + " FROM supplies"
                + " JOIN technique ON technique.techID = supplies.techID"
                + " JOIN providers ON providers.providerID = supplies.providerID"
                + " WHERE YEAR(supplyDate)=? AND MONTH(supplyDate)=?;";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setString(1, year);
        prSt.setString(2, month);
        ResultSet resultSet = prSt.executeQuery();
        ArrayList<Supplies> arrayList = new ArrayList<>();
        while (resultSet.next()) {
            Technique tech = new Technique(resultSet.getInt(2), resultSet.getString(8), resultSet.getString(9));
            Providers provider = new Providers(resultSet.getInt(3), resultSet.getString(10));
            arrayList.add(new Supplies(resultSet.getInt(1), resultSet.getDate(4), resultSet.getInt(5),
                    resultSet.getInt(6), resultSet.getString(7), tech, provider));
        }
        outputStream.writeObject(arrayList);
    }

    protected void addSupply(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException {
        Supplies receivedSupply = (Supplies) inputStream.readObject();
        Integer receivedUserID = (Integer) inputStream.readObject();

        int techID = checkTechSupply(receivedSupply.getTechnique(), receivedUserID);

        String sql = "INSERT INTO supplies (techID, providerID, supplyDate, purchasePrice, quantitySupplied, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setInt(1, techID);
        prSt.setInt(2, receivedSupply.getProvider().getProviderID());
        prSt.setDate(3, receivedSupply.getSupplyDate());
        prSt.setInt(4, receivedSupply.getPurchasePrice());
        prSt.setInt(5, receivedSupply.getQuantitySupplied());
        prSt.setString(6, receivedSupply.getStatus());
        prSt.executeUpdate();
        prSt.close();
    }

    protected int checkTechSupply(Technique technique, Integer receivedUserID) throws SQLException {
        String sql = "SELECT techID FROM technique WHERE typeName=? AND brand=? AND model=?";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setString(1, technique.getTypeName());
        prSt.setString(2, technique.getBrand());
        prSt.setString(3, technique.getModel());
        ResultSet resultSet = prSt.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1);
        } else {
            return addTechniqueSupply(technique, receivedUserID);
        }
    }

    protected int addTechniqueSupply(Technique technique, Integer receivedUserID) throws SQLException {
        String sql = "INSERT INTO technique (userID, typeName, brand, model, releaseYear, quantityInStock) values (?, ?, ?, ?, ?, ?);";

        PreparedStatement prSt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        prSt.setInt(1, receivedUserID);
        prSt.setString(2, technique.getTypeName());
        prSt.setString(3, technique.getBrand());
        prSt.setString(4, technique.getModel());
        prSt.setInt(5, LocalDate.now().getYear());
        prSt.setInt(6, 0);
        prSt.executeUpdate();
        ResultSet keys = prSt.getGeneratedKeys();
        keys.next();
        int key = keys.getInt(1);
        return key;
    }

    protected void editSupply(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException {
        Supplies receivedSupply = (Supplies) inputStream.readObject();
        String sql = "UPDATE supplies SET supplyDate=?, purchasePrice=?, quantitySupplied=? WHERE supplyID=?";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setDate(1, receivedSupply.getSupplyDate());
        prSt.setInt(2, receivedSupply.getPurchasePrice());
        prSt.setInt(3, receivedSupply.getQuantitySupplied());
        prSt.setInt(4, receivedSupply.getSupplyID());
        prSt.executeUpdate();
        prSt.close();
    }

    protected void acceptSupply(ObjectInputStream inputStream) throws SQLException, IOException, ClassNotFoundException {
        Supplies receivedSupply = (Supplies) inputStream.readObject();
        Technique technique = receivedSupply.getTechnique();
        technique.setQuantityInStock(receivedSupply.getQuantitySupplied());
        addQuantityTech(technique);


        String sql = "UPDATE supplies SET status=? WHERE supplyID=?";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setString(1, receivedSupply.getStatus());
        prSt.setInt(2, receivedSupply.getSupplyID());
        prSt.executeUpdate();
        prSt.close();
    }

    protected void deleteSupply(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException {
        Supplies receivedTech = (Supplies) inputStream.readObject();
        String sql = "DELETE FROM supplies WHERE supplyID=?;";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setInt(1, receivedTech.getSupplyID());
        prSt.executeUpdate();
        prSt.close();
    }

    //Selling
    protected void showSelling(ObjectOutputStream outputStream) throws SQLException, IOException {
        String sql = "SELECT selling.sellingID,selling.salePrice, selling.quantitySold, selling.saleDate, technique.brand, technique.model"
                + " FROM selling JOIN technique ON technique.techID = selling.techID;";
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        ArrayList<Selling> arrayList = new ArrayList<>();
        while (resultSet.next()) {
            Technique tech = new Technique(resultSet.getString(5), resultSet.getString(6));

            arrayList.add(new Selling(resultSet.getInt(1), resultSet.getInt(2), resultSet.getInt(3),
                    resultSet.getDate(4), tech));
        }
        outputStream.writeObject(arrayList);
    }

    protected void showSellingMonth(String year, String month, ObjectOutputStream outputStream) throws SQLException, IOException, ClassNotFoundException {
        String sql = "SELECT selling.sellingID,selling.salePrice, selling.quantitySold, selling.saleDate, technique.brand, technique.model " +
                "FROM selling JOIN technique ON technique.techID = selling.techID WHERE YEAR(saleDate)=? AND MONTH(saleDate)=?;";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setString(1, year);
        prSt.setString(2, month);
        ResultSet resultSet = prSt.executeQuery();
        ArrayList<Selling> arrayList = new ArrayList<>();
        while (resultSet.next()) {
            Technique tech = new Technique(resultSet.getString(5), resultSet.getString(6));

            arrayList.add(new Selling(resultSet.getInt(1), resultSet.getInt(2), resultSet.getInt(3),
                    resultSet.getDate(4), tech));
        }
        outputStream.writeObject(arrayList);
    }

    protected void addSale(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException {
        Selling receivedSale = (Selling) inputStream.readObject();

        updateQuantityTech(receivedSale.getTechnique());

        String sql = "INSERT INTO selling (techID, salePrice, quantitySold, saleDate) values (?,?,?,?)";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setInt(1, receivedSale.getTechnique().getTechID());
        prSt.setInt(2, receivedSale.getSalePrice());
        prSt.setInt(3, receivedSale.getQuantitySold());
        prSt.setDate(4, receivedSale.getSaleDate());
        prSt.executeUpdate();
        prSt.close();
    }

    protected void editSelling(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException {
        Selling receivedSale = (Selling) inputStream.readObject();

        String sql = "UPDATE selling SET salePrice=?, saleDate=? WHERE sellingID=?";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setInt(1, receivedSale.getSalePrice());
        prSt.setDate(2, receivedSale.getSaleDate());
        prSt.setInt(3, receivedSale.getSellingID());
        prSt.executeUpdate();
        prSt.close();
    }

    protected void delSelling(ObjectInputStream inputStream) throws SQLException, IOException, ClassNotFoundException {
        Selling receivedSale = (Selling) inputStream.readObject();
        Technique technique = getTechnique(receivedSale);

        technique.setQuantityInStock(technique.getQuantityInStock() + receivedSale.getQuantitySold());
        String sql = "DELETE FROM selling WHERE sellingID=?;";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setInt(1, receivedSale.getSellingID());
        prSt.executeUpdate();
        prSt.close();

        updateQuantityTech(technique);
    }

    protected Technique getTechnique(Selling receivedSale) throws SQLException {
        String sql = "SELECT selling.techID, technique.quantityInStock FROM selling  JOIN technique ON technique.techID = selling.techID WHERE selling.sellingID =?;";

        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setInt(1, receivedSale.getSellingID());
        ResultSet resultSet = prSt.executeQuery();
        resultSet.next();
        Technique technique = new Technique(resultSet.getInt(1), resultSet.getInt(2));
        prSt.close();
        return technique;
    }

    //Auto and Reg
    protected void authorization(ObjectInputStream inputStream, ObjectOutputStream outputStream) throws IOException, ClassNotFoundException, SQLException {
        Account receivedUser = (Account) inputStream.readObject();

        String sql = "SELECT * FROM accounts WHERE login=? AND password=?;";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setString(1, receivedUser.getLogin());
        prSt.setString(2, SHA_256.hashCode(receivedUser.getPassword()));
        ResultSet result = prSt.executeQuery();

        if (result.next()) {
            outputStream.writeBoolean(true);
            Account userSend = new Account(result.getInt(1), result.getString(2), result.getString(3),
                    result.getString(4), result.getString(5), result.getString(6), result.getString(7),
                    result.getString(8), result.getString(9));

            outputStream.writeObject(userSend);
            outputStream.flush();
            prSt.close();
        } else {
            outputStream.writeBoolean(false);
            outputStream.flush();
        }
        prSt.close();
    }

    protected boolean checkLogin(String login) throws SQLException {
        String sql = "SELECT COUNT(*) FROM accounts WHERE login=?";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setString(1, login);
        ResultSet resultSet = prSt.executeQuery();
        resultSet.next();
        if (resultSet.getInt(1) == 0) return true;
        return false;
    }

    protected void registration(ObjectInputStream inputStream, ObjectOutputStream outputStream) throws IOException, ClassNotFoundException, SQLException {
        Account receivedUser = (Account) inputStream.readObject();

        if (checkLogin(receivedUser.getLogin())) {
            String sqlUsers = "INSERT INTO accounts (login, email, role, password, name, surname, address, telephone) values (?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement prdStUsers = connection.prepareStatement(sqlUsers);

            prdStUsers.setString(1, receivedUser.getLogin());
            prdStUsers.setString(2, receivedUser.getEmail());
            prdStUsers.setString(3, receivedUser.getRole());
            prdStUsers.setString(4, SHA_256.hashCode(receivedUser.getPassword()));
            prdStUsers.setString(5, receivedUser.getName());
            prdStUsers.setString(6, receivedUser.getSurname());
            prdStUsers.setString(7, receivedUser.getAddress());
            prdStUsers.setString(8, receivedUser.getTelephone());
            prdStUsers.executeUpdate();
            prdStUsers.close();

            outputStream.writeBoolean(true);
        } else {
            outputStream.writeBoolean(false);
        }
        outputStream.flush();
    }

    //Users
    protected void editPersInfo(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException {
        Account receivedUser = (Account) inputStream.readObject();

        String sqlUsers = "UPDATE accounts SET email=?, name=?, surname=?, telephone=?, address=? WHERE login=?";
        PreparedStatement prStUsers = connection.prepareStatement(sqlUsers);
        prStUsers.setString(1, receivedUser.getEmail());
        prStUsers.setString(2, receivedUser.getName());
        prStUsers.setString(3, receivedUser.getSurname());
        prStUsers.setString(4, receivedUser.getTelephone());
        prStUsers.setString(5, receivedUser.getAddress());
        prStUsers.setString(6, receivedUser.getLogin());
        prStUsers.executeUpdate();
        prStUsers.close();
    }

    protected void deleteUser(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException {
        Account receivedUser = (Account) inputStream.readObject();
        String sql = "DELETE FROM accounts WHERE login=?;";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setString(1, receivedUser.getLogin());
        prSt.executeUpdate();
        prSt.close();
    }

    protected void editRoleUser(ObjectInputStream inputStream) throws SQLException, IOException, ClassNotFoundException {
        Account receivedUser = (Account) inputStream.readObject();
        String sql = "UPDATE accounts SET role=? WHERE login=?;";
        PreparedStatement prSt = connection.prepareStatement(sql);
        prSt.setString(1, receivedUser.getRole());
        prSt.setString(2, receivedUser.getLogin());
        prSt.executeUpdate();
        prSt.close();
    }

    protected void showUsers(ObjectOutputStream outputStream) throws IOException, SQLException, ClassNotFoundException {
        String sql = "select login, email, role, name, surname, address, telephone from accounts  WHERE accounts.role != 'Admin'";
        ResultSet resultSet = statement.executeQuery(sql);
        ArrayList<Account> arrayList = new ArrayList<>();
        while (resultSet.next()) {
            arrayList.add(new Account(resultSet.getString(1), resultSet.getString(2),
                    resultSet.getString(3), resultSet.getString(4),
                    resultSet.getString(5), resultSet.getString(6), resultSet.getString(7)));
        }
        outputStream.writeObject(arrayList);
    }
}

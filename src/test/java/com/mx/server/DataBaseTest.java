package com.mx.server;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DataBaseTest {
    DataBase dataBase = new DataBase();
    final String admin = "ivan";
    final String loginAccount = "qwert12345";

    @org.junit.jupiter.api.Test
    void checkLogin() throws SQLException {
        assertEquals(dataBase.checkLogin(admin),false);
        assertEquals(dataBase.checkLogin(loginAccount),true);
    }

    @org.junit.jupiter.api.Test
    void sumSalePrice() throws SQLException {
       // assertEquals(dataBase.sumSalePrice("2022","12"), 0);
        assertTrue(dataBase.sumSalePrice("2022","11") > 0);
        assertEquals(dataBase.sumSalePrice("2022","10"),7500 );
    }

    @org.junit.jupiter.api.Test
    void sumPurchasePrice() throws SQLException {
       //  assertEquals(dataBase.sumPurchasePrice("2022","12"), 0);
        assertTrue(dataBase.sumPurchasePrice("2022","11") <= 0);
        assertEquals(dataBase.sumPurchasePrice("2022","10"), 0);
    }
}

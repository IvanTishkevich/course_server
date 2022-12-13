package com.mx.data;

import java.io.Serializable;

public class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    private int userID;
    private String login;
    private String email;
    private String role;
    private String password;
    private String name;
    private String surname;
    private String address;
    private String telephone;

    public Account(int userID, String login, String email, String role, String password, String name, String surname, String address, String telephone) {
        this.userID = userID;
        this.login = login;
        this.email = email;
        this.password = password;
        this.role = role;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.telephone = telephone;
    }

    public Account(String login, String email, String role, String name, String surname, String address, String telephone) {
        this.login = login;
        this.email = email;
        this.role = role;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.telephone = telephone;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}

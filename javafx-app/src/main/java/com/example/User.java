package com.example;

import javafx.beans.property.*;

public class User {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty email;

    public User(int id, String name, String email) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
    }

    // Getters pour les propriétés (nécessaires pour TableView)
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty emailProperty() {
        return email;
    }

    // Getters classiques
    public int getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getEmail() {
        return email.get();
    }

    // Setters classiques
    public void setId(int id) {
        this.id.set(id);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    @Override
    public String toString() {
        return "User{id=" + id.get() + ", name='" + name.get() + "', email='" + email.get() + "'}";
    }
}

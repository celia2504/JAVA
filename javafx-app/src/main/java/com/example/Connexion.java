package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connexion {
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_utilisateurs?serverTimezone=UTC";
    private static final String USER = "root"; 
    private static final String PASSWORD = ""; 
    private static Connection connection;

    // Synchronisation pour éviter les accès concurrents
    public static synchronized Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // Chargement du driver
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Erreur : Driver JDBC MySQL introuvable !", e);
            } catch (SQLException e) {
                throw new RuntimeException("Erreur de connexion à la base de données !", e);
            }
        }
        return connection;
    }

    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null; // Évite les références obsolètes
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la fermeture de la connexion !", e);
            }
        }
    }
}

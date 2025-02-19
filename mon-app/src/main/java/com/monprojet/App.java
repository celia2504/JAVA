package com.monprojet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class App {
    public static void main(String[] args) {
        // Informations de connexion
        String url = "jdbc:mysql://localhost:3306/maBase"; // Remplacer "maBase" par le nom de votre base
        String utilisateur = "root";
        String motDePasse = "";
        Connection connexion = null;

        try {
            // Établir la connexion
            connexion = DriverManager.getConnection(url, utilisateur, motDePasse);
            System.out.println("Connexion réussie !");
            
            Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nom, email FROM utilisateurs");

            System.out.println("Liste des utilisateurs :");
             // Parcours du ResultSet
             while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String email = rs.getString("email");
                System.out.println("ID : " + id + ", Nom : " + nom + ", Email : " + email);
            }
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        } finally { // Toujours fermer la connexion pour éviter les fuites de ressources 
	        if (connexion != null) { 
		        try { 
			        connexion.close(); 
			        System.out.println("Connexion fermée avec succès."); 
			    } catch (SQLException e) { 
				    System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage()); 
				} 
			} 
		}
    }
}
package com.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Optional;

public class UserManagementApp extends Application {
    private TableView<User> table = new TableView<>();
    private TextField nameField = new TextField();
    private TextField emailField = new TextField();
    private TextField searchField = new TextField();
    private Label userCountLabel = new Label("Nombre d'utilisateurs : 0");
    private Button themeButton;
    private HBox inputBox;
    private VBox layout;




    private static final String URL = "jdbc:mysql://localhost:3306/gestion_utilisateurs";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    @Override
    public void start(Stage primaryStage) {
        
        primaryStage.setTitle("Gestion des Utilisateurs");
        

        // Champs de recherche
        searchField.setPromptText("Rechercher par nom ou email...");
        Button searchButton = new Button("🔍");
        searchButton.setOnAction(e -> searchUser());

        HBox searchBox = new HBox(5, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER);
        themeButton = new Button("Mode Sombre");
        inputBox = new HBox(10);
        layout = new VBox(10);


        layout.getChildren().addAll(themeButton, searchBox, userCountLabel, table, inputBox);





        // Colonnes de la table
        TableColumn<User, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<User, String> nameColumn = new TableColumn<>("Nom");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Colonne Modifier
        TableColumn<User, Void> editColumn = new TableColumn<>("Modifier");
        editColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("✘");

            {
                editButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    showEditDialog(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        // Colonne Supprimer
        TableColumn<User, Void> deleteColumn = new TableColumn<>("Supprimer");
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("✘");

            {
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    deleteUser(user.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        table.getColumns().addAll(idColumn, nameColumn, emailColumn, editColumn, deleteColumn);

        // Champs d'ajout
        nameField.setPromptText("Nom");
        emailField.setPromptText("Email");
        Button addButton = new Button("Ajouter");
        addButton.setOnAction(e -> addUser());

        HBox inputBox = new HBox(5, nameField, emailField, addButton);
        inputBox.setAlignment(Pos.CENTER);

        // Charger les utilisateurs depuis la base de données
        loadUsersFromDatabase();
        updateUserCount(); 

        VBox layout = new VBox(10, searchBox, table, inputBox);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 600, 500);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Charger les utilisateurs depuis la base de données
    private void loadUsersFromDatabase() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String sql = "SELECT * FROM users";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
            }
            table.setItems(users);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Ajouter un utilisateur
    private void addUser() {
        String name = nameField.getText();
        String email = emailField.getText();

        if (name.isEmpty() || email.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs !");
            return;
        }

        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.executeUpdate();

            showAlert("Succès", "Utilisateur ajouté !");
            nameField.clear();
            emailField.clear();
            loadUsersFromDatabase();
            updateUserCount(); 

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ajouter l'utilisateur.");
        }
    }

    // Modifier un utilisateur
    private void showEditDialog(User user) {
        Stage editStage = new Stage();
        editStage.setTitle("Modifier Utilisateur");

        TextField nameField = new TextField(user.getName());
        TextField emailField = new TextField(user.getEmail());

        Button saveButton = new Button("Enregistrer");
        saveButton.setOnAction(e -> {
            updateUser(user.getId(), nameField.getText(), emailField.getText());
            editStage.close();
        });

        VBox layout = new VBox(10, new Label("Nom:"), nameField, new Label("Email:"), emailField, saveButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 300, 200);
        editStage.setScene(scene);
        editStage.show();
    }

    // Mettre à jour un utilisateur
    private void updateUser(int id, String name, String email) {
        String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setInt(3, id);
            stmt.executeUpdate();

            showAlert("Succès", "Utilisateur mis à jour !");
            loadUsersFromDatabase();
            updateUserCount(); 

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de mettre à jour l'utilisateur.");
        }
    }

    // Supprimer un utilisateur
    private void deleteUser(int userId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'utilisateur");
        alert.setContentText("Voulez-vous vraiment supprimer cet utilisateur ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM users WHERE id = ?";

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, userId);
                stmt.executeUpdate();

                showAlert("Succès", "Utilisateur supprimé !");
                loadUsersFromDatabase();
                updateUserCount(); 

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de supprimer l'utilisateur.");
            }
        }
    }

    // Rechercher un utilisateur
    private void searchUser() {
        String keyword = searchField.getText().trim();
        ObservableList<User> users = FXCollections.observableArrayList();
        String sql = "SELECT * FROM users WHERE name LIKE ? OR email LIKE ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
            }

            table.setItems(users);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateUserCount() {
        String sql = "SELECT COUNT(*) FROM users";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int count = rs.getInt(1);
                userCountLabel.setText("Nombre d'utilisateurs : " + count);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    public static void main(String[] args) {
        launch(args);
    }
}

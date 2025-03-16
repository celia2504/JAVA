package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.sql.*;
import java.util.Optional;

public class MainController {

    @FXML private TableView<User> tableView;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, Void> editColumn;
    @FXML private TableColumn<User, Void> deleteColumn;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField searchField;

    private static final String URL = "jdbc:mysql://localhost:3306/gestion_utilisateurs";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Charger les utilisateurs
        loadUsersFromDatabase();

        // Ajouter boutons Modifier
        editColumn.setCellFactory(createEditButtonCellFactory());

        // Ajouter boutons Supprimer
        deleteColumn.setCellFactory(createDeleteButtonCellFactory());
    }

    private void loadUsersFromDatabase() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String sql = "SELECT * FROM users";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
            }
            tableView.setItems(users);

        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les utilisateurs : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

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

        } catch (SQLException e) {
            showAlert("Erreur", "Impossible d'ajouter l'utilisateur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
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

            tableView.setItems(users);

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Callback<TableColumn<User, Void>, TableCell<User, Void>> createEditButtonCellFactory() {
        return param -> new TableCell<>() {
            private final Button editButton = new Button("✏ Modifier");

            {
                editButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    editUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editButton);
            }
        };
    }

    private Callback<TableColumn<User, Void>, TableCell<User, Void>> createDeleteButtonCellFactory() {
        return param -> new TableCell<>() {
            private final Button deleteButton = new Button("🗑 Supprimer");

            {
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        };
    }

  private void editUser(User user) {
    if (user == null) {
        System.out.println("Aucun utilisateur sélectionné !");
        return;
    }

    // Création d'une boîte de dialogue
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Modifier l'utilisateur");
    dialog.setHeaderText("Modification de : " + user.getName());

    // Création des champs de texte pré-remplis
    TextField nameField = new TextField(user.getName());
    TextField emailField = new TextField(user.getEmail());

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.add(new Label("Nom:"), 0, 0);
    grid.add(nameField, 1, 0);
    grid.add(new Label("Email:"), 0, 1);
    grid.add(emailField, 1, 1);

    dialog.getDialogPane().setContent(grid);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    // Afficher la boîte de dialogue et attendre l'entrée utilisateur
    Optional<ButtonType> result = dialog.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        String newName = nameField.getText();
        String newEmail = emailField.getText();

        // Connexion et mise à jour de la base de données
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newName);
            stmt.setString(2, newEmail);
            stmt.setInt(3, user.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Utilisateur mis à jour avec succès !");
                loadUsersFromDatabase(); // Rafraîchir la liste des utilisateurs
            } else {
                System.out.println("Échec de la mise à jour.");
            }

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }
}

    private void deleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer l'utilisateur");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer " + user.getName() + " ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String sql = "DELETE FROM users WHERE id = ?";
                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setInt(1, user.getId());
                    stmt.executeUpdate();
                    showAlert("Succès", "Utilisateur supprimé !");
                    loadUsersFromDatabase();

                } catch (SQLException e) {
                    showAlert("Erreur", "Impossible de supprimer l'utilisateur : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package ui;

import bank.PrivateBank;
import bank.exceptions.AccountAlreadyExistsException;
import bank.exceptions.AccountDoesNotExistException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MainController {

    @FXML
    private ListView<String> accountListView;

    private PrivateBank bank;

    public void setBank(PrivateBank bank) {
        this.bank = bank;
        updateListView();
    }

    private void updateListView() {
        if (bank == null) return;
        List<String> accounts = bank.getAllAccounts();
        ObservableList<String> items = FXCollections.observableArrayList(accounts);
        accountListView.setItems(items);
    }

    @FXML
    public void addAccount() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Neuer Account");
        dialog.setHeaderText("Account anlegen");
        dialog.setContentText("Bitte geben Sie den Namen des Accounts ein:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String name = result.get();
            if (name.trim().isEmpty()) return;

            try {
                bank.createAccount(name);
                updateListView();
            } catch (AccountAlreadyExistsException e) {
                showError("Fehler", "Account existiert bereits!");
            } catch (IOException e) {
                showError("Fehler", e.getMessage());
            }
        }
    }

    @FXML
    public void deleteAccountEvent() {
        String selected = accountListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Wirklich löschen?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    bank.deleteAccount(selected);
                    updateListView();
                } catch (Exception e) {
                    showError("Fehler", e.getMessage());
                }
            }
        });
    }

    @FXML
    public void viewAccountEvent() {
        String selected = accountListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AccountView.fxml"));
            Parent root = loader.load();

            AccountController controller = loader.getController();
            controller.setBankAndAccount(bank, selected);

            Stage stage = (Stage) accountListView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showError("Fehler", "Konnte Ansicht nicht laden.");
        }
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.show();
    }
}
package ui;

import bank.PrivateBank;
import bank.exceptions.AccountAlreadyExistsException;
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
/**
 * Controller für die MainView.
 * Hier werden alle Accounts angezeigt und man kann neue erstellen oder löschen.
 * Verbindet das FXML-UI mit dem Bankmodell.
 */
public class MainController {

    /**
     * ListView für die Konten. Wird via FXML gemappt.
     */
    @FXML
    private ListView<String> accountListView;

    /**
     * Bankmodell, in dem alle Konten und Transaktionen liegen.
     */
    private PrivateBank bank;

    /**
     * Setter für das Bankmodell. Muss aufgerufen werden bevor man irgendwas macht.
     * Ruft direkt updateListView auf, damit UI up to date ist.
     *
     * @param bank das PrivateBank-Objekt
     */
    public void setBank(PrivateBank bank) {
        this.bank = bank;
        updateListView();
    }

    /**
     * Aktualisiert die ListView mit allen Konten aus der Bank.
     * ObservableList wird benutzt, damit die ListView direkt die Daten kennt.
     */
    private void updateListView() {
        if (bank == null) return;
        List<String> accounts = bank.getAllAccounts();
        ObservableList<String> items = FXCollections.observableArrayList(accounts);
        accountListView.setItems(items);
    }

    /**
     * Fügt einen neuen Account hinzu.
     * Zeigt zuerst ein TextInputDialog, User gibt Namen ein.
     * Wenn Account schon existiert, wird ein Fehler gezeigt.
     */
    @FXML
    public void addAccount() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Neuen Account hinzufügen");
        dialog.setHeaderText("Account anlegen");
        dialog.setContentText("Bitte geben Sie den Namen des Accounts ein:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String name = result.get();
            if (name.trim().isEmpty()) return; // nix eingegeben -> abbrechen

            try {
                bank.createAccount(name);
                updateListView(); // UI updaten
            } catch (AccountAlreadyExistsException e) {
                showError("Fehler", "Account existiert bereits!");
            } catch (IOException e) {
                showError("Fehler", e.getMessage());
            }
        }
    }

    /**
     * Löscht den selektierten Account.
     * Zeigt vorher ein Bestätigungsdialog.
     * Wenn nix selektiert ist passiert einfach nix.
     */
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

    /**
     * Öffnet die Detailansicht für den selektierten Account.
     * Lädt AccountView.fxml, holt den Controller und setzt Bank + Account.
     * Dann wird die Scene gewechselt, ohne das Fenster zu schließen.
     */
    @FXML
    public void viewAccountEvent() {
        String selected = accountListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AccountView.fxml"));
            Parent root = loader.load();

            AccountController controller = loader.getController();
            controller.setBankAndAccount(bank, selected);

            Stage stage = (Stage) accountListView.getScene().getWindow(); // wichtig um die Stage zu holen
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showError("Fehler", "Konnte Ansicht nicht laden.");
        }
    }

    /**
     * Zeigt einen simplen Error-Dialog.
     * @param title Titel des Dialogs
     * @param msg Nachricht die angezeigt werden soll
     */
    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.show(); // blockiert nicht, User kann weitermachen
    }
}

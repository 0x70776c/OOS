package ui;

import bank.PrivateBank;
import bank.Transaction;
import bank.Payment;
import bank.Transfer;
import bank.exceptions.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AccountController {

    @FXML
    private ListView<Transaction> transactionListView;

    @FXML
    private Text balanceText;

    private PrivateBank bank;
    private String selectedAccount;
    /**
     * Setzt das Bankmodell und den aktuell ausgewählten Account.
     * Muss gemacht werden bevor man irgendwas mit Transaktionen macht,
     * sonst funktioniert updateView() nicht richtig.
     *
     * @param bank das Bankobjekt, wo alle Konten und Transaktionen drin sind
     * @param account der Name des aktuell ausgewählten Accounts
     */
    public void setBankAndAccount(PrivateBank bank, String account) {
        this.bank = bank;
        this.selectedAccount = account;
        updateView();
    }

    /**
     * Aktualisiert die View: TransactionList und Balance werden frisch aus der Bank geholt.
     * Alte Daten werden komplett gelöscht und neue reingeladen.
     * Achtung: Wenn Account nicht existiert, gibt's einen Fehlerdialog.
     */
    private void updateView() {
        try {
            List<Transaction> daten = bank.getTransactions(selectedAccount);
            transactionListView.getItems().clear();
            transactionListView.getItems().addAll(daten);
            double balance = bank.getAccountBalance(selectedAccount);
            balanceText.setText("Kontostand: " + balance + " €");
        } catch (AccountDoesNotExistException e) {
            showError("Fehler", "Konto existiert nicht mehr.");
        }
    }

    /**
     * Wechselt zurück zur MainView.
     * Lädt die MainView FXML und setzt die Bank im MainController.
     * @param event das ActionEvent von dem Button-Klick
     */
    @FXML
    public void setMainView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();
            MainController controller = loader.getController();
            controller.setBank(bank);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Löscht die selektierte Transaktion.
     * Zeigt vorher ein Bestätigungsdialog, falls nix selektiert ist passiert nichts.
     */
    @FXML
    public void deleteTransactionEvent() {
        Transaction selected = transactionListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Löschen?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                bank.removeTransaction(selectedAccount, selected);
                updateView();
            } catch (Exception e) {
                showError("Fehler", e.getMessage());
            }
        }
    }

    /**
     * Holt alle Transaktionen in aufsteigender Reihenfolge und zeigt sie in der ListView.
     */
    @FXML
    public void getAscendingTransactions() {
        List<Transaction> sorted = bank.getTransactionsSorted(selectedAccount, true);
        transactionListView.getItems().clear();
        transactionListView.getItems().addAll(sorted);
    }

    /**
     * Holt alle Transaktionen in absteigender Reihenfolge und zeigt sie in der ListView.
     */
    @FXML
    public void getDescendingTransactions() {
        List<Transaction> sorted = bank.getTransactionsSorted(selectedAccount, false);
        transactionListView.getItems().clear();
        transactionListView.getItems().addAll(sorted);
    }

    /**
     * Zeigt nur positive Transaktionen an.
     * Catch block fängt alle Exceptions vom Model.
     */
    @FXML
    public void getPositiveTransactions() {
        try {
            List<Transaction> filtered = bank.getTransactionsByType(selectedAccount, true);
            transactionListView.getItems().clear();
            transactionListView.getItems().addAll(filtered);
        } catch (Exception e) { showError("Fehler", e.getMessage()); }
    }

    /**
     * Zeigt nur negative Transaktionen an.
     * Ganz ähnlich wie getPositiveTransactions.
     */
    @FXML
    public void getNegativeTransactions() {
        try {
            List<Transaction> filtered = bank.getTransactionsByType(selectedAccount, false);
            transactionListView.getItems().clear();
            transactionListView.getItems().addAll(filtered);
        } catch (Exception e) { showError("Fehler", e.getMessage()); }
    }

    /**
     * Fügt eine neue Transaktion hinzu.
     * Zeigt erst ein ChoiceDialog, damit der User Payment oder Transfer wählen kann.
     */
    @FXML
    public void addTransaction() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Payment", "Payment", "Transfer");
        dialog.setHeaderText("Typ wählen");
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (result.get().equals("Payment")) createPayment();
            else createTransfer();
        }
    }

    /**
     * Öffnet ein Dialog zum Erstellen eines Payments.
     * Alle Eingaben werden als TextField gemacht, Betrag muss in Double umgewandelt werden.
     */
    private void createPayment() {
        Dialog<Payment> dialog = new Dialog<>();
        dialog.setHeaderText("Payment anlegen");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        TextField desc = new TextField(); desc.setPromptText("Beschreibung");
        TextField date = new TextField(); date.setPromptText("Datum");
        TextField amount = new TextField(); amount.setPromptText("Betrag");

        grid.addRow(0, new Label("Beschreibung:"), desc);
        grid.addRow(1, new Label("Datum:"), date);
        grid.addRow(2, new Label("Betrag:"), amount);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    double val = Double.parseDouble(amount.getText());
                    return new Payment(date.getText(), val, desc.getText(), 0, 0);
                } catch (Exception e) { return null; }
            }
            return null;
        });

        Optional<Payment> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                bank.addTransaction(selectedAccount, result.get());
                updateView();
            } catch (Exception e) { showError("Fehler", e.getMessage()); }
        }
    }

    /**
     * Öffnet ein Dialog zum Erstellen eines Transfers.
     * Sender ist automatisch der aktuelle Account, Empfänger muss der User eingeben.
     */
    private void createTransfer() {
        Dialog<Transfer> dialog = new Dialog<>();
        dialog.setHeaderText("Transfer anlegen");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        TextField desc = new TextField(); desc.setPromptText("Beschreibung");
        TextField date = new TextField(); date.setPromptText("Datum");
        TextField amount = new TextField(); amount.setPromptText("Betrag");
        TextField sender = new TextField(); sender.setText(selectedAccount);
        TextField recipient = new TextField(); recipient.setPromptText("Empfänger");

        grid.addRow(0, new Label("Beschreibung:"), desc);
        grid.addRow(1, new Label("Datum:"), date);
        grid.addRow(2, new Label("Betrag:"), amount);
        grid.addRow(3, new Label("Sender:"), sender);
        grid.addRow(4, new Label("Empfänger:"), recipient);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    double val = Double.parseDouble(amount.getText());
                    return new Transfer(date.getText(), val, desc.getText(), sender.getText(), recipient.getText());
                } catch (Exception e) { return null; }
            }
            return null;
        });

        Optional<Transfer> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                bank.addTransaction(selectedAccount, result.get());
                updateView();
            } catch (Exception e) { showError("Fehler", e.getMessage()); }
        }
    }

    /**
     * Zeigt ein Fehler-Alert an.
     * Ganz simpel, nix kompliziert.
     * @param title Titel des Dialogs
     * @param msg Nachricht, die angezeigt werden soll
     */
    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.show();
    }

}
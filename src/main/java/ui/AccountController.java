package ui;

import bank.PrivateBank;
import bank.Transaction;
import bank.Payment;
import bank.Transfer;
import bank.exceptions.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

/**
 * Controller für die AccountView (Detailansicht).
 */
public class AccountController {

    @FXML
    private ListView<Transaction> transactionListView;

    @FXML
    private Text balanceText;

    private PrivateBank bank;
    private String selectedAccount;

    /**
     * WICHTIG: Datenübergabe vom MainController.
     * Ersetzt "initialize" und Konstruktoren.
     */
    public void setBankAndAccount(PrivateBank bank, String account) {
        this.bank = bank;
        this.selectedAccount = account;
        updateView();
    }

    /**
     * Aktualisiert Liste und Kontostand.
     */
    private void updateView() {
        try {
            updateList(bank.getTransactions(selectedAccount));

            double balance = bank.getAccountBalance(selectedAccount);
            balanceText.setText(String.format("Balance: %.2f €", balance));

        } catch (AccountDoesNotExistException e) {
            showError("Fehler", "Konto existiert nicht mehr.");
        }
    }

    private void updateList(List<Transaction> list) {
        ObservableList<Transaction> items = FXCollections.observableArrayList(list);
        transactionListView.setItems(items);
    }

    // --- FXML Event Handler (Namen exakt wie in deiner FXML) ---

    @FXML
    public void setMainView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();

            // Bank zurückgeben an den MainController (damit der Zustand erhalten bleibt)
            MainController controller = loader.getController();
            controller.setBank(bank);

            // Szene wechseln
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showError("Fehler", "Konnte nicht zurück wechseln: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteTransactionEvent() {
        Transaction selected = transactionListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Hinweis", "Keine Transaktion ausgewählt.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Wirklich löschen?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    bank.removeTransaction(selectedAccount, selected);
                    updateView(); // Liste & Saldo neu laden
                } catch (Exception e) {
                    showError("Fehler", e.getMessage());
                }
            }
        });
    }

    // --- Sortierung & Filterung ---

    @FXML
    public void getAscendingTransactions() {
        try {
            updateList(bank.getTransactionsSorted(selectedAccount, true));
        } catch (Exception e) { showError("Fehler", e.getMessage()); }
    }

    @FXML
    public void getDescendingTransactions() {
        try {
            updateList(bank.getTransactionsSorted(selectedAccount, false));
        } catch (Exception e) { showError("Fehler", e.getMessage()); }
    }

    @FXML
    public void getPositiveTransactions() {
        try {
            updateList(bank.getTransactionsByType(selectedAccount, true));
        } catch (Exception e) { showError("Fehler", e.getMessage()); }
    }

    @FXML
    public void getNegativeTransactions() {
        try {
            updateList(bank.getTransactionsByType(selectedAccount, false));
        } catch (Exception e) { showError("Fehler", e.getMessage()); }
    }

    // --- Hinzufügen (Dialoge) ---

    @FXML
    public void addTransaction() {
        // 1. Typ auswählen
        ChoiceDialog<String> typeDialog = new ChoiceDialog<>("Payment", "Payment", "Transfer");
        typeDialog.setTitle("Typ wählen");
        typeDialog.setHeaderText("Neue Transaktion");
        typeDialog.setContentText("Typ:");

        Optional<String> result = typeDialog.showAndWait();
        if (result.isPresent()) {
            if (result.get().equals("Payment")) {
                showAddPaymentDialog();
            } else {
                showAddTransferDialog();
            }
        }
    }

    private void showAddPaymentDialog() {
        Dialog<Payment> dialog = new Dialog<>();
        dialog.setTitle("Neues Payment");
        dialog.setHeaderText("Daten eingeben");

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

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
            if (btn == okButton) {
                try {
                    double val = Double.parseDouble(amount.getText());
                    return new Payment(date.getText(), val, desc.getText(), 0, 0);
                } catch (Exception e) { return null; }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(p -> {
            try {
                bank.addTransaction(selectedAccount, p);
                updateView();
            } catch (Exception e) { showError("Fehler", e.getMessage()); }
        });
    }

    private void showAddTransferDialog() {
        Dialog<Transfer> dialog = new Dialog<>();
        dialog.setTitle("Neuer Transfer");
        dialog.setHeaderText("Daten eingeben");

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        TextField desc = new TextField();
        TextField date = new TextField();
        TextField amount = new TextField();
        TextField sender = new TextField(); sender.setText(selectedAccount);
        TextField recipient = new TextField();

        grid.addRow(0, new Label("Beschreibung:"), desc);
        grid.addRow(1, new Label("Datum:"), date);
        grid.addRow(2, new Label("Betrag:"), amount);
        grid.addRow(3, new Label("Sender:"), sender);
        grid.addRow(4, new Label("Empfänger:"), recipient);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == okButton) {
                try {
                    double val = Double.parseDouble(amount.getText());
                    return new Transfer(date.getText(), val, desc.getText(), sender.getText(), recipient.getText());
                } catch (Exception e) { return null; }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(t -> {
            try {
                bank.addTransaction(selectedAccount, t);
                updateView();
            } catch (Exception e) { showError("Fehler", e.getMessage()); }
        });
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.show();
    }
}
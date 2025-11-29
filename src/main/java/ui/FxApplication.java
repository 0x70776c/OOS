package ui;

import bank.PrivateBank;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class FxApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. FXML-Loader vorbereiten (Pfad zur Datei in resources)
        // Achte auf den Slash am Anfang!
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        Parent root = loader.load();

        // 2. Backend initialisieren (PrivateBank)
        PrivateBank bank = null;
        try {
            // HIER DEINEN PFAD ANPASSEN!
           // bank = new PrivateBank("Meine Bank", 0.05, 0.03, "/Users/pawel/Desktop/UNI/3semesteer/oos/p2/JSON");
            bank = new PrivateBank("Meine Bank", 0.05, 0.03, "C:\\Users\\legue\\IdeaProjects\\oos\\src\\main\\java\\json");
        } catch (IOException e) {
            showError("Startfehler", "Konnte Bankdaten nicht laden: " + e.getMessage());
            return; // Abbruch, wenn die Bank nicht geladen werden kann
        }

        // 3. Controller holen und Bank übergeben (Dependency Injection)
        MainController controller = loader.getController();
        controller.setBank(bank);

        // 4. Fenster anzeigen
        Scene scene = new Scene(root, 400, 500);
        primaryStage.setTitle("Private Bank Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
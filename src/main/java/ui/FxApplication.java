package ui;

import bank.PrivateBank;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
/**
 * Die Hauptklasse für die JavaFX Applikation.
 * Startet das Fenster, lädt die MainView FXML und erzeugt das Bankmodell.
 * Erbt von Application, daher wird start() automatisch von JavaFX aufgerufen.
 */
public class FxApplication extends Application {

    /**
     * Start-Methode von JavaFX. Hier wird alles initialisiert.
     * Lädt das FXML, erstellt das Bankmodell und setzt die Scene ins Fenster.
     *
     * @param primaryStage das Hauptfenster, welches JavaFX bereitstellt
     * @throws Exception falls irgendwas beim Laden schief geht
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Loader für FXML, lädt das Design vom Fenster
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        Parent root = loader.load(); // object mit fenster design

        // Bankmodell erstellen
        PrivateBank bank = null;
        try {
            bank = new PrivateBank(
                    "Bank 1",   // Name der Bank
                    0.05,       // Zinssatz für Einlagen
                    0.03,       // Zinssatz für Kredite
                    "/Users/pawel/Desktop/UNI/3semesteer/oos/p2/JSON" // Pfad für Daten
            );
            // Für Windows könnte man den Pfad so schreiben:
            // "C:\\Users\\legue\\IdeaProjects\\oos\\src\\main\\java\\json"
        } catch (IOException e) {
            showError("Startfehler", "Konnte Bankdaten nicht laden: " + e.getMessage());
            return; // wenn Bank nicht geladen wird, abbrechen
        }

        // Controller aus FXML holen und Bank setzen
        MainController controller = loader.getController(); // wird beim FXML-Load erzeugt
        controller.setBank(bank); // Bank ins Controller-Objekt übergeben

        // Scene erstellen und ins Stage setzen
        Scene scene = new Scene(root, 590, 410); // Größe: 590x410
        primaryStage.setTitle("Private Bank Manager"); // Fenster-Titel
        primaryStage.setScene(scene); // Scene ins Stage packen
        primaryStage.show(); // Fenster anzeigen
    }

    /**
     * Zeigt einen simplen Error-Dialog an.
     * Wird benutzt, wenn etwas schief geht (z.B. Bankdaten nicht ladbar)
     *
     * @param title Überschrift des Fehlers
     * @param msg die eigentliche Nachricht, was schief ging
     */
    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait(); // blockiert bis User OK drückt
    }

    /**
     * Main-Methode. Startet die JavaFX Applikation.
     * launch() ruft intern start() auf.
     *
     * @param args Kommandozeilenargumente, werden hier nicht benutzt
     */
    public static void main(String[] args) {
        launch(args); // JavaFX starten
    }
}

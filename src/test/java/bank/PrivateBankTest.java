package bank;

import bank.exceptions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

// Importe für Datei-Operationen (P4)
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 Testklasse für die PrivateBank-Klasse (P4, A3).
 * Diese Klasse testet die P3-Logik SOWIE die P4-Persistierungslogik.
 */
@DisplayName("PrivateBank Tests (P3-Logik & P4-Speicherung)")
public class PrivateBankTest {

    // WICHTIG: Dein Test-Pfad
    private static final String TEST_DIRECTORY = "/Users/pawel/Desktop/UNI/3semesteer/oos/p2/JSON";

    private PrivateBank bank;
    private Transaction paymentIn;    // 1000 (calc 950)
    private Transaction paymentOut;   // -100 (calc -103)
    private Transaction transferOut;  // 50 (calc -50)

    /**
     * @BeforeEach (init-Methode)
     * Läuft vor JEDEM Test.
     * Stellt sicher, dass wir für jeden Test eine frische Bank-Instanz haben.
     */
    @BeforeEach
    public void init() throws IOException {
        // Erstellt die Bank-Instanz.
        // Der Konstruktor ruft readAccounts() auf (das Verzeichnis wird erstellt, falls nicht vorhanden).
        bank = new PrivateBank("TestBank", 0.05, 0.03, TEST_DIRECTORY);

        // Standard-Testdaten initialisieren
        paymentIn = new Payment("01.01.2025", 1000, "Gehalt", 0, 0);
        paymentOut = new Payment("02.01.2025", -100, "Miete", 0, 0);

        // Dieser Transfer wird als "ausgehend" (Outgoing) interpretiert,
        // wenn er zum Konto "KontoA" hinzugefügt wird.
        transferOut = new Transfer("03.01.2025", 50, "Strom", "KontoA", "KontoB");
    }

    /**
     * @AfterEach (Aufräum-Methode)
     * Läuft nach JEDEM Test.
     * (Wie in A3 gefordert: Löscht die persistierten Dateien)
     * Stellt sicher, dass der nächste Test ein leeres Verzeichnis vorfindet.
     */
    @AfterEach
    public void cleanup() throws IOException {
        Path dirPath = Paths.get(TEST_DIRECTORY);

        if (!Files.exists(dirPath)) {
            return;
        }

        // Lösche alle Dateien im Verzeichnis
        try (var files = Files.list(dirPath)) {
            files.filter(p -> p.toString().endsWith(".json"))
                    .forEach(filePath -> {
                        try {
                            Files.delete(filePath);
                        } catch (IOException e) {
                            System.err.println("Fehler beim Löschen der Datei: " + filePath);
                        }
                    });
        }

        // (Optional: Verzeichnis selbst löschen)
        // Files.delete(dirPath);
    }

    /**
     * Testet den P4-Konstruktor und die readAccounts()-Logik (beim Start).
     */
    @Test
    @DisplayName("P4-Konstruktor: Bank-Instanz und Verzeichnis korrekt erstellt")
    public void testConstructor() {
        assertNotNull(bank, "Bank-Objekt sollte nicht null sein");
        assertEquals("TestBank", bank.getName());
        assertEquals(TEST_DIRECTORY, bank.getDirectoryName());

        // Prüft, ob der Konstruktor (via readAccounts()) die Map korrekt (leer) initialisiert hat
        assertTrue(bank.getAccountsToTransactions().isEmpty(), "Map sollte beim Start leer sein");
        // Prüft, ob das Verzeichnis physisch erstellt wurde
        assertTrue(Files.exists(Paths.get(TEST_DIRECTORY)), "Test-Verzeichnis wurde nicht erstellt");
    }

    /**
     * Testet createAccount(String) (Happy Path)
     * Testet, ob writeAccount() aufgerufen wurde.
     */
    @Test
    @DisplayName("createAccount(String) erstellt Konto und JSON-Datei")
    public void testCreateAccount() throws IOException {
        // 1. Ausführen (ohne Exception)
        assertDoesNotThrow(() -> bank.createAccount("KontoA"),
                "createAccount sollte keine IOException werfen");

        // 2. Zustand in der Map prüfen
        assertTrue(bank.getAccountsToTransactions().containsKey("KontoA"), "Konto sollte in der Map sein");

        // 3. P4-TEST: Zustand auf der Festplatte prüfen
        Path filePath = Paths.get(TEST_DIRECTORY, "KontoA.json");
        assertTrue(Files.exists(filePath), "JSON-Datei wurde nicht erstellt");
    }

    /**
     * Testet createAccount(String) (Exception-Fall)
     * (Wie in A3 gefordert: assertThrows)
     */
    @Test
    @DisplayName("createAccount(String) wirft Exception bei Duplikat")
    public void testCreateAccountThrowsException() throws IOException, AccountAlreadyExistsException {
        // Setup: Konto erstellen
        bank.createAccount("KontoA");

        // Test: Erneut erstellen
        assertThrows(AccountAlreadyExistsException.class, () -> {
            bank.createAccount("KontoA");
        }, "Sollte AccountAlreadyExistsException werfen");
    }

    /**
     * Testet addTransaction() (Happy Path)
     * Testet, ob writeAccount() die Datei aktualisiert.
     */
    @Test
    @DisplayName("addTransaction() fügt Transaktion hinzu und speichert")
    public void testAddTransaction() throws Exception { // Wirft viele Exceptions
        bank.createAccount("KontoA");

        // Ausführen
        assertDoesNotThrow(() -> bank.addTransaction("KontoA", paymentIn),
                "addTransaction sollte keine IOException werfen");

        // Zustand in der Map prüfen
        assertEquals(1, bank.getTransactions("KontoA").size());
        assertTrue(bank.containsTransaction("KontoA", paymentIn), "Konto sollte Payment enthalten");
    }

    /**
     * Testet die P3-Logik in addTransaction (Umwandlung in OutgoingTransfer)
     */
    @Test
    @DisplayName("P3-Logik: addTransaction wandelt Transfer korrekt um")
    public void testAddTransactionP3Logic() throws Exception {
        bank.createAccount("KontoA");

        // 'transferOut' hat 'KontoA' als SENDER
        bank.addTransaction("KontoA", transferOut);

        List<Transaction> transactions = bank.getTransactions("KontoA");

        // Prüfen, ob das Objekt in der Liste jetzt ein OutgoingTransfer ist
        assertTrue(transactions.get(0) instanceof OutgoingTransfer,
                "Transaktion sollte zu OutgoingTransfer umgewandelt worden sein");
    }

    /**
     * Testet die P3-Logik in getAccountBalance()
     */
    @Test
    @DisplayName("getAccountBalance() berechnet Saldo korrekt (P3-Logik)")
    public void testGetAccountBalance() throws Exception {
        bank.createAccount("KontoA");

        // paymentIn (1000) -> calc() = 1000 * (1 - 0.05) = 950
        bank.addTransaction("KontoA", paymentIn);

        // paymentOut (-100) -> calc() = -100 * (1 + 0.03) = -103
        bank.addTransaction("KontoA", paymentOut);

        // transferOut (50) -> wird zu OutgoingTransfer -> calc() = -50
        bank.addTransaction("KontoA", transferOut);

        // Erwartet: 950 - 103 - 50 = 797.0
        assertEquals(797.0, bank.getAccountBalance("KontoA"),
                "Saldo (inkl. Zinsen und Transfer-Logik) ist falsch berechnet");
    }

    /**
     * Testet removeTransaction() (Happy Path)
     * Testet, ob writeAccount() die Datei aktualisiert.
     */
    @Test
    @DisplayName("removeTransaction() entfernt Transaktion und speichert")
    public void testRemoveTransaction() throws Exception {
        bank.createAccount("KontoA");
        bank.addTransaction("KontoA", paymentIn);

        // Zustand vorher prüfen
        assertEquals(1, bank.getTransactions("KontoA").size());

        // Ausführen
        assertDoesNotThrow(() -> bank.removeTransaction("KontoA", paymentIn),
                "removeTransaction sollte keine IOException werfen");

        // Zustand nachher prüfen
        assertEquals(0, bank.getTransactions("KontoA").size(), "Transaktion wurde nicht entfernt");
    }

    /**
     * Testet die Exceptions für add/remove Transaction
     * (Wie in A3 gefordert: assertThrows)
     */
    @Test
    @DisplayName("Exceptions für add/remove (AccountDoesNotExist, TransactionExists, ...)")
    public void testTransactionExceptions() throws Exception {
        bank.createAccount("KontoA");
        bank.addTransaction("KontoA", paymentIn);

        // AccountDoesNotExistException
        assertThrows(AccountDoesNotExistException.class, () -> {
            bank.addTransaction("Konto-GIBTSNICHT", paymentOut);
        });

        // TransactionAlreadyExistException
        assertThrows(TransactionAlreadyExistException.class, () -> {
            bank.addTransaction("KontoA", paymentIn);
        });

        // TransactionDoesNotExistException
        assertThrows(TransactionDoesNotExistException.class, () -> {
            bank.removeTransaction("KontoA", paymentOut); // paymentOut wurde nie hinzugefügt
        });
    }

    /**
     * Testet die Anforderung von P4, A3: @ParameterizedTest
     * Wir testen die attributeValidation() (die von addTransaction genutzt wird).
     */
    @ParameterizedTest(name = "Ungültiger Betrag: {0}")
    @ValueSource(doubles = {0.0, -10.0, -50.0}) // Testet 0 und negative Werte
    @DisplayName("P4-A3: Parametrisierter Test für TransactionAttributeException")
    public void testAttributeValidationException(double invalidAmount) throws IOException, AccountAlreadyExistsException {
        bank.createAccount("KontoA");

        // Test 1: Payment mit Betrag 0
        if (invalidAmount == 0.0) {
            Payment p = new Payment("Datum", invalidAmount, "Betrag 0", 0.1, 0.1);
            assertThrows(TransactionAttributeException.class, () -> {
                bank.addTransaction("KontoA", p);
            });
        }

        // Test 2: Transfer mit negativem Betrag
        if (invalidAmount < 0.0) {
            Transfer t = new Transfer("Datum", invalidAmount, "Negativ", "A", "B");
            assertThrows(TransactionAttributeException.class, () -> {
                bank.addTransaction("KontoA", t);
            });
        }
    }

    /**
     * Testet die P4-Persistenz (readAccounts)
     */
    @Test
    @DisplayName("P4-Persistenz: Konten werden korrekt geladen (readAccounts)")
    public void testPersistenceRead() throws Exception {
        // 1. Setup: Konto erstellen und Transaktion hinzufügen
        bank.createAccount("KontoPersist");
        bank.addTransaction("KontoPersist", paymentIn);

        // Sicherstellen, dass die Daten in der Map sind
        assertEquals(1, bank.getTransactions("KontoPersist").size());

        // 2. Test: EINE NEUE Bank-Instanz erstellen
        // Diese MUSS den Zustand (dank readAccounts() im Konstruktor) laden.
        PrivateBank bank2 = new PrivateBank("ZweiteBank", 0.05, 0.03, TEST_DIRECTORY);

        // 3. Prüfen: Sind die Daten in der neuen Instanz?
        assertEquals(1, bank2.getTransactions("KontoPersist").size(),
                "Bank 2 hat die Transaktionen nicht aus der JSON-Datei geladen");
        assertTrue(bank2.containsTransaction("KontoPersist", paymentIn),
                "Bank 2 hat die korrekte Transaktion nicht geladen");
    }

    /**
     * Testet die Lese-Methoden (getTransactionsSorted, getTransactionsByType)
     */
    @Test
    @DisplayName("Lese-Methoden: getTransactionsSorted / getTransactionsByType")
    public void testReadMethods() throws Exception {
        bank.createAccount("KontoA");
        bank.addTransaction("KontoA", paymentIn);  // +950
        bank.addTransaction("KontoA", paymentOut); // -103
        bank.addTransaction("KontoA", transferOut); // -50

        // Test getTransactionsByType
        List<Transaction> positive = bank.getTransactionsByType("KontoA", true);
        List<Transaction> negative = bank.getTransactionsByType("KontoA", false);

        assertEquals(1, positive.size(), "Sollte 1 positive Transaktion finden");
        assertEquals(950.0, positive.get(0).calculate());
        assertEquals(2, negative.size(), "Sollte 2 negative Transaktionen finden");

        // Test getTransactionsSorted (absteigend)
        List<Transaction> sortedDesc = bank.getTransactionsSorted("KontoA", false);
        assertEquals(950.0, sortedDesc.get(0).calculate(), "Größte (950) sollte an erster Stelle sein");
        assertEquals(-103.0, sortedDesc.get(2).calculate(), "Kleinste (-103) sollte an letzter Stelle sein");
    }

    /**
     * Testet die equals()-Methode (P4-Anforderung)
     */
    @Test
    @DisplayName("equals()")
    public void testEquals() throws IOException, AccountAlreadyExistsException {
        // Erstellt eine zweite Bank, die exakt identisch ist (gleicher Pfad, liest dieselben (leeren) Dateien)
        PrivateBank bank2 = new PrivateBank("TestBank", 0.05, 0.03, TEST_DIRECTORY);

        // 1. Test auf Identität (leer)
        assertEquals(bank, bank2, "Zwei leere Banken mit gleichem Pfad sollten gleich sein");

        // 2. Eine Bank ändern
        bank.createAccount("KontoDiff");

        // 3. Test auf Ungleichheit
        assertNotEquals(bank, bank2, "Banken sollten nach Änderung ungleich sein");
    }
}
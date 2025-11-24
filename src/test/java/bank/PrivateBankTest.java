package bank;

import bank.exceptions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Testklasse für die PrivateBank.
 * Enthält grundlegende Funktionstests.
 */
public class PrivateBankTest {

    private static final String TEST_DIRECTORY = "/Users/pawel/Desktop/UNI/3semesteer/oos/p2/JSON";

    private PrivateBank bank;

    private Transaction paymentIn, paymentOut, transferOut;

    @BeforeEach
    public void init() throws IOException {
        bank = new PrivateBank("TestBank", 0.05, 0.03, TEST_DIRECTORY);
        paymentIn = new Payment("01.01.2025", 1000, "Gehalt", 0, 0);
        paymentOut = new Payment("02.01.2025", -100, "Miete", 0, 0);
        transferOut = new Transfer("03.01.2025", 50, "Strom", "KontoA", "KontoB");
    }

    @AfterEach
    public void cleanup() {
        File dir = new File(TEST_DIRECTORY);
        if (!dir.exists()) return;
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.getName().endsWith(".json")) file.delete();
        }
    }

    /**
     * Testet das Erstellen eines neuen Kontos.
     */
    @Test
    public void testCreateAccount() throws IOException, AccountAlreadyExistsException {
        bank.createAccount("KontoA");
        assertTrue(bank.getAccountsToTransactions().containsKey("KontoA"));
        assertTrue(Files.exists(Paths.get(TEST_DIRECTORY, "KontoA.json")));
    }

    /**
     * Testet das Hinzufügen und Entfernen einer Transaktion.
     */
    @Test
    public void testAddRemoveTransaction() throws Exception {
        bank.createAccount("KontoA");
        bank.addTransaction("KontoA", paymentIn);
        assertEquals(1, bank.getTransactions("KontoA").size());
        bank.removeTransaction("KontoA", paymentIn);
        assertEquals(0, bank.getTransactions("KontoA").size());
    }

    /**
     * Testet den Copy-Konstruktor.
     */
    @Test
    public void testCOPY() throws Exception {
        PrivateBank bankCpy = new PrivateBank(bank);
        assertEquals(bank, bankCpy);
    }

    /**
     * Testet alle relevanten Exceptions.
     */
    @Test
    public void testAllExceptions() throws Exception {

        bank.createAccount("KontoA");
        assertThrows(AccountAlreadyExistsException.class, () -> bank.createAccount("KontoA"));
        assertThrows(AccountDoesNotExistException.class, () -> bank.addTransaction("QuatschKonto", paymentIn));

        bank.addTransaction("KontoA", paymentIn);
        assertThrows(TransactionAlreadyExistException.class, () -> bank.addTransaction("KontoA", paymentIn));
        assertThrows(TransactionDoesNotExistException.class, () -> bank.removeTransaction("KontoA", paymentOut));

        Transfer temp = new Transfer("03.01.2025", -50, "Strom", "KontoA", "KontoB");
        assertThrows(TransactionAttributeException.class, () -> bank.attributeValidation(temp));
    }

    /**
     * Testet Kontostandberechnung und automatische Transferumwandlungen.
     */
    @Test
    public void testBalanceAndTransfer() throws Exception {
        bank.createAccount("KontoA");
        bank.addTransaction("KontoA", paymentIn);
        bank.addTransaction("KontoA", paymentOut);
        bank.addTransaction("KontoA", transferOut);

        List<Transaction> txs = bank.getTransactions("KontoA");

        assertTrue(txs.get(2) instanceof OutgoingTransfer);
        assertEquals(797.0, bank.getAccountBalance("KontoA"));
    }


    /**
     * Testet das Laden von gespeicherten Transaktionen aus JSON Dateien.
     */
    @Test
    public void testPersistence() throws Exception {
        bank.createAccount("KontoPersist");
        bank.addTransaction("KontoPersist", paymentIn);

        PrivateBank bank2 = new PrivateBank("ZweiteBank", 0.05, 0.03, TEST_DIRECTORY);
        assertEquals(1, bank2.getTransactions("KontoPersist").size());
        assertTrue(bank2.containsTransaction("KontoPersist", paymentIn));
    }

    /**
     * Testet ungültige Transferbeträge mit verschiedenen negativen Werten.
     */
    @ParameterizedTest
    @ValueSource(doubles = {0.0, -10.0, -0.01, -1000.0})
    public void testInvalidTransferAmount(double amount) throws Exception {
        bank.createAccount("KontoA");
        bank.createAccount("KontoB");
        Transfer t = new Transfer("01.01.2025", amount, "Invalid", "KontoA", "KontoB");
        assertThrows(TransactionAttributeException.class, () -> bank.addTransaction("KontoA", t));
    }

    /**
     * Testet die equals Methode der Bankklasse.
     */
    @Test
    public void testEquals() throws IOException, AccountAlreadyExistsException {
        PrivateBank bank2 = new PrivateBank("TestBank", 0.05, 0.03, TEST_DIRECTORY);
        assertEquals(bank, bank2);
        bank.createAccount("KontoDiff");
        assertNotEquals(bank, bank2);
    }


    /**
     * Testet die Sortierung der Transaktionen nach Wert.
     */
    @Test
    public void testGetTransactionsSorted() throws Exception {
        bank.createAccount("KontoA");

        bank.addTransaction("KontoA", paymentIn);
        bank.addTransaction("KontoA", paymentOut);
        bank.addTransaction("KontoA", transferOut);

        List<Transaction> sortedAsc = bank.getTransactionsSorted("KontoA", true);
        assertEquals(-103.0, sortedAsc.get(0).calculate());
        assertEquals(-50.0, sortedAsc.get(1).calculate());
        assertEquals(950.0, sortedAsc.get(2).calculate());

        List<Transaction> sortedDesc = bank.getTransactionsSorted("KontoA", false);
        assertEquals(950.0, sortedDesc.get(0).calculate());
        assertEquals(-50.0, sortedDesc.get(1).calculate());
        assertEquals(-103.0, sortedDesc.get(2).calculate());
    }

    /**
     * Testet das Filtern von positiven und negativen Transaktionen.
     */
    @Test
    public void testGetTransactionsByType() throws Exception {
        bank.createAccount("KontoA");

        bank.addTransaction("KontoA", paymentIn);
        bank.addTransaction("KontoA", paymentOut);
        bank.addTransaction("KontoA", transferOut);

        List<Transaction> positive = bank.getTransactionsByType("KontoA", true);
        assertEquals(1, positive.size());
        assertEquals(950.0, positive.get(0).calculate());

        List<Transaction> negative = bank.getTransactionsByType("KontoA", false);
        assertEquals(2, negative.size());
    }
}

package bank;

import bank.exceptions.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PrivateBank Tests kompakt")
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

        if (!dir.exists()) {
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.getName().endsWith(".json")) {
                file.delete();
            }
        }
    }




    @Test
    @DisplayName("createAccount & JSON-Datei")
    public void testCreateAccount() throws IOException, AccountAlreadyExistsException {
        bank.createAccount("KontoA");
        assertTrue(bank.getAccountsToTransactions().containsKey("KontoA"));
        assertTrue(Files.exists(Paths.get(TEST_DIRECTORY, "KontoA.json")));
    }

    @Test
    @DisplayName("addTransaction / removeTransaction")
    public void testAddRemoveTransaction() throws Exception {
        bank.createAccount("KontoA");
        bank.addTransaction("KontoA", paymentIn);
        assertEquals(1, bank.getTransactions("KontoA").size());
        bank.removeTransaction("KontoA", paymentIn);
        assertEquals(0, bank.getTransactions("KontoA").size());
    }


    @Test
    @DisplayName("Copy konst")
    public void testCOPY() throws Exception {
        PrivateBank bankCpy= new PrivateBank(bank);
        assertEquals(bank, bankCpy);
    }

    @Test
    @DisplayName("Testet alle Exceptions)")
    public void testAllExceptions() throws Exception {

        bank.createAccount("KontoA");
        assertThrows(AccountAlreadyExistsException.class, () -> bank.createAccount("KontoA"));
        assertThrows(AccountDoesNotExistException.class, () -> bank.addTransaction("QuatschKonto", paymentIn));
        bank.addTransaction("KontoA", paymentIn);
        assertThrows(TransactionAlreadyExistException.class, () -> bank.addTransaction("KontoA", paymentIn));
        assertThrows(TransactionDoesNotExistException.class, () -> bank.removeTransaction("KontoA", paymentOut));
        Transfer temp =new Transfer("03.01.2025", -50, "Strom", "KontoA", "KontoB");
        assertThrows(TransactionAttributeException.class,() -> bank.attributeValidation(temp));
    }

    @Test
    @DisplayName("P3-Logik: getAccountBalance & Transfer-Typ")
    public void testBalanceAndTransfer() throws Exception {
        bank.createAccount("KontoA");
        bank.addTransaction("KontoA", paymentIn);
        bank.addTransaction("KontoA", paymentOut);
        bank.addTransaction("KontoA", transferOut);
        List<Transaction> txs = bank.getTransactions("KontoA");
        assertTrue(txs.get(2) instanceof OutgoingTransfer);
        assertEquals(797.0, bank.getAccountBalance("KontoA"));
    }

    @Test
    @DisplayName("P4-Persistenz: readAccounts")
    public void testPersistence() throws Exception {
        bank.createAccount("KontoPersist");
        bank.addTransaction("KontoPersist", paymentIn);
        PrivateBank bank2 = new PrivateBank("ZweiteBank", 0.05, 0.03, TEST_DIRECTORY);
        assertEquals(1, bank2.getTransactions("KontoPersist").size());
        assertTrue(bank2.containsTransaction("KontoPersist", paymentIn));
    }

    @Test
    @DisplayName("equals-Methode")
    public void testEquals() throws IOException, AccountAlreadyExistsException {
        PrivateBank bank2 = new PrivateBank("TestBank", 0.05, 0.03, TEST_DIRECTORY);
        assertEquals(bank, bank2);
        bank.createAccount("KontoDiff");
        assertNotEquals(bank, bank2);
    }

    @Test
    @DisplayName("getTransactionsSorted: aufsteigend und absteigend")
    public void testGetTransactionsSorted() throws Exception {
        bank.createAccount("KontoA");

        // Transaktionen hinzufügen
        bank.addTransaction("KontoA", paymentIn);   // +950
        bank.addTransaction("KontoA", paymentOut);  // -103
        bank.addTransaction("KontoA", transferOut); // -50 (als OutgoingTransfer)

        // Aufsteigend sortieren
        List<Transaction> sortedAsc = bank.getTransactionsSorted("KontoA", true);
        assertEquals(-103.0, sortedAsc.get(0).calculate());
        assertEquals(-50.0, sortedAsc.get(1).calculate());
        assertEquals(950.0, sortedAsc.get(2).calculate());

        // Absteigend sortieren
        List<Transaction> sortedDesc = bank.getTransactionsSorted("KontoA", false);
        assertEquals(950.0, sortedDesc.get(0).calculate());
        assertEquals(-50.0, sortedDesc.get(1).calculate());
        assertEquals(-103.0, sortedDesc.get(2).calculate());
    }
    @Test
    @DisplayName("getTransactionsByType: positive und negative Transaktionen")
    public void testGetTransactionsByType() throws Exception {
        bank.createAccount("KontoA");

        // Transaktionen hinzufügen
        bank.addTransaction("KontoA", paymentIn);   // +950
        bank.addTransaction("KontoA", paymentOut);  // -103
        bank.addTransaction("KontoA", transferOut); // -50 (als OutgoingTransfer)

        // Positive Transaktionen abfragen
        List<Transaction> positive = bank.getTransactionsByType("KontoA", true);
        assertEquals(1, positive.size());
        assertEquals(950.0, positive.get(0).calculate());

        // Negative Transaktionen abfragen
        List<Transaction> negative = bank.getTransactionsByType("KontoA", false);
        assertEquals(2, negative.size());
    }

}

package bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 Testklasse für die Transfer-Klasse (P4, A3).
 * Testet auch das calculate()-Verhalten von Incoming- und OutgoingTransfer.
 */
public class TransferTest {

    private Transfer transferBase;
    private Transfer transferDifferent;
    private IncomingTransfer incoming; // Für calculate() Test
    private OutgoingTransfer outgoing; // Für calculate() Test

    /**
     * Diese 'init'-Methode wird (dank @BeforeEach)
     * vor JEDEM einzelnen @Test neu ausgeführt.
     */
    @BeforeEach
    public void init() {
        // Testobjekt 1 (Basis für die meisten Tests)
        transferBase = new Transfer("01.01.2025", 100, "Miete", "Konto A", "Konto B");

        // Testobjekt 2 (für Ungleichheits-Tests)
        transferDifferent = new Transfer("02.01.2025", 50, "Strom", "Konto C", "Konto D");

        // Testobjekte für P4, A3 Anforderung (Test calculate())
        // Wir simulieren die Objekte, die PrivateBank in P3 erstellt hat.
        incoming = new IncomingTransfer(transferBase);
        outgoing = new OutgoingTransfer(transferBase);
    }

    /**
     * Test 1: Testet den Konstruktor und die Getter (der Basis-Transfer-Klasse).
     */
    @Test
    @DisplayName("Konstruktor und Getter (Transfer)")
    public void testConstructor() {
        assertEquals("01.01.2025", transferBase.getDate());
        assertEquals(100, transferBase.getAmount());
        assertEquals("Miete", transferBase.getDescription());
        assertEquals("Konto A", transferBase.getSender());
        assertEquals("Konto B", transferBase.getRecipient());
    }

    /**
     * Test 2: Testet den Copy-Konstruktor (der Basis-Transfer-Klasse).
     */
    @Test
    @DisplayName("Copy-Konstruktor (Transfer)")
    public void testCopyConstructor() {
        Transfer copy = new Transfer(transferBase);

        assertEquals(transferBase, copy, "Kopie sollte 'equals' dem Original sein");
        assertNotSame(transferBase, copy, "Kopie sollte nicht dasselbe Objekt im Speicher sein");
    }

    /**
     * Test 3: Testet calculate() (alle drei Varianten).
     * (Wie in P4, A3 gefordert)
     */
    @Test
    @DisplayName("calculate() (Base, Incoming, Outgoing)")
    public void testCalculate() {

        // Fall 1: Basis 'Transfer'-Klasse (P3-Verhalten: calculate() ändert nichts)
        // (Falls dein P3-Code hier -amount zurückgibt, wäre 100.0 falsch)
        assertEquals(100.0, transferBase.calculate(), "Basis-Transfer.calculate() sollte den Betrag unverändert zurückgeben");

        // Fall 2: 'IncomingTransfer' (sollte positiv sein)
        assertEquals(100.0, incoming.calculate(), "IncomingTransfer.calculate() sollte den positiven Betrag zurückgeben");

        // Fall 3: 'OutgoingTransfer' (sollte negativ sein)
        assertEquals(-100.0, outgoing.calculate(), "OutgoingTransfer.calculate() sollte den negativen Betrag zurückgeben");
    }

    /**
     * Test 4: Testet die equals()-Methode (der Basis-Transfer-Klasse).
     */
    @Test
    @DisplayName("equals() (Transfer)")
    public void testEquals() {
        Transfer copy = new Transfer(transferBase);

        assertEquals(transferBase, copy);
        assertNotEquals(transferBase, transferDifferent);
        assertNotEquals(null, transferBase);

        // WICHTIG: Ein Transfer darf nicht 'equals' einem IncomingTransfer sein (wg. getClass()-Check)
        assertNotEquals(transferBase, incoming, "Transfer und IncomingTransfer dürfen nicht gleich sein");
    }

    /**
     * Test 5: Testet die toString()-Methode (robust).
     */
    @Test
    @DisplayName("toString() (Transfer)")
    public void testToString() {
        String output = transferBase.toString();

        assertTrue(output.contains("Transfer"), "toString() sollte 'Transfer' enthalten");
        assertTrue(output.contains("100"), "toString() sollte Betrag (oder berechneten Betrag) enthalten");
        assertTrue(output.contains("Miete"), "toString() sollte Beschreibung enthalten");
        assertTrue(output.contains("Konto A"), "toString() sollte Sender enthalten");
    }
}
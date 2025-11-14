package bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 Testklasse für die Payment-Klasse (P4, A3).
 */
public class PaymentTest {

    private Payment paymentIncoming;
    private Payment paymentOutgoing;

    /**
     * Diese 'init'-Methode wird (dank @BeforeEach)
     * vor JEDEM einzelnen @Test neu ausgeführt.
     */
    @BeforeEach
    public void init() {
        paymentIncoming = new Payment("01.01.2025", 1000, "Gehalt", 0.05, 0.03);
        paymentOutgoing = new Payment("02.01.2025", -100, "Miete", 0.05, 0.03);
    }

    /**
     * Test 1: Testet den Konstruktor und die Getter.
     */
    @Test
    @DisplayName("Konstruktor und Getter")
    public void testConstructor() {
        assertEquals("01.01.2025", paymentIncoming.getDate());
        assertEquals(1000, paymentIncoming.getAmount());
        assertEquals("Gehalt", paymentIncoming.getDescription());
        assertEquals(0.05, paymentIncoming.getIncomingInterest());
        assertEquals(0.03, paymentIncoming.getOutgoingInterest());
    }

    /**
     * Test 2: Testet den Copy-Konstruktor.
     */
    @Test
    @DisplayName("Copy-Konstruktor")
    public void testCopyConstructor() {
        Payment copy = new Payment(paymentIncoming);

        assertEquals(paymentIncoming, copy, "Kopie sollte 'equals' dem Original sein");
        assertNotSame(paymentIncoming, copy, "Kopie sollte nicht dasselbe Objekt im Speicher sein");
    }

    /**
     * Test 3: Testet calculate() (beide Fälle, Ein- und Auszahlung).
     */
    @Test
    @DisplayName("calculate() (Ein- und Auszahlung)")
    public void testCalculate() {
        assertEquals(950.0, paymentIncoming.calculate(), "Berechnung für Einzahlung ist falsch");
        assertEquals(-103.0, paymentOutgoing.calculate(), "Berechnung für Auszahlung ist falsch");
    }

    /**
     * Test 4: Testet die equals()-Methode.
     */
    @Test
    @DisplayName("equals()")
    public void testEquals() {
        Payment copy = new Payment(paymentIncoming);

        assertEquals(paymentIncoming, copy);
        assertNotEquals(paymentIncoming, paymentOutgoing);
        assertNotEquals(null, paymentIncoming);
    }

    /**
     * Test 5: Testet die toString()-Methode (robust).
     */
    @Test
    @DisplayName("toString()")
    public void testToString() {
        String output = paymentIncoming.toString();

        assertTrue(output.contains("Payment"), "toString() sollte 'Payment' enthalten");
        assertTrue(output.contains("950"), "toString() sollte den *berechneten* Betrag enthalten");
        assertTrue(output.contains("Gehalt"), "toString() sollte Beschreibung enthalten");
    }
}
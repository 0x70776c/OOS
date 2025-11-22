package bank;

import bank.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    private Payment payment1;
    private Payment payment2;

    @BeforeEach
    void setUp() {
        // Testdaten erstellen
        payment1 = new Payment("2025-11-17", 100.0, "Miete", 0.01, 0.02);
        payment2 = new Payment(payment1); // Copy-Konstruktor
    }

    @Test
    void testConstructorAndCopy() {
        assertEquals(payment1.getAmount(), payment2.getAmount());
        assertEquals(payment1.getDescription(), payment2.getDescription());
        assertEquals(payment1.getDate(), payment2.getDate());
        assertEquals(payment1.getIncomingInterest(), payment2.getIncomingInterest());
        assertEquals(payment1.getOutgoingInterest(), payment2.getOutgoingInterest());
    }

    @Test
    void testCalculate() {
        double expected = 100.0 + (100.0 * 0.01) - (100.0 * 0.02);
        assertEquals(expected, payment1.calculate(), 1e-6);
    }

    @Test
    void testEqualsAndToString() {
        assertEquals(payment1, payment2);
        assertTrue(payment1.toString().contains("Miete"));
    }
}

package bank;

import bank.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferTest {

    private Transfer transfer;
    private IncomingTransfer incoming;
    private OutgoingTransfer outgoing;

    @BeforeEach
    void setUp() {
        transfer = new Transfer("2025-11-17", 200.0, "Gehalt", "Alice", "Bob");
        incoming = new IncomingTransfer(transfer);
        outgoing = new OutgoingTransfer(transfer);
    }

    @Test
    void testCalculate() {
        assertEquals(200.0, incoming.calculate(), 1e-6);
        assertEquals(-200.0, outgoing.calculate(), 1e-6);
    }

    @Test
    void testEqualsAndToString() {
        Transfer copy = new Transfer(transfer);
        assertEquals(transfer, copy);
        assertTrue(transfer.toString().contains("Alice"));
    }
}

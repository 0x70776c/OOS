package bank;

public class IncomingTransfer extends Transfer{
    public IncomingTransfer(String date, double amount, String desc) {
        super(date, amount, desc);
    }

    public IncomingTransfer(String date, double amount, String desc, String sender, String recipient) {
        super(date, amount, desc, sender, recipient);
    }

    public IncomingTransfer(Transfer other) {
        super(other);
    }


    /**
     * Überschriebene Methode für Variante 1.
     * Ein "IncomingTransfer" (Empfang) erhöht den Kontostand.
     * @return Der positive Betrag der Transaktion.
     */
    @Override
    public double calculate() {
        // Gibt den positiven Betrag zurück
        return this.getAmount();
    }
}

package bank;

public class OutgoingTransfer extends Transfer{
    public OutgoingTransfer(String date, double amount, String desc) {
        super(date, amount, desc);
    }

    public OutgoingTransfer(String date, double amount, String desc, String sender, String recipient) {
        super(date, amount, desc, sender, recipient);
    }

    public OutgoingTransfer(Transfer other) {
        super(other);
    }


    /**
     * Überschriebene Methode für Variante 1.
     * Ein "OutgoingTransfer" sendung verringert den Kontostand.
     * @return Der negative Betrag der Transaktion.
     */
    @Override
    public double calculate() {
        return -this.getAmount();
    }
}

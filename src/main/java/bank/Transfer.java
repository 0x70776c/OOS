package bank;

import java.util.Objects;

/**
 * Stellt eine Transaktion zwischen einem Sender und einem Empfänger.
 * Diese Klasse erweitert {@link Transaction} und fügt Attribute für Sender und Empfänger hinzu.
 */
public class Transfer extends Transaction {

    /**
     * Der Name des Senders.
     */
    private String sender;


    /**
     * Der Name  des Empfängers.
     */
    private String recipient;



    /**
     * Erstellt eine neue Überweisung.
     * @param date   Das Datum der Überweisung.
     * @param amount Der überwiesene Betrag (darf nicht 0 sein).
     * @param desc   Eine Beschreibung der Überweisung.
     */
    public Transfer(String date, double amount, String desc) {
        super(date, amount, desc);
    }

    /**
     * Erstellt eine neue Überweisung mit allen parametern.
     * @param date       Datum der Überweisung.
     * @param amount     überwiesener Betrag !=0.
     * @param desc       Beschreibung der Überweisung.
     * @param sender     Sender des Geldes.
     * @param recipient  Empfänger des Geldes.
     */
    public Transfer(String date, double amount, String desc, String sender, String recipient) {
        super(date, amount, desc);
        this.sender = sender;
        this.recipient = recipient;
    }

    /**
     * Ein Copy-Konstruktor, der ein neues Objekt als exakte Kopie eines anderen erstellt.
     *
     * @param other Das zu kopierende Objekt.
     */
    public Transfer(Transfer other) {
        super(other.getDate(), other.getAmount(), other.getDescription());
        this.sender = other.sender;
        this.recipient = other.recipient;
    }


    /**
     * Gibt den Sender der Überweisung zurück.
     *
     * @return Der Name des Senders.
     */
    public String getSender() {
        return this.sender;
    }

    /**
     * Private Hilfsmethode nur für die Gültigkeitsprüfung.
     */
    private void validateAmount(double amount) {
        if (amount <= 0) {
           System.out.println("Der Betrag einer Überweisung muss positiv sein.");
        }
    }
    /**
     * Legt den Sender der Überweisung fest.
     *
     * @param sender Der neue Sender.
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Gibt den Empfänger der Überweisung zurück.
     *
     * @return Der Name des Empfängers.
     */
    public String getRecipient() {
        return this.recipient;
    }

    /**
     * Legt den Empfänger der Überweisung fest.
     *
     * @param recipient Der neue Empfänger.
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * Berechnet den Wert der Überweisung.
     * Bei einer Überweisung ist der berechnete Wert einfach der Betrag selbst.
     *
     * @return Der Betrag der Überweisung.
     */
    @Override
    public double calculate() {
        return this.getAmount();
    }



    @Override
    public void setAmount(double amount) {
        validateAmount(amount);
        super.setAmount(amount);
    }
    /**
     * Gibt eine String-Repräsentation der Überweisung zurück, die die Basis-Transaktionsdaten
     * sowie Sender und Empfänger enthält.
     *
     * @return Ein String, der alle Attribute des Objekts darstellt.
     */
    @Override
    public String toString() {
        return "Transfer{" +
                super.toString() +
                ", sender='" + sender  +
                ", recipient='" + recipient +
                '}';
    }



    public double calculate2(){
        return this.getAmount()*0.25;
    }

    /**
     *
     * @param obj
     * @return true wenn Attribute gleich
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(super.equals(obj)){
            Transfer other = (Transfer) obj;
            if (this.sender.equals(other.sender) && this.recipient.equals(other.recipient)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Erzeugt einen Hashcode für das Objekt.
     * Notwendig für die korrekte Funktion in HashMaps und HashSets.
     *
     * @return Ein Hashcode-Wert für dieses Objekt.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sender, recipient);
    }


}
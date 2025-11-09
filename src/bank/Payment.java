package bank;

import java.util.Objects;

/**
 * Repräsentiert eine spezielle Art von Transaktion, die eine Ein- oder Auszahlung darstellt.
 * Diese Klasse erweitert {@link Transaction} und fügt Attribute für Zinsen hinzu,
 * die je nachdem, ob es sich um eine Ein- oder Auszahlung handelt, unterschiedlich sein können.
 *
 */
public class Payment extends Transaction {

    /**
     * Der Zinssatz für positive Beträge (Einzahlungen).
     */
    private double incomingInterest;

    /**
     * Der Zinssatz für negative Beträge (Auszahlungen).
     */
    private double outgoingInterest;

    /**
     * Erstellt eine neue Ein-/Auszahlung mit allen erforderlichen Details.
     *
     * @param date             Das Datum der Transaktion.
     * @param amount           Der Betrag der Transaktion.
     * @param desc             Eine Beschreibung der Transaktion.
     * @param incomingInterest Der Zinssatz für Einzahlungen (z.B. 0.05 für 5%).
     * @param outgoingInterest Der Zinssatz für Auszahlungen (z.B. 0.1 für 10%).
     */
    public Payment(String date, double amount, String desc, double incomingInterest, double outgoingInterest) {
        super(date, amount, desc);
        this.incomingInterest = incomingInterest;
        this.outgoingInterest = outgoingInterest;
    }

    /**
     * Ein Copy-Konstruktor, der ein neues {@code Payment}-Objekt als exakte Kopie eines anderen erstellt.
     *
     * @param other Das zu kopierende {@code Payment}-Objekt.
     */
    public Payment(Payment other) {
        super(other.getDate(), other.getAmount(), other.getDescription());
        this.incomingInterest = other.incomingInterest;
        this.outgoingInterest = other.outgoingInterest;
    }

    /**
     * Erstellt eine einfache Ein-/Auszahlung ohne spezifische Zinsen.
     *
     * @param s          Das Datum der Transaktion.
     * @param i          Der Betrag der Transaktion.
     * @param einzahlung Eine Beschreibung der Transaktion.
     */
    public Payment(String Datum, double Betrag, String Beschreibung) {
        super(Datum, Betrag, Beschreibung);
    }

    /**
     * Gibt den Zinssatz für Einzahlungen zurück.
     *
     * @return Der Zinssatz für positive Beträge.
     */
    public double getIncomingInterest() {
        return incomingInterest;
    }

    /**
     * Legt den Zinssatz für Einzahlungen fest.
     *
     * @param incomingInterest Der neue Zinssatz.
     */
    public void setIncomingInterest(double incomingInterest) {
        this.incomingInterest = incomingInterest;
    }

    /**
     * Gibt den Zinssatz für Auszahlungen zurück.
     *
     * @return Der Zinssatz für negative Beträge.
     */
    public double getOutgoingInterest() {
        return outgoingInterest;
    }

    /**
     * Legt den Zinssatz für Auszahlungen fest.
     *
     * @param outgoingInterest Der neue Zinssatz.
     */
    public void setOutgoingInterest(double outgoingInterest) {
        this.outgoingInterest = outgoingInterest;
    }


    /**
     *
     * @return amount mit incomingInteres abgezogen bei Einzahlung
     * mit outgoingInterest addiert bei Auszahlung
     */
    @Override
    public double calculate() {
        double ret = 0;
        if(this.getAmount() > 0) {
            ret = this.getAmount() *(1-this.incomingInterest);
        }
        else{
            ret = this.getAmount() *(1+this.outgoingInterest);

        }
        return ret;
    }


    /**
     * Gibt eine String-Repräsentation des Objekts zurück, die die Basis-Transaktionsdaten
     * und die spezifischen Zinsinformationen enthält.
     *
     * @return Ein String, der alle Attribute des Objekts darstellt.
     */
    @Override
    public String toString() {
        return "Payment{" +
                super.toString() +
                ", incomingInterest=" + incomingInterest +
                ", outgoingInterest=" + outgoingInterest +
                '}';
    }

public double calculate2(){
        return this.getAmount()*0.5;
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
            Payment other = (Payment) obj;
            if (this.incomingInterest == other.incomingInterest && this.outgoingInterest == other.outgoingInterest) {
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
        return Objects.hash(super.hashCode(), incomingInterest, outgoingInterest);
    }
}
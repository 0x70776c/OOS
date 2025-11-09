package bank;

import java.util.Objects;

/**
 * Repräsentiert eine abstrakte Basisklasse für alle Transaktionen in einem Banksystem.
 * Sie enthält die grundlegenden Attribute, die jede Transaktion teilt: Datum, Betrag und eine Beschreibung.
 * Implementiert das {@link CalculateBill} Interface, um sicherzustellen, dass alle abgeleiteten Klassen
 * eine Berechnungslogik bereitstellen.
 */
public abstract class Transaction implements CalculateBill {

    /**
     * Das Datum, an dem die Transaktion stattgefunden hat.
     */
    private String date;

    /**
     * Der Geldbetrag der Transaktion. Kann positiv (Einzahlung) oder negativ (Auszahlung) sein.
     */
    private double amount;

    /**
     * Eine kurze Beschreibung des Zwecks der Transaktion.
     */
    private String description;

    /**
     * Erstellt eine neue Transaktion mit den angegebenen Details.
     *
     * @param date        Das Datum der Transaktion im Format TT.MM.JJJJ.
     * @param amount      Der Betrag der Transaktion.
     * @param description Eine Beschreibung der Transaktion.
     */
    public Transaction(String date, double amount, String description) {
        this.date = date;
        setAmount(amount);
        this.description = description;
    }

    /**
     * Gibt das Datum der Transaktion zurück.
     *
     * @return Das Datum der Transaktion als String.
     */
    public String getDate() {
        return date;
    }

    /**
     * Legt das Datum der Transaktion fest.
     *
     * @param date Das neue Datum der Transaktion.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gibt den Betrag der Transaktion zurück.
     *
     * @return Der Geldbetrag der Transaktion.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Legt den Betrag der Transaktion fest.
     *
     * @param amount Der neue Betrag der Transaktion.
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Gibt die Beschreibung der Transaktion zurück.
     *
     * @return Die Beschreibung als String.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Legt die Beschreibung der Transaktion fest.
     *
     * @param description Die neue Beschreibung der Transaktion.
     */
    public void setDescription(String description) {
        this.description = description;
    }



    /**
     * Gibt eine String-Repräsentation der gemeinsamen Transaktionsattribute zurück.
     *
     * @return Ein String mit Datum, Betrag und Beschreibung.
     */
    @Override
    public String toString() {
        return "date= " + date  +
                ", description= " + description +
                ", calculatedAmount= " + calculate();
    }

    /**
     *
     * @param obj
     * @return true wenn Attribute gleich
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(this.getClass() == obj.getClass()) {
            Transaction other = (Transaction) obj;//notwendig da compiler nur typo object sieht
            if (this.description.equals(other.description) && this.amount == other.amount && this.date.equals(other.date)) {
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
        return Objects.hash(date,description, amount);
    }

}
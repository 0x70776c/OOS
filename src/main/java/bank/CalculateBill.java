package bank;

/**
 * Definiert einen Vertrag für Klassen, die eine Berechnung durchführen können.
 * <p>
 * Dieses Interface wird von Klassen implementiert, die eine eigene Logik zur
 * Berechnung eines finalen Wertes
 * bereitstellen müssen.
 */
public interface CalculateBill {

    /**
     * Führt die definierte Berechnung durch und gibt das Ergebnis zurück.
     * Die Implementierung dieser Methode sollte den finalen Wert des Objekts
     * basierend auf dessen internem Zustand berechnen.
     *
     * @return Das Ergebnis der Berechnung als {@code double}.
     */
    public double calculate();

    public double calculate2();

}
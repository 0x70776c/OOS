# Bank Manager 🏦

Eine voll funktionsfähige Banking-Applikation mit grafischer Benutzeroberfläche (GUI), entwickelt im Rahmen des Moduls **Objektorientierte Softwareentwicklung (OOS)** an der FH Aachen University of Applied Sciences (3. Semester).

Dieses Projekt demonstriert die iterative Entwicklung einer komplexen Java-Anwendung von den ersten OOP-Grundlagen bis hin zu einer professionellen Architektur mit **MVC-Pattern**, **Persistenz** und **Unit-Testing**.

---

## 🚀 Funktionalität & Features

Der PrivateBank Manager ermöglicht die Verwaltung von Bankkonten und deren Transaktionen über eine moderne JavaFX-Oberfläche:

- **Account Management:** Erstellen, Anzeigen und Löschen von Konten.
- **Transaktionsverwaltung:**
  - Hinzufügen von Ein-/Auszahlungen (`Payment`) und Überweisungen (`Transfer`)
  - Automatische Berechnung des aktuellen Kontostands
  - Intelligente Unterscheidung zwischen `IncomingTransfer` und `OutgoingTransfer`
- **Persistenz:** Alle Daten werden automatisch im JSON-Format serialisiert und lokal gespeichert. Der Zustand bleibt nach einem Neustart erhalten.
- **Analyse:** Sortieren von Transaktionen (nach Betrag/Datum) und Filtern (nur Einnahmen/Ausgaben)
- **User Experience:** Interaktive Dialoge für Eingaben und Sicherheitsabfragen (`Alerts`) bei kritischen Aktionen

---

## 🎓 Lernziele & Projektfortschritt (P1 - P5)

Das Projekt wurde über fünf aufeinanderfolgende Praktika entwickelt, wobei jedes Praktikum neue Technologien und Konzepte eingeführt hat:

| Phase | Fokus & Lernziele |
|-------|-----------------|
| **P1** | Grundlagen: Einführung in Java, Klassenmodellierung, Attribute, Methoden und Kapselung |
| **P2** | OOP-Konzepte: Vererbung (`Transaction` als Basisklasse), Polymorphie, Abstrakte Klassen und Interfaces (`CalculateBill`) |
| **P3** | Robustheit & Datenstrukturen: Fehlerbehandlung durch Custom Exceptions, Nutzung von Collections (`Map`, `List`) und komplexe Geschäftslogik |
| **P4** | I/O & Qualitätssicherung: Implementierung von Persistenz mittels Gson (JSON-Serialisierung) und Test-Driven Development (TDD) mit JUnit 5 (`@BeforeEach`, `@AfterEach`) |
| **P5** | GUI & Architektur: Entwicklung einer grafischen Oberfläche mit JavaFX und FXML. Anwendung des **Model-View-Controller (MVC)** Patterns und Event-Handling |

---

## 📂 Projektstruktur

Das Projekt folgt einer sauberen **MVC-Architektur (Model-View-Controller)** mit strikter Trennung von Logik, Datenhaltung und Anzeige:
### Projektstruktur

```plaintext
.
├── src
│   └── main
│       ├── java
│       │   └── bank
│       │       ├── exceptions
│       │       │   ├── AccountAlreadyExistsException.java
│       │       │   ├── AccountDoesNotExistException.java
│       │       │   ├── TransactionAlreadyExistException.java
│       │       │   ├── TransactionAttributeException.java
│       │       │   └── TransactionDoesNotExistException.java
│       │       ├── Bank.java
│       │       ├── PrivateBank.java
│       │       ├── Transaction.java
│       │       ├── Payment.java
│       │       ├── Transfer.java
│       │       ├── IncomingTransfer.java
│       │       ├── OutgoingTransfer.java
│       │       ├── JSONHandler.java
│       │       └── ui
│       │           ├── Main.java
│       │           ├── FxApplication.java
│       │           ├── MainController.java
│       │           └── AccountController.java
│       └── resources
│           ├── MainView.fxml
│           └── AccountView.fxml
└── test
    └── java
        └── bank
            ├── PrivateBankTest.java
            ├── PaymentTest.java
            └── TransferTest.java
```
---

## 🛠️ Technologie-Stack

- **Sprache:** Java 17+
- **Build System:** Maven
- **UI Framework:** JavaFX (FXML)
- **Datenformat:** JSON (Google Gson Library)
- **Testing:** JUnit 5 (Jupiter)

---

## ▶️ Installation & Setup

1. **Repository klonen:**

```bash
git clone https://github.com/DeinUsername/privatebank-manager.git
```
2. **Konfiguration anpassen:**
Öffnen Sie src/main/java/ui/FxApplication.java und passen Sie den Speicherpfad im Konstruktor der PrivateBank an Ihr lokales System an:
```bash
// Beispiel für Windows:
bank = new PrivateBank("MyBank", 0.05, 0.03, "C:\\Users\\Name\\Documents\\BankData");
```

3.**Ausführen:**
Starten Sie die Anwendung über die Klasse ui.Main (um JavaFX-Modul-Konflikte zu vermeiden) oder nutzen Sie Maven:
mvn clean javafx:run


package bank;

import bank.exceptions.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Repräsentiert eine private Bank, die Konten und Transaktionen verwaltet.
 * Implementiert das Bank-Interface unter Verwendung von Incoming/OutgoingTransfer (Variante 1).
 */
public class PrivateBank implements Bank {


    /**
     * Map, die Kontonamen auf Listen von Transaktionen abbildet.
     * Wird direkt initialisiert.
     */
    private final Map<String, List<Transaction>> accountsToTransactions = new HashMap<>();
    private String name;
    private double incomingInterest;
    private double outgoingInterest;
    private transient Gson gson;
    private String directoryName;
    /**
     * Standard-Konstruktor.
     *
     * @param name             Name der Bank
     * @param incomingInterest Einzahlungszins
     * @param outgoingInterest Auszahlungszins
     */
    public PrivateBank(String name, double incomingInterest, double outgoingInterest, String directoryName) throws IOException {
        this.name = name;
        this.incomingInterest = incomingInterest;
        this.outgoingInterest = outgoingInterest;
        this.directoryName = directoryName;

        this.readAccounts();

    }


    /**
     * Copy-Konstruktor.
     *
     * @param other Die zu kopierende PrivateBank
     */
    public PrivateBank(PrivateBank other) throws IOException {
        this.name = other.name;
        this.incomingInterest = other.incomingInterest;
        this.outgoingInterest = other.outgoingInterest;
        this.directoryName = other.directoryName;
    }

    public Map<String, List<Transaction>> getAccountsToTransactions() {
        return accountsToTransactions;
    }

    /**
     * Setter setzen mit hilfe von @param attribute neu
     *
     * @return gibt die attribute zurück
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public double getIncomingInterest() {
        return incomingInterest;
    }

    public void setIncomingInterest(double incomingInterest) {
        this.incomingInterest = incomingInterest;
    }

    public double getOutgoingInterest() {
        return outgoingInterest;
    }

    public void setOutgoingInterest(double outgoingInterest) {
        this.outgoingInterest = outgoingInterest;
    }


    /**
     * Gibt eine String-Repräsentation des Objekts zurück, die die Basis-Transaktionsdaten
     * und die spezifischen Zinsinformationen enthält.
     *
     * @return Ein String, der alle Attribute des Objekts darstellt.
     */

    @Override
    public String toString() {
        return "PrivateBank[" +
                "name='" + name +
                ", incomingInterest=" + incomingInterest +
                ", outgoingInterest=" + outgoingInterest +
                ", numAccounts=" + accountsToTransactions.size() +
                ']';
    }


    /**
     * @param obj
     * @return true wenn Attribute gleich
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        PrivateBank other = (PrivateBank) obj;

        return Double.compare(other.incomingInterest, incomingInterest) == 0 &&
                Double.compare(other.outgoingInterest, outgoingInterest) == 0 &&
                Objects.equals(name, other.name) &&
                Objects.equals(directoryName, other.directoryName) && // dir geadded
                Objects.equals(accountsToTransactions, other.accountsToTransactions);
    }


    /**
     * Liest alle .json-Dateien aus dem directoryName,
     * deserialisiert sie und lädt sie in die accountsToTransactions-Map.
     *
     * @throws IOException Wenn ein E/A-Fehler beim Lesen der Dateien auftritt.
     */
    private void readAccounts() throws IOException {
        Path dirPath = Paths.get(this.directoryName);

        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                System.err.println("Fehler beim Erstellen des Verzeichnisses: " + dirPath);
                throw e;
            }
            return;
        }

        Gson deserialize = new GsonBuilder().registerTypeAdapter(Transaction.class, new JSONHandler()).setPrettyPrinting().create();

        Type typeList = new TypeToken<List<Transaction>>() {}.getType();

        try (Stream<Path> files = Files.list(dirPath)) {
            files.filter(p -> p.toString().endsWith(".json")).forEach(filePath -> {

                // Dateiname (z.B. "Konto Adam.json")
                String fileName = filePath.getFileName().toString();
                // Kontoname (z.B. "Konto Adam")
                String accountName = fileName.substring(0, fileName.lastIndexOf('.'));

                // Versuche, die Datei zu lesen und zu deserialisieren
                try (Reader reader = new FileReader(filePath.toFile())) {

                    List<Transaction> transactions = deserialize.fromJson(reader, typeList);

                    if (transactions != null) {
                        this.accountsToTransactions.put(accountName, new ArrayList<>(transactions));
                    }

                } catch (Exception e) {

                    System.err.println("Fehler beim Lesen der Kontodatei " + fileName + ": " + e.getMessage());
                }
            });
        }
    }

    /**
     * Schreibt den *aktuellen* Stand eines Kontos aus der Map
     * in die entsprechende .json-Datei.
     *
     * @param account Der Name des Kontos, das gespeichert werden soll.
     * @throws IOException Wenn ein E/A-Fehler beim Schreiben der Datei auftritt.
     */
    private void writeAccount(String account) throws IOException {
        List<Transaction> transactions = this.accountsToTransactions.get(account);
        if (transactions == null) {
            throw new IOException("Konto '" + account + "' nicht in der Map gefunden, kann nicht schreiben.");
        }

        Gson serialize = new GsonBuilder().registerTypeAdapter(Transaction.class, new JSONHandler()).setPrettyPrinting().create();

        Type typeList = new TypeToken<List<Transaction>>() {}.getType();

        Path filePath = Paths.get(this.directoryName, account + ".json");

        try (Writer writer = new FileWriter(filePath.toFile())) {

            serialize.toJson(transactions, typeList, writer);
        }
    }


    /**
     * fügt neuen account in liste hinzu
     *
     * @param account der geadded wird
     * @throws AccountAlreadyExistsException wenn der account bereits vorhanden ist
     */
    @Override
    public void createAccount(String account) throws AccountAlreadyExistsException,IOException {

        if (accountsToTransactions.containsKey(account)) {
            throw new AccountAlreadyExistsException("Konto '" + account + "' existiert bereits.");
        }
        accountsToTransactions.put(account, new ArrayList<>());

        writeAccount(account);
    }


    /**
     * fügt eine transaction zu einem neuen hinzu
     *
     * @param account      account der erstellt wird und dem die transaction zugewiesen wird
     * @param transactions die transactions die dem account zugewiesen werden
     * @throws TransactionAlreadyExistException wenn die transaction bereits existiert
     * @throws AccountDoesNotExistException     wenn der gegebene account nicht existiert
     * @throws TransactionAttributeException    wenn die Validierung der attribute fehlschlägt
     */
    @Override
    public void createAccount(String account, List<Transaction> transactions)
            throws AccountAlreadyExistsException, TransactionAlreadyExistException, TransactionAttributeException,IOException {

        this.createAccount(account);

        try {
            for (int i = 0; i < transactions.size(); i++) {

                this.addTransaction(account, transactions.get(i));
            }
        } catch (AccountDoesNotExistException e) {
            throw new AccountDoesNotExistException(e.getMessage());
        }
    }

    /**
     * Validiert die Attribute einer Transaktion nach den Bankvorgaben.
     *
     * @param transaction Die zu prüfende Transaktion
     * @throws TransactionAttributeException wenn Attribute ungültig sind
     */
    public void attributeValidation(Transaction transaction) throws TransactionAttributeException {


        if (transaction instanceof Payment p) {
            if (p.getIncomingInterest() < 0 || p.getIncomingInterest() > 1 ||
                    p.getOutgoingInterest() < 0 || p.getOutgoingInterest() > 1) {
                throw new TransactionAttributeException("IncomingInterest oder OutgoingInterest außerhalb des Wertebereiches (0-1)!");
            }
            if (p.getAmount() == 0) { //payment gleich 0 unsinn
                throw new TransactionAttributeException("Payment Amount darf nicht 0 sein!");
            }
        }

        if (transaction instanceof Transfer t) {
            if (t.getAmount() <= 0) { // Transfers müssen positiv sein
                throw new TransactionAttributeException("Transfer Amount muss positiv sein!");
            }
        }
    }


    /**
     * fügt eine neue transaction einem bestehenden account hinzu
     *
     * @param account     der account der transaction bekommt
     * @param transaction die hinzukommende transaction
     * @throws TransactionAlreadyExistException falls die transaction schon existiert
     * @throws AccountDoesNotExistException     wenn der account nicht existiert
     * @throws TransactionAttributeException    wenn die Validierung fehlschlägt
     */
    @Override
    public void addTransaction(String account, Transaction transaction) throws TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {
        if (!accountsToTransactions.containsKey(account))
            throw new AccountDoesNotExistException("Konto exestiert nicht");
        if (accountsToTransactions.get(account).contains(transaction))
            throw new TransactionAlreadyExistException("Transaction exestiert bereits");
        attributeValidation(transaction);

        if (transaction instanceof Payment p) { // zinswerte von alter bank überschreiben weil mit payment calc geabreitet wird
            p.setIncomingInterest(this.incomingInterest);
            p.setOutgoingInterest(this.outgoingInterest);
        }


        if (transaction instanceof Transfer t) {

            if (t.getSender().equals(account)) {
                transaction = new OutgoingTransfer(t); //überschriebt calc
            } else if (t.getRecipient().equals(account)) {
                transaction = new IncomingTransfer(t);
            }
        }

        accountsToTransactions.get(account).add(transaction);
        writeAccount(account); // Speichert das Konto mit der neuen Transaktion
    }


    /**
     * entfernt transaction vom account
     *
     * @param account     der account von dem die transaction entfernt wird
     * @param transaction zu entfernende transaction
     * @throws AccountDoesNotExistException     wenn der gegebene account nicht existiert
     * @throws TransactionDoesNotExistException wenn die transaction nicht existiert
     */
    @Override
    public void removeTransaction(String account, Transaction transaction) throws AccountDoesNotExistException, TransactionDoesNotExistException, IOException {
        if (!accountsToTransactions.containsKey(account))
            throw new AccountDoesNotExistException("Konto exestiert nicht");
        if (!accountsToTransactions.get(account).contains(transaction))
            throw new TransactionDoesNotExistException("Transaktion exestiert nicht");

        accountsToTransactions.get(account).remove(transaction);
        writeAccount(account);
    }


    /**
     * prüft die existenz der transaction auf einem account.
     *
     * @param account     der zu checkende account
     * @param transaction die zu überprüfende transaction
     */
    @Override
    public boolean containsTransaction(String account, Transaction transaction) {
        return accountsToTransactions.get(account).contains(transaction);
    }

    /**
     * gibt aktuellen Kontostand zurück
     *
     * @param account der aktuelle account
     * @return der aktuelle kontostand
     */
    @Override
    public double getAccountBalance(String account) {
        if (!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException("Konto '" + account + "' existiert nicht!");
        }
        double balance = 0.0;
        List<Transaction> transactions = accountsToTransactions.get(account);

        for (Transaction transaction : transactions) {
            balance += transaction.calculate();
        }

        return balance;
    }


    /**
     * gibt liste der transaktionen für account zurück
     *
     * @param account der ausgewählte account
     * @return liste aller transactions des accounts
     * @throws AccountDoesNotExistException
     */
    @Override
    public List<Transaction> getTransactions(String account) throws AccountDoesNotExistException {
        if (!accountsToTransactions.containsKey(account))
            throw new AccountDoesNotExistException("Konto existiert nicht!");
        return accountsToTransactions.get(account);
    }


    /**
     * gibt liste calculierter ammounts asc oder desc zurück oder leer
     *
     * @param account selektierter account
     * @param asc     auswahl ob aufsteigend sortiert werden soll
     * @return sortierte liste
     */
    @Override
    public List<Transaction> getTransactionsSorted(String account, boolean asc) {
        List<Transaction> transactions = getTransactions(account);
        if (asc) {
            transactions.sort(Comparator.comparingDouble(Transaction::calculate)); // Comparator(interface mit methode compare) nutzt calc() für jedes transaction obj
        } else {
            transactions.sort(Comparator.comparingDouble(Transaction::calculate).reversed());
        }
        return transactions;
    }


    /**
     * gibt eine liste von postiven oder negativen transactionen aus.
     *
     * @param account  selektierter accoung
     * @param positive auswahl für pos oder negative accounts
     * @return gibt liste nach typ zurück
     */
    @Override
    public List<Transaction> getTransactionsByType(String account, boolean positive) {
        List<Transaction> transactions = getTransactions(account);
        List<Transaction> gefiltert = new ArrayList<>();

        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            double calculatedAmount = t.calculate();
            if (positive && calculatedAmount >= 0) {
                gefiltert.add(t);
            } else if (!positive && calculatedAmount < 0) {
                gefiltert.add(t);
            }
        }
        return gefiltert;
    }

}
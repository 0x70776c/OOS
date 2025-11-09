package bank;

import bank.exceptions.*;

import java.util.*;

/**
 * Repräsentiert eine private Bank, die Konten und Transaktionen verwaltet.
 * Implementiert das Bank-Interface unter Verwendung von Incoming/OutgoingTransfer (Variante 1).
 */
public class PrivateBankAlt implements Bank {

    private String name;
    private double incomingInterest;
    private double outgoingInterest;

    /**
     * Map, die Kontonamen auf Listen von Transaktionen abbildet.
     * Wird direkt initialisiert.
     */
    private final Map<String, List<Transaction>> accountsToTransactions = new HashMap<>();


    /**
     * Standard-Konstruktor.
     *
     * @param name             Name der Bank
     * @param incomingInterest Einzahlungszins
     * @param outgoingInterest Auszahlungszins
     */
    public PrivateBankAlt(String name, double incomingInterest, double outgoingInterest) {
        this.name = name;
        this.incomingInterest = incomingInterest;
        this.outgoingInterest = outgoingInterest;
    }

    /**
     * Copy-Konstruktor.
     *
     * @param other Die zu kopierende PrivateBank
     */
    public PrivateBankAlt(PrivateBankAlt other) {
        this.name = other.name;
        this.incomingInterest = other.incomingInterest;
        this.outgoingInterest = other.outgoingInterest;
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
        return "PrivateBankAlt[" +
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

        PrivateBankAlt other = (PrivateBankAlt) obj;

        return Double.compare(other.incomingInterest, incomingInterest) == 0 &&
                Double.compare(other.outgoingInterest, outgoingInterest) == 0 &&
                Objects.equals(name, other.name) &&
                Objects.equals(accountsToTransactions, other.accountsToTransactions);
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
            if(p.getAmount() == 0){ //payment gleich 0 unsinn
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
     * fügt neuen account in liste hinzu
     *
     * @param account der geadded wird
     * @throws AccountAlreadyExistsException wenn der account bereits vorhanden ist
     */
    @Override
    public void createAccount(String account) throws AccountAlreadyExistsException {

        if (accountsToTransactions.containsKey(account)) {
            throw new AccountAlreadyExistsException("Konto '" + account + "' existiert bereits.");
        }
        accountsToTransactions.put(account, new ArrayList<>());
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
            throws AccountAlreadyExistsException, TransactionAlreadyExistException, TransactionAttributeException {

        this.createAccount(account);

        try {
            for (int i = 0; i < transactions.size(); i++) {

                this.addTransaction(account, transactions.get(i));
            }
        } catch (AccountDoesNotExistException e) {
            System.out.println(e);
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
    public void addTransaction(String account, Transaction transaction) throws TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException {
        if (!accountsToTransactions.containsKey(account))
            throw new AccountDoesNotExistException("Konto exestiert nicht");
        if (accountsToTransactions.get(account).contains(transaction))
            throw new TransactionAlreadyExistException("Transaction exestiert bereits");
        attributeValidation(transaction);

        if (transaction instanceof Payment p) {
            p.setIncomingInterest(this.incomingInterest);
            p.setOutgoingInterest(this.outgoingInterest);
        }

        //falls transfer wird in getAccbalance der recipent und sender manuell addiert/subtrahiert
        accountsToTransactions.get(account).add(transaction);

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
    public void removeTransaction(String account, Transaction transaction) throws AccountDoesNotExistException, TransactionDoesNotExistException {
        if (!accountsToTransactions.containsKey(account))
            throw new AccountDoesNotExistException("Konto exestiert nicht");
        if (!accountsToTransactions.get(account).contains(transaction))
            throw new TransactionDoesNotExistException("Transaktion exestiert nicht");

        accountsToTransactions.get(account).remove(transaction);
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
     * Implementierung von Variante 2:
     * Unterscheidung mit instanceof und Type-Casting.
     */
    @Override
    public double getAccountBalance(String account) {

        if (!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException("Konto '" + account + "' existiert nicht!");
        }
        double balance = 0.0;
        List<Transaction> transactions = accountsToTransactions.get(account);

            for(int i = 0; i<transactions.size();i++){

                Transaction t = transactions.get(i);

                if (t instanceof Transfer transfer) { //prüfung inkl. cast
                if (transfer.getSender().equals(account)) {
                    balance -= transfer.getAmount();
                } else if (transfer.getRecipient().equals(account)) {
                    balance += transfer.getAmount();
                }
            } else {
                balance += t.calculate();
            }
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
            transactions.sort(Comparator.comparingDouble(Transaction::calculate));
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
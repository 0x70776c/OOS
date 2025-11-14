import bank.*;
import bank.exceptions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Testklasse für Praktikum 3 (Aufgabe 3).
 * Testet die Funktionalität von PrivateBank und PrivateBankAlt.
 */
public class Main {

    public static void main(String[] args)  {
        System.out.println("--- OBJEKTE ERZEUGEN ---");
        PrivateBank bankA = new PrivateBank("Bank A ", 0.05, 0.03);
        PrivateBankAlt bankAlt = new PrivateBankAlt("Bank Alt ", 0.05, 0.03);
        System.out.println("OK: \n" + bankA.getName() + "erstellt,\n " + bankAlt.getName() + "erstellt.\n");




        System.out.println("----------------------------------------------");
        System.out.println("--- 2. Exceptiontests ---");


        System.out.println("\n--- Test: AccountAlreadyExistsException ---");
        try {
            bankA.createAccount("KontoThomasV1");
            bankAlt.createAccount("KontoV2");
            bankA.createAccount("Konto-Doppelt"); // 1. Erstellen (OK)
            bankA.createAccount("Konto-Doppelt"); // 2. Erstellen (FEHLER)

            System.out.println("FEHLER: Exception wurde NICHT geworfen!");
        } catch (AccountAlreadyExistsException e) {
            System.out.println("OK: Erwartete Exception gefangen: " + e.getMessage());
        }



        System.out.println("\n--- Test: AccountDoesNotExistException ---");
        try {

            Payment p = new Payment("01.01.2025", 100, "Test", 0.1, 0.1);
            bankA.addTransaction("Konto-GIBTS-NICHT", p);

        } catch (AccountDoesNotExistException e) {
            System.out.println("OK: Erwartete Exception gefangen: " + e.getMessage());
        }
        catch (TransactionAlreadyExistException e){
            System.out.println("Nicht OK:  Exception gefangen: " + e.getMessage());
        }
        catch (TransactionAttributeException e){
            System.out.println("Nicht OK:  Exception gefangen: " + e.getMessage());

        }



        System.out.println("\n--- Test: TransactionAttributeException (Zins) ---");
        try {
            Payment p = new Payment("02.01.2025", 100, "Zins > 1", 1.5, 0.0);
            bankA.addTransaction("Konto-Doppelt", p);

        } catch (TransactionAttributeException e) {
            System.out.println("OK: Erwartete Exception gefangen: " + e.getMessage());
        }
        catch (AccountDoesNotExistException e) {
            System.out.println("Nicht OK:  Exception gefangen: " + e.getMessage());
        }
        catch (TransactionAlreadyExistException e){
            System.out.println("Nicht OK:  Exception gefangen: " + e.getMessage());
        }



        System.out.println("\n--- Test: TransactionAlreadyExistException ---");
        try {
            Payment p = new Payment("01.01.2025", 100, "Test", 0.1, 0.1);
            bankA.addTransaction("Konto-Doppelt",p);
            bankA.addTransaction("Konto-Doppelt", p);

        } catch (TransactionAttributeException e) {
            System.out.println("Nicht OK:  Exception gefangen: " + e.getMessage());
        }
        catch (AccountDoesNotExistException e) {
            System.out.println("Nicht OK:  Exception gefangen: " + e.getMessage());
        }
        catch (TransactionAlreadyExistException e){
            System.out.println("OK: konto existiert bereits: " + e.getMessage());
        }


        System.out.println("\n--- Test: TransactionDoesNotExistException ---");
        try {
            Payment p_fremd = new Payment("05.01.2025", 99, "irgendwas", 0.1, 0.1);
            bankA.removeTransaction("Konto-Doppelt", p_fremd);

        } catch (bank.exceptions.TransactionDoesNotExistException e) {
            System.out.println("OK: Erwartete Exception gefangen: " + e.getMessage());
        } catch (AccountDoesNotExistException e) {
            System.out.println("Nicht OK: Erwartete Exception gefangen: " + e.getMessage());
        }



        System.out.println("----------------------------------------------");
        System.out.println("--- 3. Methodentests ---");


        try {
            System.out.println(" PrivateBank (V1)");

            bankA.addTransaction("KontoThomasV1", new Payment("05.11.2025", 1000, "Gehalt", 0.0, 0.0));
            bankA.addTransaction("KontoThomasV1", new Payment("05.11.2025", -200, "Strom", 0.0, 0.0));
            bankA.addTransaction("KontoThomasV1", new Transfer("05.11.2025", 300, "Miete", "KontoThomasV1", "Vermieter"));
            bankA.addTransaction("KontoThomasV1", new Transfer("05.11.2025", 50, "Geschenk", "Oma", "KontoThomasV1"));

            double balanceV1 = bankA.getAccountBalance("KontoThomasV1");
            System.out.println("OK: Bank A (V1) Saldo: " + balanceV1 + " (Erwartet: 494.0)");


            System.out.println("\nTeste PrivateBankAlt (V2)...");

            bankAlt.addTransaction("KontoV2", new Payment("05.11.2025", 1000, "Gehalt", 0.0, 0.0));
            bankAlt.addTransaction("KontoV2", new Payment("05.11.2025", -200, "Strom", 0.0, 0.0));
            bankAlt.addTransaction("KontoV2", new Transfer("05.11.2025", 300, "Miete", "KontoV2", "Vermieter"));
            bankAlt.addTransaction("KontoV2", new Transfer("05.11.2025", 50, "Geschenk", "Oma", "KontoV2"));

            double balanceV2 = bankAlt.getAccountBalance("KontoV2");
            System.out.println("OK: Bank Alt (V2) Saldo: " + balanceV2 + " (Erwartet: 494.0)");

            System.out.println("\nTeste getTransactionsSorted (absteigend)...");
            System.out.println(bankA.getTransactionsSorted("KontoThomasV1", false));

        } catch (AccountDoesNotExistException | TransactionAlreadyExistException | TransactionAttributeException  | TransactionDoesNotExistException e) {
            System.out.println("darf nicht vorkommen "+ e);
        }



        PrivateBank BankTest = new PrivateBank("BankTest", 0.02, 0.03);

        try {
            BankTest.createAccount("leererAcc");
        }catch(AccountAlreadyExistsException e){
            System.out.println("Darf nicht geworfen werden"+ e);
        }

        List<Transaction> testListe = new ArrayList<>();
        Transfer t1 = new Transfer("1.1.2000",12,"t1","Testsender","leererAcc2");
        testListe.add(t1);
        try {
            BankTest.createAccount("leererAcc2",testListe);
        }catch(AccountAlreadyExistsException  | TransactionAlreadyExistException |TransactionAttributeException e){
            System.out.println("Darf nicht geworfen werden"+ e.getMessage());
        }


        try{
            BankTest.attributeValidation(testListe.get(0));
        }catch (TransactionAttributeException e){
            System.out.println("Darf nicht geworfen werden"+ e.getMessage());
        }


        Transfer neuer = new Transfer("1.1.2000",12,"t1","Test","test");

        try{
            BankTest.addTransaction("leererAcc",neuer);
        }catch (TransactionAlreadyExistException| AccountDoesNotExistException | TransactionAttributeException e){
            System.out.println("Darf nicht geworfen werden"+ e.getMessage());
        }

        try {
            BankTest.removeTransaction("leererAcc",neuer);
        }catch (TransactionDoesNotExistException | AccountDoesNotExistException e)
        {
            System.out.println("Darf nicht geworfen werden"+ e.getMessage());
        }


        System.out.println("----------------------------------------------");
        System.out.println("--- ZUSÄTZLICHE TESTS ---");

        System.out.println("1. containsTransaction: " + BankTest.containsTransaction("leererAcc2",t1));

        System.out.println("2. getAccountBalance (leererAcc): " + BankTest.getAccountBalance("leererAcc2"));

        System.out.println("3. getTransactions (leererAcc2): " + BankTest.getTransactions("leererAcc2"));

        System.out.println("4. getTransactionsSorted (leererAcc2, ASC): " + BankTest.getTransactionsSorted("leererAcc2",true));

        System.out.println("5. getTransactionsByType (leererAcc2, POS): " + BankTest.getTransactionsByType("leererAcc2",true));

        System.out.println("----------------------------------------------");
        System.out.println("--- 4. TEST PrivateBank#equals ---");


        PrivateBank bankVG = new PrivateBank("BankVG", 0.02, 0.01);
        PrivateBank bankVGCopy = new PrivateBank("BankVG", 0.02, 0.01);
        PrivateBank bank1 = new PrivateBank("Bank1", 0.05, 0.03);


        System.out.println("Test 1 (identisch, leer): bankVG == BankVGCopy? "
                + bankVG.equals(bankVGCopy) + " (Erwartet: true)");


        System.out.println("Test 2 (unterschiedl. Name): bankVG==bank1 "
                + bankVG.equals(bank1) + " (Erwartet: false)");


        try {
            bankVG.createAccount("Konto-A");
            System.out.println("Test 3 (Konto hinzugefügt): bankVG.equals(bankVGCopy)? "
                    + bankVG.equals(bankVGCopy) + " (Erwartet: false)");


            bankVGCopy.createAccount("Konto-A");

            bankVG.addTransaction("Konto-A",
                    new Payment("01.11.2025", 100, "Gehalt", 0, 0));

            bankVGCopy.addTransaction("Konto-A",
                    new Payment("01.11.2025", 100, "Gehalt", 0, 0));

            System.out.println("Test 4 (Ident. Transaktionen): bankVG.equals(bankVGCopy)? "
                    + bankVG.equals(bankVGCopy) + " (Erwartet: true)");


        }catch(AccountAlreadyExistsException|AccountDoesNotExistException |TransactionAlreadyExistException |TransactionAttributeException e){
            System.out.println("Darf nicht geworfen werden"+ e.getMessage());
        }

        System.out.println("----------------------------------------------");
        System.out.println("--- TESTS ABGESCHLOSSEN ---");
    }
}
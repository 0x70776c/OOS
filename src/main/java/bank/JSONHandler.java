package bank;

import com.google.gson.*;

import java.lang.reflect.Type;

public class JSONHandler implements JsonDeserializer<Transaction>, JsonSerializer<Transaction> {
    /**
     * Deserialisiert ein JsonElement (in unserem verschachtelten Format)
     * zurück in ein konkretes Transaction-Objekt (Payment, Transfer etc.).
     *
     * @param jsonElement Das JSON-Element, das gelesen wird
     * @param type        Der Typ (immer Transaction.class)
     * @param context     Der Gson-Kontext (wird hier nicht direkt gebraucht)
     * @return Ein Payment-, IncomingTransfer- oder OutgoingTransfer-Objekt
     * @throws JsonParseException wenn CLASSNAME fehlt oder unbekannt ist
     */
    @Override
    public Transaction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {

        // 1. Hol dir das äußere Objekt
        JsonObject obj = jsonElement.getAsJsonObject();

        // 2. Hol dir den Typ-Namen (z.B. "Payment")
        JsonElement classElement = obj.get("CLASSNAME");
        if (classElement == null) {
            throw new JsonParseException("Fehlendes 'CLASSNAME'-Feld im JSON-Objekt.");
        }
        String className = classElement.getAsString();

        // 3. Hol dir das innere Objekt (INSTANCE) mit den Daten
        JsonElement instanceElement = obj.get("INSTANCE");
        if (instanceElement == null) {
            throw new JsonParseException("Fehlendes 'INSTANCE'-Feld im JSON-Objekt.");
        }
        JsonObject instance = instanceElement.getAsJsonObject();

        // 4. Lies die Basis-Attribute aus, die *alle* Typen haben
        String date = instance.get("date").getAsString();
        double amount = instance.get("amount").getAsDouble();
        String description = instance.get("description").getAsString();

        // 5. Unterscheide anhand des CLASSNAME, welches Objekt wir bauen müssen

        if (className == "Payment") {
            // a) Lies die zusätzlichen Payment-Attribute
            double inInterest = instance.get("incomingInterest").getAsDouble();
            double outInterest = instance.get("outgoingInterest").getAsDouble();

            // b) Baue und returne das Payment-Objekt
            return new Payment(date, amount, description, inInterest, outInterest);
        } else if (className == "IncomingTransfer") {
            // a) Lies die zusätzlichen Transfer-Attribute
            String sender = instance.get("sender").getAsString();
            String recipient = instance.get("recipient").getAsString();

            // b) Baue das Objekt (genau wie in P3 gelernt)
            Transfer t = new Transfer(date, amount, description, sender, recipient);
            return new IncomingTransfer(t);
        } else if (className == "OutgoingTransfer") {
            // a) Lies die zusätzlichen Transfer-Attribute
            String sender = instance.get("sender").getAsString();
            String recipient = instance.get("recipient").getAsString();

            // b) Baue das Objekt (genau wie in P3 gelernt)
            Transfer t = new Transfer(date, amount, description, sender, recipient);
            return new OutgoingTransfer(t);
        } else {
            // Wenn wir den Typ nicht kennen (z.B. "BonusPayment"), werfen wir einen Fehler.
            throw new JsonParseException("Unbekannter CLASSNAME beim Deserialisieren: " + className);
        }

    }


    /**
     * Serialisiert ein Transaction-Objekt (Payment, Transfer etc.)
     * in das vorgegebene JSON-Format mit CLASSNAME und INSTANCE.
     */
    @Override
    public JsonElement serialize(Transaction transaction, Type type, JsonSerializationContext context) {

        JsonObject jsonOuterObject = new JsonObject();
        JsonObject jsonInnerObject = new JsonObject(); // Das wird "INSTANCE"

        // Basis-Attribute füllen
        jsonInnerObject.addProperty("date", transaction.getDate());
        jsonInnerObject.addProperty("amount", transaction.getAmount());
        jsonInnerObject.addProperty("description", transaction.getDescription());

        // Spezifische Attribute füllen
        if (transaction instanceof Payment payment) {
            jsonOuterObject.addProperty("CLASSNAME", "Payment");
            jsonInnerObject.addProperty("incomingInterest", payment.getIncomingInterest());
            jsonInnerObject.addProperty("outgoingInterest", payment.getOutgoingInterest());

        } else if (transaction instanceof IncomingTransfer incoming) {
            jsonOuterObject.addProperty("CLASSNAME", "IncomingTransfer");
            jsonInnerObject.addProperty("sender", incoming.getSender());
            jsonInnerObject.addProperty("recipient", incoming.getRecipient());

        } else if (transaction instanceof OutgoingTransfer outgoing) {
            jsonOuterObject.addProperty("CLASSNAME", "OutgoingTransfer");
            jsonInnerObject.addProperty("sender", outgoing.getSender());
            jsonInnerObject.addProperty("recipient", outgoing.getRecipient());
        }

        jsonOuterObject.add("INSTANCE", jsonInnerObject);
        return jsonOuterObject;
    }
}
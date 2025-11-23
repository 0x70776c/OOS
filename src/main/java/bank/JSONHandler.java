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

        JsonObject obj = jsonElement.getAsJsonObject();

        JsonElement classElement = obj.get("CLASSNAME");
        if (classElement == null) {
            throw new JsonParseException("Fehlendes 'CLASSNAME'-Feld im JSON-Objekt.");
        }

        // --- KORREKTUR 1: .trim() hinzugefügt ---
        String className = classElement.getAsString().trim(); // Entfernt Leerzeichen

        JsonElement instanceElement = obj.get("INSTANCE");
        if (instanceElement == null) {
            throw new JsonParseException("Fehlendes 'INSTANCE'-Feld im JSON-Objekt.");
        }
        JsonObject instance = instanceElement.getAsJsonObject();

        String date = instance.get("date").getAsString();
        double amount = instance.get("amount").getAsDouble();
        String description = instance.get("description").getAsString();

        // --- KORREKTUR 2: .equals() statt == ---

        if (className.equals("Payment")) {
            double inInterest = instance.get("incomingInterest").getAsDouble();
            double outInterest = instance.get("outgoingInterest").getAsDouble();
            return new Payment(date, amount, description, inInterest, outInterest);

        } else if (className.equals("IncomingTransfer")) {
            String sender = instance.get("sender").getAsString();
            String recipient = instance.get("recipient").getAsString();
            Transfer t = new Transfer(date, amount, description, sender, recipient);
            return new IncomingTransfer(t);

        } else if (className.equals("OutgoingTransfer")) {
            String sender = instance.get("sender").getAsString();
            String recipient = instance.get("recipient").getAsString();
            Transfer t = new Transfer(date, amount, description, sender, recipient);
            return new OutgoingTransfer(t);

        } else {
            // Dieser Fehler wird jetzt nur noch geworfen, wenn der Typ WIRKLICH unbekannt ist
            // (oder "Payment " hieß und du .trim() nicht hinzugefügt hast)
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
        JsonObject jsonInnerObject = new JsonObject();

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
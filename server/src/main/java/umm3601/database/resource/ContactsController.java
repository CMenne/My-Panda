package umm3601.database.resource;

import com.google.gson.Gson;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.Iterator;
import java.util.Map;


import static com.mongodb.client.model.Filters.eq;

public class ContactsController {
    private final Gson gson;
    private MongoDatabase database;
    private final MongoCollection<Document> contactsCollection;

    /**
     * Construct a controller for contacts.
     *
     * @param database the database containing contacts data
     */
    public ContactsController(MongoDatabase database) {
        gson = new Gson();
        this.database = database;
        contactsCollection = database.getCollection("contacts");
    }

    public String getContact(String id) {

        FindIterable<Document> jsonContacts
            = contactsCollection
            .find(eq("_id", new ObjectId(id)));

        Iterator<Document> iterator = jsonContacts.iterator();
        if (iterator.hasNext()) {
            Document contact = iterator.next();
            return contact.toJson();
        } else {
            // We didn't find the desired Contact
            return null;
        }
    }


    public String getContacts(Map<String, String[]> queryParams) {
        Document filterDoc = new Document();

        if (queryParams.containsKey("userID")) {
            String targetContent = (queryParams.get("userID")[0]);
            Document contentRegQuery = new Document();
            contentRegQuery.append("$regex", targetContent);
            contentRegQuery.append("$options", "i");
            filterDoc = filterDoc.append("userID", targetContent);
        }

        else {
            System.out.println("It had no userID");
            return JSON.serialize("[ ]");
        }

        FindIterable<Document> matchingContacts = contactsCollection.find(filterDoc);


        return JSON.serialize(matchingContacts);
    }


    public String addNewContacts( String userID, String name, String email, String phone) {

        Document newContacts = new Document();
        newContacts.append("userID", userID);
        newContacts.append("name", name);
        newContacts.append("email", email);
        newContacts.append("phone", phone);




        try {
            contactsCollection.insertOne(newContacts);

            ObjectId id = newContacts.getObjectId("_id");
            System.err.println("Successfully added new contact [_id=" + id + ",userID=" + userID + " name=" + name + ", email=" + email + " phone=" + phone + ']');

            return JSON.serialize(id);
        } catch (MongoException me) {
            me.printStackTrace();
            return null;
        }
    }

    public void deleteContact(String id){
        Document searchQuery = new Document().append("_id", new ObjectId(id));
        System.out.println("Journal id: " + id);
        try {
            contactsCollection.deleteOne(searchQuery);
            ObjectId theID = searchQuery.getObjectId("_id");
            System.out.println("Succesfully deleted contact with ID: " + theID);

        } catch(MongoException me) {
            me.printStackTrace();
            System.out.println("error");
        }
    }

}

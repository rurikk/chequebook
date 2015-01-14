package chequebook;

import java.io.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

/**
 * Created by rurik
 */
public class Bank implements Serializable {
    public static final File dataFile = new File(System.getProperty("user.home"), "bank.bin");
    public static final Bank instance = bootstrap();

    private final Map<String, Person> persons = new HashMap<>();
    private String adminKey = "admin";

    private static Bank bootstrap() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
            return (Bank) ois.readObject();
        } catch (Exception ignored) {
            return new Bank();
        }
    }

    private synchronized void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile))) {
            oos.writeObject(this);
        } catch (Exception ignored) {
        }
    }

    public synchronized List<Person> getPersons() {
        return new ArrayList<>(persons.values());
    }

    public synchronized void addTransaction(Instant time, Person p1, Person p2, BigDecimal amount, String comment) {
        p1.getTransactions().add(new Transaction(time, p2, amount, comment));
        p2.getTransactions().add(new Transaction(time, p1, amount.negate(), comment));
        save();
    }

    public synchronized Person findPerson(String key) {
        Person p = persons.get(key);
        if (p == null) throw new IllegalArgumentException(key);
        return p;
    }

    public synchronized Person addPerson(String name) {
        Person person = new Person(UUID.randomUUID().toString(), name);
        persons.put(person.getKey(), person);
        save();
        return person;
    }

    public synchronized boolean isAdmin(String ctx) {
        return Objects.equals(ctx, adminKey);
    }

    public synchronized String generateAdminKey() {
        adminKey = UUID.randomUUID().toString();
        save();
        return adminKey;
    }
}

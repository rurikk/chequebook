package chequebook;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

/**
 * Created by rurik
 */
public class Bank {
    public static Bank instance = bootstrap();
    public Map<String, Person> persons = new HashMap<>();

    private static Bank bootstrap() {
        Bank bank = new Bank();
        bank.persons.put("admin", new Person("admin", "Admin", true));

        debug(bank);

        return bank;
    }

    private static void debug(Bank bank) {
        Person a = bank.findPerson("admin");
        Person p = bank.addPerson();
        p.setName("Test 1");

        bank.addTransaction(Instant.now(), a, p, new BigDecimal("23.45"), "Comment1");
        bank.addTransaction(Instant.now(), p, a, new BigDecimal("24.55"), "Comment2");
    }

    public synchronized List<Person> getPersons() {
        return new ArrayList<>(persons.values());
    }

    public synchronized void addTransaction(Instant time, Person p1, Person p2, BigDecimal amount, String comment) {
        p1.transactions.add(new Transaction(time, p2, amount, comment));
        p2.transactions.add(new Transaction(time, p1, amount.negate(), comment));
    }

    public synchronized Person findPerson(String key) {
        Person p = persons.get(key);
        if (p == null) throw new IllegalArgumentException(key);
        return p;
    }

    public synchronized Person addPerson() {
        Person person = new Person(UUID.randomUUID().toString(), "Новичок", false);
        persons.put(person.key, person);
        return person;
    }
}

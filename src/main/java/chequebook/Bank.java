package chequebook;

import java.math.BigDecimal;
import java.math.MathContext;
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
        ArrayList<Person> list = new ArrayList<>();
        list.add(bank.findPerson("admin"));
        for (int i = 0; i < 100; i++) {
            Person p = bank.addPerson();
            p.setName("Test " + i);
            list.add(p);
        }
        long start = System.currentTimeMillis();

        for (Person p1 : list) {
            for (Person p2 : list) {
                for (int i = 0; i < 10; i++) {
                    bank.addTransaction(Instant.now(), p1, p2, new BigDecimal(Math.random() * 1000, new MathContext(4)),
                            "Comment " + p1.getName() + " - " + p2.getName());
                }
            }
        }
        System.out.println(System.currentTimeMillis() - start);
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

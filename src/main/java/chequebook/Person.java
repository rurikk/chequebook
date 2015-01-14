package chequebook;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rurik
 */
public class Person implements Serializable {
    private final String key;
    private final String name;
    private final List<Transaction> transactions = new ArrayList<>();

    public Person(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public BigDecimal getBalance() {
        BigDecimal sum = BigDecimal.ZERO;
        for (Transaction t : transactions) {
            sum = sum.add(t.getAmount());
        }
        return sum;
    }
}

package chequebook;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rurik
 */
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;

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
        return sum.negate();
    }

    public BigDecimal getChange() {
        BigDecimal sum = BigDecimal.ZERO;
        Instant dayBefore = Instant.now().minus(12, ChronoUnit.HOURS);
        for (Transaction t : transactions) {
            if (t.getCreated().isAfter(dayBefore)) {
                sum = sum.add(t.getAmount());
            }
        }
        return sum.negate();
    }
}

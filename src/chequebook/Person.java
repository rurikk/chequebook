package chequebook;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rurik
 */
public class Person implements Serializable {
    public String key;
    public String name;
    public List<Transaction> transactions = new ArrayList<>();
    boolean admin;

    public Person(String key, String name, boolean admin) {
        this.key = key;
        this.name = name;
        this.admin = admin;
    }

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    BigDecimal getBalance() {
        BigDecimal sum = BigDecimal.ZERO;
        for (Transaction t : transactions) {
            if (t.peer == this) sum = sum.add(t.amount);
        }
        return sum;
    }
}

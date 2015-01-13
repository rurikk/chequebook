package chequebook;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Created by rurik
 */
public class Transaction {
    Instant created;
    Person peer;
    BigDecimal amount;
    String comment;

    public Transaction(Instant created, Person peer, BigDecimal amount, String comment) {
        this.created = created;
        this.peer = peer;
        this.amount = amount;
        this.comment = comment;
    }

    public Instant getCreated() {
        return created;
    }

    public Person getPeer() {
        return peer;
    }

    public String getPeerName() {
        return peer.name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getComment() {
        return comment;
    }
}

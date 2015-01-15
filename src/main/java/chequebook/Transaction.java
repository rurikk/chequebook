package chequebook;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Created by rurik
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Instant created;
    private final Person peer;
    private final BigDecimal amount;
    private final String comment;

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
        return peer.getName();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getComment() {
        return comment;
    }
}

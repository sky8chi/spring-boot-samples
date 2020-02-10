package com.kxfo.springboot.samples.distributed.jta.dto;

import java.math.BigDecimal;

/**
 * TODO
 *
 * @author by tianxiang.chi
 * @date 2020-02-10 12:56
 */
public class TransferLog {
    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;

    public TransferLog(String fromAccountId, String toAccountId,
                       BigDecimal amount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public String getToAccountId() {
        return toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}

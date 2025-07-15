package com.tag.biometric.ifService.config.exception;

/**
 * @author: Ananda KC
 * Created On: 13/05/2025
 */
public class TransactionHistoryFailedException extends RuntimeException {
    public TransactionHistoryFailedException() {
        super("Card TransactionHistory failed for your given account/card id.");
    }

    public TransactionHistoryFailedException(Long id) {
        super(String.format("Card TransactionHistory failed for your given account/card id : %s.", id));
    }

    public TransactionHistoryFailedException(String message) {
        super(message);
    }
}
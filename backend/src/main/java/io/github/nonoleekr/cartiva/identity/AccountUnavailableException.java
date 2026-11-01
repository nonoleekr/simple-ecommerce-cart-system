package io.github.nonoleekr.cartiva.identity;

public class AccountUnavailableException extends RuntimeException {
    public AccountUnavailableException() {
        super("Account is not active");
    }
}

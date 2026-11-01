package io.github.nonoleekr.cartiva.identity;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid username, email, or password");
    }
}

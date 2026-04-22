package io.github.toptl.exception;

/** 401/403 — invalid or missing API key, or the key is missing the required scope. */
public class AuthenticationException extends TopTLException {

    public AuthenticationException(String message, int statusCode, String responseBody) {
        super(message, statusCode, responseBody);
    }
}

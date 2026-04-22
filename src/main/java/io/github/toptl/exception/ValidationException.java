package io.github.toptl.exception;

/** 4xx (other than 401/403/404/429) — the payload was rejected by the server. */
public class ValidationException extends TopTLException {

    public ValidationException(String message, int statusCode, String responseBody) {
        super(message, statusCode, responseBody);
    }
}

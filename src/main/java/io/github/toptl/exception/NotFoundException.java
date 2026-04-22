package io.github.toptl.exception;

/** 404 — the listing or resource does not exist. */
public class NotFoundException extends TopTLException {

    public NotFoundException(String message, int statusCode, String responseBody) {
        super(message, statusCode, responseBody);
    }
}

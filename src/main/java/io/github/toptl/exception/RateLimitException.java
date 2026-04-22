package io.github.toptl.exception;

/** 429 — API rate limit hit. Back off and retry. */
public class RateLimitException extends TopTLException {

    public RateLimitException(String message, int statusCode, String responseBody) {
        super(message, statusCode, responseBody);
    }
}

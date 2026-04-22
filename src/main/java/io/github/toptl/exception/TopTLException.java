package io.github.toptl.exception;

/**
 * Base class for every exception thrown by the TOP.TL SDK.
 *
 * <p>Subclasses narrow down the HTTP failure mode:
 * {@link AuthenticationException} for 401/403,
 * {@link NotFoundException} for 404,
 * {@link RateLimitException} for 429,
 * {@link ValidationException} for other 4xx.</p>
 */
public class TopTLException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public TopTLException(String message) {
        this(message, 0, null, null);
    }

    public TopTLException(String message, Throwable cause) {
        this(message, 0, null, cause);
    }

    public TopTLException(String message, int statusCode, String responseBody) {
        this(message, statusCode, responseBody, null);
    }

    public TopTLException(String message, int statusCode, String responseBody, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /** HTTP status code the server returned, or {@code 0} for non-HTTP errors. */
    public int getStatusCode() {
        return statusCode;
    }

    /** Raw response body as returned by the server, or {@code null} when unavailable. */
    public String getResponseBody() {
        return responseBody;
    }
}

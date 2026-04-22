package io.github.toptl.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Response from {@code POST /v1/listing/{username}/webhook/test}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookTestResult {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("statusCode")
    @JsonAlias({"status"})
    private Integer statusCode;

    @JsonProperty("message")
    @JsonAlias({"error"})
    private String message;

    public WebhookTestResult() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public Integer getStatusCode() { return statusCode; }
    public void setStatusCode(Integer statusCode) { this.statusCode = statusCode; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @Override
    public String toString() {
        return "WebhookTestResult{success=" + success
                + ", statusCode=" + statusCode
                + ", message='" + message + "'}";
    }
}

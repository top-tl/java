package io.github.toptl.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Webhook configuration registered via
 * {@code PUT /v1/listing/{username}/webhook}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebhookConfig {

    @JsonProperty("url")
    @JsonAlias({"webhookUrl"})
    private String url;

    @JsonProperty("rewardTitle")
    private String rewardTitle;

    public WebhookConfig() {}

    public WebhookConfig(String url) {
        this.url = url;
    }

    public WebhookConfig(String url, String rewardTitle) {
        this.url = url;
        this.rewardTitle = rewardTitle;
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getRewardTitle() { return rewardTitle; }
    public void setRewardTitle(String rewardTitle) { this.rewardTitle = rewardTitle; }

    @Override
    public String toString() {
        return "WebhookConfig{url='" + url + "', rewardTitle='" + rewardTitle + "'}";
    }
}

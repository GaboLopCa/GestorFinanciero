package com.gabriel.gestorfinanciero.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "webhook")
public class WebhookConfigProperties {

    private String secret;
    private String defaultEmail = "demo@test.com";

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }

    public String getDefaultEmail() { return defaultEmail; }
    public void setDefaultEmail(String defaultEmail) { this.defaultEmail = defaultEmail; }
}

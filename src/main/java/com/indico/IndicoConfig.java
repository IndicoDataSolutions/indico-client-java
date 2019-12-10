package com.indico;

/**
 * Indico client configuration
 *
 * Use the builder to modify the config and pass this object to the IndicoClient constructor
 */
public class IndicoConfig {
    public final String apiToken;
    public final String host;

    public static class Builder {
        protected  String apiToken;
        protected  String host = "app.indico.io";

        public Builder apiToken(String apiToken) {
            this.apiToken = apiToken;
            return this;
        }

        public Builder  host(String host) {
            this.host = host;
            return this;
        }

        public IndicoConfig build() {
            IndicoConfig config = new IndicoConfig(this);
            return config;
        }
    }
    
    private IndicoConfig(Builder builder){
        this.apiToken = builder.apiToken;
        this.host = builder.host;
    }
}
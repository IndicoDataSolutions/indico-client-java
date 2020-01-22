package com.indico;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Indico client configuration
 *
 * Use the builder to modify the config and pass this object to the IndicoClient
 * constructor
 */
public class IndicoConfig {

    public final String apiToken;
    public final String host;
    public final String protocol;
    public final int maxConnections;
    public final int connectionReadTimeout;
    public final int connectionWriteTimeout;

    public static class Builder {

        protected String apiToken;
        protected String host = "app.indico.io";
        protected String protocol = "https";
        protected int maxConnections = 10;
        protected int connectionReadTimeout = 60;
        protected int connectionWriteTimeout = 60;

        public Builder apiToken(String apiToken) {
            this.apiToken = apiToken;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder maxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        public Builder connectionReadTimeout(int connectionReadTimeout) {
            this.connectionReadTimeout = connectionReadTimeout;
            return this;
        }

        public Builder connectionWriteTimeout(int connectionWriteTimeout) {
            this.connectionWriteTimeout = connectionWriteTimeout;
            return this;
        }

        public Builder tokenPath(String tokenPath) throws IOException {
            this.apiToken = this.resolveApiToken(tokenPath);
            return this;
        }

        public IndicoConfig build() {
            IndicoConfig config = new IndicoConfig(this);
            return config;
        }

        /**
         *
         * Returns api token from indico_api_token.txt from path
         *
         * @param path path to indico_api_token.txt
         * @return api token from indico_api_token.txt
         * @throws IOException
         */
        private String resolveApiToken(String path) throws IOException {
            String apiTokenPath = path;
            File apiTokenFile = new File(apiTokenPath.concat("/indico_api_token.txt"));
            if (!(apiTokenFile.exists() && apiTokenFile.isFile())) {
                throw new RuntimeException("File " + apiTokenFile.getPath() + " not found.");
            } else {
                try (BufferedReader reader = new BufferedReader(new FileReader(apiTokenFile))) {
                    apiToken = reader.readLine();
                }
            }
            return apiToken.trim();
        }
    }

    private IndicoConfig(Builder builder) {
        this.apiToken = builder.apiToken;
        this.host = builder.host;
        this.protocol = builder.protocol;
        this.maxConnections = builder.maxConnections;
        this.connectionWriteTimeout = builder.connectionWriteTimeout;
        this.connectionReadTimeout = builder.connectionReadTimeout;
    }
}

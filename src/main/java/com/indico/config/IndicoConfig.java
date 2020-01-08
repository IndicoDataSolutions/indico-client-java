package com.indico.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class IndicoConfig {

    public final String host;
    public final String protocol;
    public final String apiToken;
    public final int maxConnections;

    public static class Builder {

        protected String tokenPath = null;
        protected String host = "app.indico.io";
        protected String protocol = "https";
        protected int maxConnections = 10;

        /**
         *
         * @param tokenPath path to indico_api_token.txt, defaults to null
         * @return Builder instance
         */
        public Builder tokenPath(String tokenPath) {
            this.tokenPath = tokenPath;
            return this;
        }

        /**
         *
         * @param host host for indico graphql api, defaults to app.indico.io
         * @return Builder instance
         */
        public Builder host(String host) {
            this.host = host;
            return this;
        }

        /**
         *
         * @param protocol protocol to communicate with host, defaults to https
         * @return Builder instance
         */
        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        /**
         *
         * @param maxConnections maximum connections threads allowed from an
         * instance of IndicoClient
         * @return
         */
        public Builder maxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        /**
         * returns an instance of IndicoConfig to pass to Indico constructor
         *
         * @return instance to IndicoConfig
         * @throws FileNotFoundException
         * @throws IOException
         */
        public IndicoConfig build() throws FileNotFoundException, IOException {
            IndicoConfig config = new IndicoConfig(this);
            return config;
        }

    }

    private IndicoConfig(Builder builder) throws FileNotFoundException, IOException {
        this.host = builder.host;
        this.protocol = builder.protocol;
        this.maxConnections = builder.maxConnections;
        this.apiToken = this.resolveApiToken(builder.tokenPath);
    }

    /**
     *
     * Returns api token from indico_api_token.txt from path, project root, home
     * directory or INDICO_API_TOKEN_PATH
     *
     * @param path path to indico_api_token.txt
     * @return api token from indico_api_token.txt
     * @throws FileNotFoundException
     * @throws IOException
     * @see Authentication#resolveApiToken()
     */
    private String resolveApiToken(String path) throws FileNotFoundException, IOException {
        String apiTokenPath;
        final String apiToken;
        if (path != null) {
            apiTokenPath = path;
        } else {
            apiTokenPath = System.getenv("INDICO_API_TOKEN_PATH");
        }

        if (apiTokenPath == null) {
            apiTokenPath = ".";
            if (!new File(apiTokenPath + "/indico_api_token.txt").exists()) {
                apiTokenPath = System.getProperty("user.home");
            }
        }

        File apiTokenFile = new File(apiTokenPath.concat("/indico_api_token.txt"));
        if (!(apiTokenFile.exists() && apiTokenFile.isFile())) {
            throw new RuntimeException("Expected indico_api_token.txt in current directory, home directory "
                    + "or provided as INDICO_API_TOKEN_PATH");
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(apiTokenFile))) {
                apiToken = reader.readLine();
            }
        }
        return apiToken.trim();
    }
}

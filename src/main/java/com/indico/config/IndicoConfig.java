package com.indico.config;

public class IndicoConfig {

    public final String host;
    public final String protocol;
    public final String tokenPath;
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

        public Builder maxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        /**
         * returns an instance of IndicoConfig to pass to Indico constructor
         *
         * @return instance to IndicoConfig
         */
        public IndicoConfig build() {
            IndicoConfig config = new IndicoConfig(this);
            return config;
        }

    }

    private IndicoConfig(Builder builder) {
        this.tokenPath = builder.tokenPath;
        this.host = builder.host;
        this.protocol = builder.protocol;
        this.maxConnections = builder.maxConnections;
    }
}

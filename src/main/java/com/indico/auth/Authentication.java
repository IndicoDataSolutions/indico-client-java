package com.indico.auth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Authentication {

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
    public static String resolveApiToken(String path) throws FileNotFoundException, IOException {
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

    /**
     *
     * Returns api token from indico_api_token.txt from project root, home
     * directory or INDICO_API_TOKEN_PATH
     *
     * @return api token from indico_api_token.txt
     * @throws FileNotFoundException
     * @throws IOException
     * @see Authentication#resolveApiToken(String)
     */
    public static String resolveApiToken() throws FileNotFoundException, IOException {
        return resolveApiToken(null);
    }
}

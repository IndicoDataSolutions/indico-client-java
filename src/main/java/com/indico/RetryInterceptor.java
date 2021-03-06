package com.indico;

import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;



public class RetryInterceptor  implements Interceptor {
    private final IndicoConfig indicoConfig;
    private final Logger logger = LogManager.getLogger(RetryInterceptor.class);

    public RetryInterceptor(IndicoConfig indicoConfig) {
        this.indicoConfig = indicoConfig;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        boolean success = false;
        int tryCount = 0;
        while (!success && tryCount < indicoConfig.maxRetries) {
            tryCount++;
            try {
                response = chain.proceed(request);
                success = response.isSuccessful();
            } catch(IOException | RuntimeException ex){
                success = false;
                if(response != null){
                    response.close();
                }
                logger.trace("Failed to complete the request for " + request.url() + "retrying: " + ex.getMessage());
            }
            if(!success){
                logger.trace("attempt " + tryCount + " failed for " + request.url() );
                if(response != null && (tryCount + 1) < indicoConfig.maxRetries)
                {
                    response.close();
                    logger.debug("Failed due to status code: " + response.code());

                }
            }
        }
        if(response == null){
            throw new RuntimeException("Unable to complete the request, enable trace logging for more information");
        }
        return response;
    }

}

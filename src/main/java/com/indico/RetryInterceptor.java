package com.indico;

import okhttp3.*;

import java.io.IOException;


public class RetryInterceptor  implements Interceptor {
    private final IndicoConfig indicoConfig;

    public RetryInterceptor(IndicoConfig indicoConfig) {
        this.indicoConfig = indicoConfig;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Response response = null;
        boolean success = false;
        int tryCount = 0;
        while ((response == null || !success) && tryCount < indicoConfig.maxRetries) {
            tryCount++;
            try {
                response = chain.proceed(request);
                success = response.isSuccessful();

            } catch(IOException ex){
                success = false;
            }
            if(!success){
                System.out.println(request.url());
                System.out.println("Failed, retrying... " + tryCount);

            }
        }
        if(tryCount >= indicoConfig.maxRetries){
            System.out.println(request.url());
            System.out.println("Gave up...");
        }

        return response;
    }
}

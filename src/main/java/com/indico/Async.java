package com.indico;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.util.concurrent.CompletableFuture;

import com.apollographql.apollo.exception.ApolloHttpException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Async {
    private static final Logger logger = LogManager.getLogger(Async.class);

    /**
     * Executes a an apollo graphQL call.
     * Specify the maximum amount of retries.
     * @param apolloCall
     * @param <T>
     * @return
     */
    public static <T> CompletableFuture<Response<T>> executeSync(ApolloCall<T> apolloCall) {
        CompletableFuture<Response<T>> completableFuture = new CompletableFuture<>();
        completableFuture.whenComplete((tResponse, throwable) -> {
            if (completableFuture.isCancelled()) {

                completableFuture.cancel(true);
            }
        });
            apolloCall.enqueue(new ApolloCall.Callback<T>() {


                public void onResponse(Response<T> response) {
                    completableFuture.complete(response);


                }

                public void onFailure(ApolloException e) {
                    logger.error("Failed to execute apollo call...");
                    completableFuture.completeExceptionally(e);
                }

                public void onHttpError(ApolloHttpException e) {
                    okhttp3.Response response = e.rawResponse();
                    if (response != null) {
                        response.close();
                    }
                    completableFuture.completeExceptionally(e);
                }

            });
        return completableFuture;


    }

}

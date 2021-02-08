package com.indico;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import java.util.concurrent.CompletableFuture;

import com.apollographql.apollo.exception.ApolloHttpException;
import com.apollographql.apollo.exception.ApolloNetworkException;
import org.jetbrains.annotations.NotNull;

public class Async {

    /**
     * Synchronizes apollographql api calls with the help of dispatcher
     * Retries up to 3 times by default on Network or Http Exception.
     * @param <T>
     * @param apolloCall instance of ApolloCall
     * @return synchronous Response from query specified by apolloCall
     */
    public static <T> CompletableFuture<Response<T>> executeSync(ApolloCall<T> apolloCall, IndicoConfig config) {
       return executeSync(apolloCall, config.maxRetries);
    }

    /**
     * Executes with retry logic up to the specified number of retries.
     * Specify the maximum amount of retries.
     * @param apolloCall
     * @param retries
     * @param <T>
     * @return
     */
    public static <T> CompletableFuture<Response<T>> executeSync(ApolloCall<T> apolloCall, int retries) {
        CompletableFuture<Response<T>> completableFuture = new CompletableFuture<>();
        return executeSync(apolloCall, retries, 0);
    }

    public static <T> CompletableFuture<Response<T>> executeSync(ApolloCall<T> apolloCall, int retries, int retry) {
        CompletableFuture<Response<T>> completableFuture = new CompletableFuture<>();

        completableFuture.whenComplete((tResponse, throwable) -> {
            if (completableFuture.isCancelled()) {
                completableFuture.cancel(true);
            }
        });

            try{
            apolloCall.enqueue(new ApolloCall.Callback<T>() {
                @Override
                public void onResponse(@NotNull Response<T> response) {
                    completableFuture.complete(response);
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {
                    throw e;
                }
            });}
            catch(ApolloNetworkException|ApolloHttpException e){
                if(retry < retries){
                    retry++;
                    return executeSync(apolloCall, retries, retry);
                }
                else{
                    completableFuture.completeExceptionally(e);
                }
            }

        return completableFuture;
    }



}

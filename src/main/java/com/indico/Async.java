package com.indico;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;


import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import com.apollographql.apollo.exception.ApolloHttpException;
import com.apollographql.apollo.exception.ApolloNetworkException;
import com.apollographql.apollo.exception.ApolloParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class Async {
    static AtomicInteger counter = new AtomicInteger();
    private static final Logger logger = LogManager.getLogger(Async.class);

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
        counter.set(0);
        while(counter.get() < retries){
            CompletableFuture<Response<T>> completableFuture = new CompletableFuture<>();
            completableFuture.whenComplete((tResponse, throwable) -> {
            if (completableFuture.isCancelled()) {

                completableFuture.cancel(true);
            }
        });
            try{
                apolloCall.enqueue(new ApolloCall.Callback<T>() {


                    public void onResponse(Response<T> response) {
                        completableFuture.complete(response);

                    }

                    public void onFailure(ApolloException e)
                    {
                        completableFuture.completeExceptionally(e);
                    }


                });}
            catch(ApolloNetworkException | ApolloParseException e){
                //parse exception can happen when there is a protocol_error
                //thrown by OKHttpClient
                if(counter.get() < retries){
                    logger.debug("Retrying call due to " + e.getMessage());
                    completableFuture.cancel(true);
                    counter.incrementAndGet();

                }
                else{
                    throw new RuntimeException("Max number of retries met, giving up", e);
                }
            }catch(ApolloHttpException e){
                // could store this in an array but let's try this for now.
                logger.trace("Retrying call" + apolloCall.getClass() + "due to " + e.code());
                if(counter.get() < retries && e.code() == 504 || e.code() == 503 || e.code() == 502){

                    completableFuture.cancel(true);
                    counter.incrementAndGet();
                }
                else{
                    logger.trace("Failed call" + apolloCall.getClass() + "due to " + e.code());
                    throw new RuntimeException("Max number of retries met, giving up", e);
                }
            }
            catch(Exception ex){
                logger.debug("Retrying call" + apolloCall.getClass() + "due to " + ex.getMessage());
                completableFuture.completeExceptionally(ex);
            }
         return completableFuture;
        }
        return new CompletableFuture<>();
    }



}

package com.indico;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

public class Async {

    /**
     * Synchronizes apollographql api calls with the help of dispatcher
     *
     * @param <T>
     * @param apolloCall instance of ApolloCall
     * @return synchronous Response from query specified by apolloCall
     */
    public static <T> CompletableFuture<Response<T>> executeSync(ApolloCall<T> apolloCall) {
        CompletableFuture<Response<T>> completableFuture = new CompletableFuture<>();

        completableFuture.whenComplete((tResponse, throwable) -> {
            if (completableFuture.isCancelled()) {
                completableFuture.cancel(true);
            }
        });

        apolloCall.enqueue(new ApolloCall.Callback<T>() {
            @Override
            public void onResponse(@NotNull Response<T> response) {
                completableFuture.complete(response);
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                completableFuture.completeExceptionally(e);
            }
        });

        return completableFuture;
    }
}

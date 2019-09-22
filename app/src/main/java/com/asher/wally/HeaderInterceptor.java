package com.asher.wally;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class HeaderInterceptor implements Interceptor {
    private String clientId;
    public HeaderInterceptor(String clientId) {
        this.clientId = clientId;
    }
    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder()
                .addHeader("Authorization", "Client-ID " + clientId)
                .build();
        return chain.proceed(request);
    }
}

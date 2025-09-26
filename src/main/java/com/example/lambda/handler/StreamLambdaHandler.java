package com.example.lambda.handler;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.example.lambda.Application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamLambdaHandler implements RequestStreamHandler {

    // No explicit AwsHttpApiV2Proxy* types here
    private static final SpringBootLambdaContainerHandler<?, ?> handler;

    static {
        try {
            // IMPORTANT: use HTTP API v2 adapter for Function URLs
            handler = SpringBootLambdaContainerHandler.getHttpApiV2ProxyHandler(Application.class);
        } catch (ContainerInitializationException e) {
            throw new RuntimeException("Could not initialize Spring Boot application", e);
        }
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        handler.proxyStream(input, output, context);
    }
}
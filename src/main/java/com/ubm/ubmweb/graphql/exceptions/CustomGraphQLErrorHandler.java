package com.ubm.ubmweb.graphql.exceptions;

import graphql.GraphQLError;
import graphql.kickstart.execution.error.GraphQLErrorHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomGraphQLErrorHandler implements GraphQLErrorHandler {

    @Override
    public List<GraphQLError> processErrors(List<GraphQLError> errors) {
        return errors.stream()
                .map(this::getCustomError)
                .collect(Collectors.toList());
    }

    private GraphQLError getCustomError(GraphQLError error) {
        // Here you can check the type of the error and return a custom error if needed
        // For simplicity, let's just return the original error
        return error;
    }
}

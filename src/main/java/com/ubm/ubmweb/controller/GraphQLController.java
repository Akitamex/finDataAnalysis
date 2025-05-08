package com.ubm.ubmweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import java.util.Map;

import jakarta.servlet.*;

import java.io.IOException;

import com.ubm.ubmweb.security.jwt.JwtTokenProvider;
import com.ubm.ubmweb.security.jwt.JwtAuthenticationException;

@RestController
public class GraphQLController {

    @Autowired
    private GraphQL graphQL;
	
    private final JwtTokenProvider jwtTokenProvider;
	
	
    public GraphQLController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
	
	
	// Разкомменть если токены юзаются
	
    @PostMapping("/query")
    public Map<String, Object> graphql(ServletRequest servletRequest, @RequestBody Map<String, Object> request) throws JwtAuthenticationException, IOException, ServletException {
		String token = jwtTokenProvider.resolveToken((jakarta.servlet.http.HttpServletRequest) servletRequest);
        if (token != null) {
			token = jwtTokenProvider.decryptToken(token);
			if (jwtTokenProvider.validateToken(token)) {
				String query = (String) request.get("query");
				String operationName = (String) request.get("operationName");
				Map<String, Object> variables = (Map<String, Object>) request.get("variables");
				Long userId = jwtTokenProvider.getUserId(token);
				variables.put("userId", userId);
				
				ExecutionInput executionInput = ExecutionInput.newExecutionInput()
						.query(query)
						.operationName(operationName)
						.variables(variables)
						.build();

				ExecutionResult executionResult = graphQL.execute(executionInput);

				return executionResult.toSpecification();	
			}
		}
		return null;
    }
	
	
	// @PostMapping("/query")
    // public Map<String, Object> graphql(@RequestBody Map<String, Object> request) {
	// 	String query = (String) request.get("query");
	// 	String operationName = (String) request.get("operationName");
	// 	Map<String, Object> variables = (Map<String, Object>) request.get("variables");
	// 	Long userId = 1L; //userid
	// 	variables.put("userId", userId);
		
	// 	ExecutionInput executionInput = ExecutionInput.newExecutionInput()
	// 			.query(query)
	// 			.operationName(operationName)
	// 			.variables(variables)
	// 			.build();

	// 	ExecutionResult executionResult = graphQL.execute(executionInput);

	// 	return executionResult.toSpecification();	
    // }
}

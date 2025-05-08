package com.ubm.ubmweb.config;

import java.math.BigDecimal;

// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import graphql.kickstart.tools.SchemaParser;
// import graphql.language.StringValue;
import graphql.GraphQL;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.language.StringValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ubm.ubmweb.graphql.resolvers.*;

@Configuration
public class GraphQlConfig {

    @Autowired private ArticleGroupMutationResolver articleGroupMutationResolver;
    @Autowired private ArticleGroupQueryResolver articleGroupQueryResolver;
    @Autowired private ArticleMutationResolver articleMutationResolver;
    @Autowired private ArticleQueryResolver articleQueryResolver;
    @Autowired private BankAccountMutationResolver bankAccountMutationResolver;
    @Autowired private BankAccountQueryResolver bankAccountQueryResolver;
    @Autowired private CompanyMutationResolver companyMutationResolver;
    @Autowired private CompanyQueryResolver companyQueryResolver;
    @Autowired private CounterpartyGroupMutationResolver counterpartyGroupMutationResolver;
    @Autowired private CounterpartyGroupQueryResolver counterpartyGroupQueryResolver;
    @Autowired private CounterpartyMutationResolver counterpartyMutationResolver;
    @Autowired private CounterpartyQueryResolver counterpartyQueryResolver;
    @Autowired private LegalEntityMutationResolver legalEntityMutationResolver;
    @Autowired private LegalEntityQueryResolver legalEntityQueryResolver;
    @Autowired private OperationMutationResolver operationMutationResolver;
    @Autowired private OperationQueryResolver operationQueryResolver;
    @Autowired private ProjectDirectionMutationResolver projectDirectionMutationResolver;
    @Autowired private ProjectDirectionQueryResolver projectDirectionQueryResolver;
    @Autowired private ProjectMutationResolver projectMutationResolver;
    @Autowired private ProjectQueryResolver projectQueryResolver;
    @Autowired private CashFlowQueryResolver cashFlowQueryResolver;
    @Autowired private ItemQueryResolver itemQueryResolver; 
    @Autowired private ItemMutationResolver itemMutationResolver; 
    @Autowired private ItemHistoryQueryResolver itemHistoryQueryResolver; 
    @Autowired private ItemHistoryMutationResolver itemHistoryMutationResolver; 
    @Autowired private ObligationQueryResolver obligationQueryResolver;
    @Autowired private ObligationMutationResolver obligationMutationResolver;
    @Autowired private LoanQueryResolver loanQueryResolver;
    @Autowired private LoanMutationResolver loanMutationResolver;
    @Autowired private AssetQueryResolver assetQueryResolver;
    @Autowired private AssetMutationResolver assetMutationResolver;
    @Autowired private FixedAssetMutationResolver fixedAssetMutationResolver;
    @Autowired private FixedAssetQueryResolver fixedAssetQueryResolver;
    @Autowired private ProfitsLossesQueryResolver profitsLossesQueryResolver;
    @Autowired private CostAnalysisQueryResolver costAnalysisQueryResolver;
    @Autowired private DebtAnalysisQueryResolver debtAnalysisQueryResolver;
    // private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Register the JavaTimeModule to handle Java 8 Date/Time types
        mapper.registerModule(new JavaTimeModule());
        // To ensure LocalDate and other date types aren't serialized as timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.simpleDateFormat("yyyy-MM-dd");
            builder.modules(new JavaTimeModule());
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }
    
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        GraphQLScalarType longType = longScalar();
        GraphQLScalarType bigDecimalType = bigDecimalScalar();
        return wiringBuilder -> wiringBuilder
                .scalar(longType)
                .scalar(bigDecimalType);
    }

    @Bean
    public GraphQLSchema graphQLSchema() {
        GraphQLSchema schema = SchemaParser.newParser()
                .files("graphql/schema.graphqls") // Make sure this path correctly points to your schema files
                .scalars(longScalar(), bigDecimalScalar()) // Register the scalar types
                .resolvers(
                        articleGroupMutationResolver,
                        articleGroupQueryResolver,
                        articleMutationResolver,
                        articleQueryResolver,
                        bankAccountMutationResolver,
                        bankAccountQueryResolver,
                        companyMutationResolver,
                        companyQueryResolver,
                        counterpartyGroupMutationResolver,
                        counterpartyGroupQueryResolver,
                        counterpartyMutationResolver,
                        counterpartyQueryResolver,
                        legalEntityMutationResolver,
                        legalEntityQueryResolver,
                        operationMutationResolver,
                        operationQueryResolver,
                        projectDirectionMutationResolver,
                        projectDirectionQueryResolver,
                        projectMutationResolver,
                        projectQueryResolver,
                        cashFlowQueryResolver,
                        itemQueryResolver,
                        itemMutationResolver,
                        itemHistoryQueryResolver,
                        itemHistoryMutationResolver,
                        obligationQueryResolver,
                        obligationMutationResolver,
                        loanQueryResolver,
                        loanMutationResolver,
                        assetQueryResolver,
                        assetMutationResolver,
                        fixedAssetMutationResolver,
                        fixedAssetQueryResolver,
                        profitsLossesQueryResolver,
                        costAnalysisQueryResolver,
                        debtAnalysisQueryResolver
                )
                .build()
                .makeExecutableSchema();
                return schema;
    }

    @Bean
    public GraphQL graphQL(GraphQLSchema graphQLSchema) {
        return GraphQL.newGraphQL(graphQLSchema).build();
    }

    // @Bean
    // public GraphQLScalarType dateScalar() {
    //     return GraphQLScalarType.newScalar()
    //             .name("LocalDate")
    //             .description("Java 8 LocalDate as scalar.")
    //             .coercing(new Coercing<Object, Object>() {
    //                 @Override
    //                 public String serialize(final Object dataFetcherResult) {
    //                     if (dataFetcherResult instanceof LocalDate) {
    //                         return formatter.format((LocalDate) dataFetcherResult);
    //                     } else {
    //                         throw new CoercingSerializeException("Expected a LocalDate object.");
    //                     }
    //                 }

    //                 @Override
    //                 public LocalDate parseValue(final Object input) {
    //                     try {
    //                         if (input instanceof String) {
    //                             return LocalDate.parse((String) input, formatter);
    //                         } else {
    //                             throw new CoercingParseValueException("Expected a String");
    //                         }
    //                     } catch (DateTimeParseException e) {
    //                         throw new CoercingParseValueException(String.format("Not a valid date: '%s'.", input), e
    //                         );
    //                     }
    //                 }

    //                 @Override
    //                 public LocalDate parseLiteral(final Object input) {
    //                     if (input instanceof StringValue) {
    //                         try {
    //                             return LocalDate.parse(((StringValue) input).getValue(), formatter);
    //                         } catch (DateTimeParseException e) {
    //                             throw new CoercingParseLiteralException(e);
    //                         }
    //                     } else {
    //                         throw new CoercingParseLiteralException("Expected a StringValue.");
    //                     }
    //                 }
    //             }).build();
    // }

    // Define a custom Long scalar to avoid naming conflicts
    @Bean
    public GraphQLScalarType longScalar() {
        return GraphQLScalarType.newScalar()
                .name("GraphQLLong")
                .description("Custom scalar for Long values")
                .coercing(new Coercing<Long, Long>() {
                    @Override
                    public Long serialize(Object dataFetcherResult) {
                        if (dataFetcherResult instanceof Long long1) {
                            return long1;
                        }
                        throw new CoercingSerializeException("Expected a Long object.");
                    }

                    @Override
                    public Long parseValue(Object input) {
                        try {
                            if (input instanceof String string) {
                                return Long.parseLong(string);
                            }
                            if (input instanceof Integer integer) {
                                return integer.longValue();
                            }
                            if (input instanceof Long long1) {
                                return long1;
                            }
                            throw new CoercingParseValueException("Expected a Long object.");
                        } catch (NumberFormatException e) {
                            throw new CoercingParseValueException("Failed to parse value as Long.", e);
                        }
                    }

                    @Override
                    public Long parseLiteral(Object input) {
                        if (!(input instanceof String) && !(input instanceof Integer) && !(input instanceof Long)) {
                            throw new CoercingParseLiteralException("Expected a value of type Long.");
                        }
                        try {
                            return Long.parseLong(input.toString());
                        } catch (NumberFormatException e) {
                            throw new CoercingParseLiteralException("Failed to parse literal as Long.", e);
                        }
                    }
                }).build();
    }
    
    @Bean
    public GraphQLScalarType bigDecimalScalar() {
    return GraphQLScalarType.newScalar()
            .name("GraphQLBigDecimal")
            .description("Custom scalar for BigDecimal values")
            .coercing(new Coercing<BigDecimal, BigDecimal>() {
                @Override
                public BigDecimal serialize(Object dataFetcherResult) {
                    if (dataFetcherResult instanceof BigDecimal decimal) {
                        return decimal;
                    }
                    throw new CoercingSerializeException("Expected a BigDecimal object.");
                }

                @Override
                public BigDecimal parseValue(Object input) {
                    try {
                        if (input instanceof String string) {
                            return new BigDecimal(string);
                        }
                        if (input instanceof Double || input instanceof Integer) {
                            return BigDecimal.valueOf(((Number) input).doubleValue());
                        }
                        if (input instanceof BigDecimal decimal) {
                            return decimal;
                        }
                        throw new CoercingParseValueException("Expected a BigDecimal object.");
                    } catch (NumberFormatException e) {
                        throw new CoercingParseValueException("Failed to parse value as BigDecimal.", e);
                    }
                }

                @Override
                public BigDecimal parseLiteral(Object input) {
                    if (input instanceof StringValue value) {
                        try {
                            return new BigDecimal(value.getValue());
                        } catch (NumberFormatException e) {
                            throw new CoercingParseLiteralException("Failed to parse literal as BigDecimal.", e);
                        }
                    }
                    throw new CoercingParseLiteralException("Expected AST type 'StringValue'.");
                }
            }).build();
    }
}

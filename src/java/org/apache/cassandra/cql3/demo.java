package org.apache.cassandra.cql3;

import org.antlr.runtime.RecognitionException;
import org.apache.cassandra.cql3.statements.CFStatement;
import org.apache.cassandra.cql3.statements.ParsedStatement;
import org.apache.cassandra.exceptions.CassandraException;
import org.apache.cassandra.exceptions.InvalidRequestException;
import org.apache.cassandra.exceptions.RequestExecutionException;
import org.apache.cassandra.exceptions.RequestValidationException;
import org.apache.cassandra.exceptions.SyntaxException;
import org.apache.cassandra.metrics.CQLMetrics;
import org.apache.cassandra.service.ClientState;
import org.apache.cassandra.service.QueryState;
import org.apache.cassandra.tracing.Tracing;
import org.apache.cassandra.transport.messages.ResultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class demo {
    private static final Logger logger = LoggerFactory.getLogger(QueryProcessor.class);
    public static final CQLMetrics metrics = new CQLMetrics();
	
    public static ParsedStatement parseStatement(String queryStr) throws SyntaxException
    {
        try
        {
            return CQLFragmentParser.parseAnyUnhandled(CqlParser::query, queryStr);
        }
        catch (CassandraException ce)
        {
            throw ce;
        }
        catch (RuntimeException re)
        {
            logger.error(String.format("The statement: [%s] could not be parsed.", queryStr), re);
            throw new SyntaxException(String.format("Failed parsing statement: [%s] reason: %s %s",
                                                    queryStr,
                                                    re.getClass().getSimpleName(),
                                                    re.getMessage()));
        }
        catch (RecognitionException e)
        {
            throw new SyntaxException("Invalid or malformed CQL query string: " + e.getMessage());
        }
    }
	
    public static ParsedStatement.Prepared getStatement(String queryStr, ClientState clientState)
    throws RequestValidationException
    {
        Tracing.trace("Parsing {}", queryStr);
        ParsedStatement statement = parseStatement(queryStr);

        // Set keyspace for statement that require login
        if (statement instanceof CFStatement)
            ((CFStatement)statement).prepareKeyspace(clientState);

        Tracing.trace("Preparing statement");
        return statement.prepare(clientState);
    }
	
    
    
    public ResultMessage process(String queryString, QueryState queryState, QueryOptions options, long queryStartNanoTime)
    throws RequestExecutionException, RequestValidationException
    {
        ParsedStatement.Prepared p = getStatement(queryString, queryState.getClientState());
        options.prepare(p.boundNames);
        CQLStatement prepared = p.statement;
        if (prepared.getBoundTerms() != options.getValues().size())
            throw new InvalidRequestException("Invalid amount of bind variables");

        if (!queryState.getClientState().isInternal)
            metrics.regularStatementsExecuted.inc();

        return processStatement(prepared, queryState, options, queryStartNanoTime);
    }
	
    
    
    
    
    
    
    
    
    
    
    
    public ResultMessage processStatement(CQLStatement statement, QueryState queryState, QueryOptions options, long queryStartNanoTime)
    throws RequestExecutionException, RequestValidationException
    {
        logger.trace("Process {} @CL.{}", statement, options.getConsistency());
        ClientState clientState = queryState.getClientState();
        statement.checkAccess(clientState);
        statement.validate(clientState);

        ResultMessage result = statement.execute(queryState, options, queryStartNanoTime);
        return result == null ? new ResultMessage.Void() : result;
    }
    
    
}

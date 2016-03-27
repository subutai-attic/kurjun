package ai.subut.kurjun.web.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;


/**
 *
 */
public class ResponseFilter implements Filter
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AuthorizedFilter.class );

    @Override
    public Result filter( FilterChain filterChain, Context context )
    {
        Result result = filterChain.next( context );
        result.addHeader( "Access-Control-Allow-Origin", "*" );
        result.addHeader( "Access-Control-Allow-Headers", "origin, content-type, accept, authorization" );
        result.addHeader( "Access-Control-Allow-Credentials","true");
        result.addHeader( "Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS, HEAD" );

        return result;
    }
}

package ai.subut.kurjun.web.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import ai.subut.kurjun.common.service.KurjunProperties;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;


/**
 *
 */
public class ProtocolFilter implements Filter
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AuthorizedFilter.class );

    @Inject
    KurjunProperties props;

    @Override
    public Result filter( FilterChain filterChain, Context context )
    {

        if(context.getScheme().equals( "https" ))
        {
            if(props.get( "security.https.enabled" ).equals( "true" ))
                return filterChain.next( context );
            else
                return Results.forbidden().render( "Not allowed" ).text();
        }
        else if(context.getScheme().equals( "http" ))
        {
            if(props.get( "security.http.enabled" ).equals( "true" ))
                return filterChain.next( context );
            else
                return Results.forbidden().render( "Not allowed" ).text();
        }
        else
        {
            return Results.forbidden().render( "Not allowed" ).text();
        }
    }
}

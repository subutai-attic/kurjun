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
        String scheme = context.getScheme();
        boolean httpEnabled = props.get( "security.http.enabled" ).equals( "true" );
        boolean httpsEnabled = props.get( "security.https.enabled" ).equals( "true" );

        if( scheme.equals("https") && httpsEnabled || scheme.equals("http") && httpEnabled )
        {
            return filterChain.next( context );
        }
        else
        {
            LOGGER.warn( "Not passed "+this.getClass().getName() );
            return Results.forbidden().render( "Not allowed" ).text();
        }
    }
}

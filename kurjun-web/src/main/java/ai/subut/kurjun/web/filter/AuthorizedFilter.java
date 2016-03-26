package ai.subut.kurjun.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.subut.kurjun.model.identity.UserSession;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.utils.NoHttpBody;

/**
 *
 */
public class AuthorizedFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger( AuthorizedFilter.class );

    @Override
    public Result filter(FilterChain filterChain, Context context)
    {
        Result result = filterChain.next( context );
        try
        {
            UserSession us = ( UserSession ) context.getAttribute( SecurityFilter.USER_SESSION );
            if ( us != null && us.getUser() != null && !( result.getRenderable() instanceof NoHttpBody )  /* If not redirecting */

                    && Result.TEXT_HTML.equals( result.getContentType() ) /* handle only html content types */ )
            {
                result.render( "userInfo", us.getUser() );
            }
        }
        catch ( Exception e )
        {
            LOGGER.error( "Some error occurred: "+e.getMessage() );
        }

        return result;
    }
}

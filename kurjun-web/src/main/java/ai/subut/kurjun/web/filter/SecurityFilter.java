package ai.subut.kurjun.web.filter;


import com.google.common.base.Strings;
import com.google.inject.Inject;

import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;
import ninja.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SecurityFilter implements Filter
{

    private static final Logger LOGGER = LoggerFactory.getLogger( SecurityFilter.class );

    @Inject
    IdentityManagerService identityManagerService;

    public static final String USER_SESSION = "USER_SESSION";
    public static final String USER_TOKEN = "sptoken";

    @Override
    public Result filter( final FilterChain filterChain, final Context ctx )
    {
        try
        {
            Session session = ctx.getSession();
            UserSession uSession = null;
            String sptoken = ctx.getParameter( USER_TOKEN );

            if( Strings.isNullOrEmpty(sptoken))
            {
                if ( session != null && session.get( USER_SESSION ) != null )
                {
                    uSession = identityManagerService.loginUser("token", session.get( USER_SESSION ));
                }
            }
            else
            {
                uSession = identityManagerService.loginUser("token", sptoken);
            }

            if ( uSession == null )
            {
                uSession = identityManagerService.loginPublicUser();
            }

            //******************************
            if ( uSession != null )
            {
                //--------------------------------------
                if(!uSession.getUser().getKeyFingerprint().equals( identityManagerService.getPublicUserId())) // if not public user
                {
                    session.put( USER_SESSION, uSession.getUserToken().getFullToken() );
                }
                //--------------------------------------
                ctx.setAttribute( "USER_SESSION", uSession );
                //--------------------------------------
            }
            else
            {
                session.remove(  USER_SESSION );
            }
            //******************************
        }
        catch(Exception ex)
        {
            LOGGER.error( "Not passed SecurityFilter: {}", ex.getMessage() );
            return Results.forbidden().render( "Not allowed" ).text();
        }

        return filterChain.next( ctx );
    }
}
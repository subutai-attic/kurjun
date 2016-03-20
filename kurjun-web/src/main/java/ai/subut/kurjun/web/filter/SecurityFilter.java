package ai.subut.kurjun.web.filter;


import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.inject.Inject;

import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;
import ninja.session.Session;
import ninja.utils.NoHttpBody;


public class SecurityFilter implements Filter
{
    @Inject
    IdentityManagerService identityManagerService;

    public static final String USER_SESSION = "USER_SESSION";
    public static final String USER_TOKEN = "sptoken";

    @Override
    public Result filter( final FilterChain filterChain, final Context ctx )
    {
        Session session = ctx.getSession();
        Result result = filterChain.next( ctx );
        /*
        if ( session == null || session.get( USER_SESSION ) == null )
        {
            return Results.redirect( "/login" );
        }
        */
        try
        {
            UserSession uSession = null;
            String sptoken = ctx.getParameter( USER_TOKEN );

            if( Strings.isNullOrEmpty(sptoken))
            {
                uSession = identityManagerService.loginPublicUser();
            }
            else
            {
                uSession = identityManagerService.loginUser ("token", sptoken);
            }

            //******************************
            if( uSession != null )
            {
                session.put( USER_SESSION, new Gson().toJson( uSession ) );
                if ( !(result.getRenderable() instanceof NoHttpBody)  /* If not redirecting */
                        && Result.TEXT_HTML.equals( result.getContentType()) /* handle only html content types */  )
                {
                    result.render( "userInfo", uSession );
                }
                else
                {
                    return result;
                }
            }
            //******************************
        }
        catch(Exception ex)
        {
            return Results.forbidden().render( "Not allowed" ).text();
        }

        return filterChain.next( ctx );
    }
}
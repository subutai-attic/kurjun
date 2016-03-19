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


public class SecurityFilter implements Filter
{
    @Inject
    IdentityManagerService identityManagerService;

    public static final String USER_SESSION = "USER_SESSION";

    @Override
    public Result filter( final FilterChain filterChain, final Context ctx )
    {
        Session session = ctx.getSession();

        /*
        if ( session == null || session.get( USER_SESSION ) == null )
        {
            return Results.redirect( "/login" );
        }
        */
        try
        {
            UserSession uSession = null;
            String sptoken = ctx.getParameter( "sptoken" );

            if( Strings.isNullOrEmpty(sptoken))
            {
                uSession = identityManagerService.loginPublicUser();
            }
            else
            {
                uSession = identityManagerService.loginUser ("token", sptoken);
            }

            //******************************
            if(uSession != null)
            {
                session.put( USER_SESSION, new Gson().toJson( uSession ) );
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
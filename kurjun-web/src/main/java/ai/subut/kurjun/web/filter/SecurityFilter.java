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
        try
        {
            Session session = ctx.getSession();
            Result result = filterChain.next( ctx );
            UserSession uSession = null;
            String sptoken = ctx.getParameter( USER_TOKEN );

            if( Strings.isNullOrEmpty(sptoken))
            {
                if ( session != null && session.get( USER_SESSION ) != null )
                {
                    uSession = identityManagerService.loginUser ("token", session.get( USER_SESSION ));
                }
                else
                {
                    uSession = identityManagerService.loginPublicUser();
                }
            }
            else
            {
                uSession = identityManagerService.loginUser ("token", sptoken);
            }

            //******************************
            if( uSession != null )
            {
                //--------------------------------------
                if(!uSession.equals( "public-user" ))
                {
                    session.put( USER_SESSION, uSession.getUserToken().getFullToken() );
                }
                //--------------------------------------
                ctx.setAttribute( "USER_SESSION", uSession );
                //--------------------------------------


                if ( !(result.getRenderable() instanceof NoHttpBody)  // If not redirecting
                        && Result.TEXT_HTML.equals( result.getContentType())) //  handle only html content types
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
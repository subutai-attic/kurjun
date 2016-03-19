package ai.subut.kurjun.web.filter;


import com.google.common.base.Strings;
import com.google.inject.Inject;

import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ai.subut.kurjun.web.service.RepositoryService;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;


public class SecurityFilter implements Filter
{
    @Inject
    IdentityManagerService identityManagerService;
    @Inject
    RepositoryService repositoryService;

    public static final String USER_SESSION = "USER_SESSION";


    @Override
    public Result filter( final FilterChain filterChain, final Context ctx )
    {
        repositoryService.getRepositories();
        try
        {
            UserSession uSession = null;
            String sptoken = ctx.getParameter( "sptoken" );

            if ( Strings.isNullOrEmpty( sptoken ) )
            {
                uSession = identityManagerService.loginPublicUser();
            }
            else
            {
                uSession = identityManagerService.loginUser( "token", sptoken );
            }

            //******************************
            if ( uSession != null )
            {
                ctx.setAttribute( USER_SESSION, uSession );
            }
            //******************************
        }
        catch ( Exception ex )
        {
            return Results.forbidden().render( "Not allowed" ).text();
        }

        return filterChain.next( ctx );
    }
}
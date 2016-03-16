package ai.subut.kurjun.web.filter;


import com.google.inject.Inject;

import ai.subut.kurjun.identity.service.IdentityManager;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;


public class SecurityFilter implements Filter
{
    @Inject
    IdentityManager identityManager;

    @Override
    public Result filter( final FilterChain filterChain, final Context context )
    {
        if ( 2 == 3 )
        {
            return Results.forbidden().render( "Not allowed" ).text();
        }
        context.setAttribute( "sptoke", "hello" );

        return filterChain.next( context );
    }
}
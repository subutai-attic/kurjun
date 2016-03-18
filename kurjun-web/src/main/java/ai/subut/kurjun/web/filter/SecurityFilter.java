package ai.subut.kurjun.web.filter;


import com.google.common.base.Strings;
import com.google.inject.Inject;

import ai.subut.kurjun.web.service.IdentityManagerService;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;


public class SecurityFilter implements Filter
{
    @Inject
    IdentityManagerService identityManager;

    @Override
    public Result filter( final FilterChain filterChain, final Context context )
    {

        try
        {
            String sptoken = context.getParameter( "sptoken" );

            if( Strings.isNullOrEmpty(sptoken))
            {

            }
            else
            {
                identityManager.
            }
        }
        catch(Exception ex)
        {
            return Results.forbidden().render( "Not allowed" ).text();
        }


        if ( 2 == 3 )
        {
        }
        context.setAttribute( "sptoke", "hello" );

        return filterChain.next( context );
    }
}
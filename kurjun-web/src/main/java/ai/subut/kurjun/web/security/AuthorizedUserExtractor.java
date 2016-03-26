package ai.subut.kurjun.web.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.web.filter.SecurityFilter;
import ninja.Context;
import ninja.params.ArgumentExtractor;


public class AuthorizedUserExtractor implements ArgumentExtractor<UserSession>
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AuthorizedUserExtractor.class );

    @Override
    public UserSession extract( Context context )
    {
        try
        {
            UserSession uSession = ( UserSession ) context.getAttribute( SecurityFilter.USER_SESSION );
            return uSession;
        }
        catch ( Exception e )
        {
            LOGGER.error( "Some error occurred: "+e.getMessage() );
            return null;
        }
    }


    @Override
    public Class<UserSession> getExtractedType()
    {
        return UserSession.class;
    }


    @Override
    public String getFieldName()
    {
        return null;
    }
}

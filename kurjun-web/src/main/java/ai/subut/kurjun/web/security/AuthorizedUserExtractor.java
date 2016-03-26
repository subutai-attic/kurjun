package ai.subut.kurjun.web.security;

import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.web.filter.SecurityFilter;
import ninja.Context;
import ninja.params.ArgumentExtractor;


public class AuthorizedUserExtractor implements ArgumentExtractor<UserSession>
{
    @Override
    public UserSession extract( Context context )
    {
        UserSession uSession = (UserSession ) context.getAttribute(SecurityFilter.USER_SESSION );
        return uSession;
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

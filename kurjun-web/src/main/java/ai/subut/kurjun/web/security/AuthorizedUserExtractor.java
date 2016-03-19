package ai.subut.kurjun.web.security;

import ai.subut.kurjun.identity.DefaultUserSession;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.web.filter.SecurityFilter;
import com.google.gson.Gson;
import ninja.Context;
import ninja.params.ArgumentExtractor;


public class AuthorizedUserExtractor implements ArgumentExtractor<UserSession>
{
    @Override
    public UserSession extract( Context context )
    {
        String uSessionStr = context.getSession().get(SecurityFilter.USER_SESSION);
        UserSession userSession = new Gson().fromJson( uSessionStr, DefaultUserSession.class );
        return userSession;
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

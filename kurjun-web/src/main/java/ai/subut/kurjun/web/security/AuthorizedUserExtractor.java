package ai.subut.kurjun.web.security;

import ai.subut.kurjun.web.filter.SecurityFilter;
import ninja.Context;
import ninja.params.ArgumentExtractor;


public class AuthorizedUserExtractor implements ArgumentExtractor<String>
{
    @Override
    public String extract( Context context )
    {
        String uSessionStr = context.getSession().get(SecurityFilter.USER_SESSION);
        return uSessionStr;
    }


    @Override
    public Class<String> getExtractedType()
    {
        return String.class;
    }


    @Override
    public String getFieldName()
    {
        return null;
    }
}

package ai.subut.kurjun.web.model;


import ai.subut.kurjun.common.service.KurjunContext;


public abstract class UserContext extends KurjunContext
{
    public UserContext( final String name )
    {
        super( name );
    }


    public abstract String getFingerprint();

    public abstract String getRepositoryName();
}

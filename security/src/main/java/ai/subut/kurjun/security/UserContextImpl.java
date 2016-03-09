package ai.subut.kurjun.security;


import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.user.UserContext;


public class UserContextImpl extends KurjunContext implements UserContext
{
    private String fingerprint;
    private String repositoryName;

    public UserContextImpl( final String name )
    {
        super( name );
        this.fingerprint = name;
        this.repositoryName = name;
    }


    public UserContextImpl( final String name, final String fingerprint, final String repositoryName )
    {
        super( name );
        this.fingerprint = fingerprint;
        this.repositoryName = repositoryName;
    }


    @Override
    public String getFingerprint()
    {
        return fingerprint;
    }


    @Override
    public String getRepositoryName()
    {
        return repositoryName;
    }
}

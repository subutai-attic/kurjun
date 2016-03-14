package ai.subut.kurjun.web.model;


public class UserContextImpl extends UserContext
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


    @Override
    public String getName()
    {
        return fingerprint;
    }
}

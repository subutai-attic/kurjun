package ai.subut.kurjun.web.model;


public class RemoteRepo
{
    private String url;
    private String token;
    private long tokenTtl;


    public RemoteRepo( final String url )
    {
        this.url = url;
    }


    public RemoteRepo( final String url, final String token, final long tokenTtl )
    {
        this.url = url;
        this.token = token;
        this.tokenTtl = tokenTtl;
    }


    public String getUrl()
    {
        return url;
    }


    public void setUrl( final String url )
    {
        this.url = url;
    }


    public String getToken()
    {
        return token;
    }


    public void setToken( final String token )
    {
        this.token = token;
    }


    public long getTokenTtl()
    {
        return tokenTtl;
    }


    public void setTokenTtl( final long tokenTtl )
    {
        this.tokenTtl = tokenTtl;
    }

}

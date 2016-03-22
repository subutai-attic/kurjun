package ai.subut.kurjun.web.model;


import java.util.List;

import ai.subut.kurjun.model.metadata.SerializableMetadata;


public class RepositoryCache
{
    private String md5;

    private List<SerializableMetadata> metadataList;


    public RepositoryCache( final String md5, final List<SerializableMetadata> metadataList )
    {
        this.md5 = md5;
        this.metadataList = metadataList;
    }


    public String getMd5()
    {
        return md5;
    }


    public void setMd5( final String md5 )
    {
        this.md5 = md5;
    }


    public List<SerializableMetadata> getMetadataList()
    {
        return metadataList;
    }


    public void setMetadataList( final List<SerializableMetadata> metadataList )
    {
        this.metadataList = metadataList;
    }


    @Override
    public String toString()
    {
        return "RepositoryCache{" +
                "md5='" + md5 + '\'' +
                ", metadataList=" + metadataList +
                '}';
    }


    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof RepositoryCache ) )
        {
            return false;
        }

        final RepositoryCache that = ( RepositoryCache ) o;

        if ( md5 != null ? !md5.equals( that.md5 ) : that.md5 != null )
        {
            return false;
        }
        return !( metadataList != null ? !metadataList.equals( that.metadataList ) : that.metadataList != null );
    }


    @Override
    public int hashCode()
    {
        int result = md5 != null ? md5.hashCode() : 0;
        result = 31 * result + ( metadataList != null ? metadataList.hashCode() : 0 );
        return result;
    }
}

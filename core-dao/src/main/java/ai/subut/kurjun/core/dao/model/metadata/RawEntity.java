package ai.subut.kurjun.core.dao.model.metadata;


import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


public class RawEntity implements SerializableMetadata, Metadata
{


    private String md5Sum;

    private String name;
    private long size;
    private String fingerprint;


    public RawEntity()
    {
    }


    public RawEntity( final String md5Sum, final String name, final long size, final String fingerprint )
    {
        this.md5Sum = md5Sum;
        this.name = name;
        this.size = size;
        this.fingerprint = fingerprint;
    }


    public long getSize()
    {
        return size;
    }


    public void setSize( final long size )
    {
        this.size = size;
    }


    @Override
    public Object getId()
    {
        if ( md5Sum == null || fingerprint == null )
        {
            return null;
        }

        return fingerprint + "." + md5Sum;
    }


    @Override
    public String getMd5Sum()
    {

        return md5Sum;
    }


    public void setMd5Sum( String md5Sum )
    {
        this.md5Sum = md5Sum;
    }


    @Override
    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    @Override
    public String getVersion()
    {
        return "Not Supported";
    }


    @Override
    public String serialize()
    {
        return null;
    }


    public String getFingerprint()
    {
        return fingerprint;
    }


    public void setFingerprint( final String fingerprint )
    {
        this.fingerprint = fingerprint;
    }
}

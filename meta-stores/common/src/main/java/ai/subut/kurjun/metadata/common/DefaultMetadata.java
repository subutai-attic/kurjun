package ai.subut.kurjun.metadata.common;


import javax.persistence.Embeddable;

import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


/**
 * Default POJO implementation of {@link Metadata}.
 */
@Embeddable
public class DefaultMetadata implements SerializableMetadata
{

    private String md5sum;
    private String name;
    private String version;
    private String serialized;
    private String fingerprint;

    //private String objectOwner;


    public String getFingerprint()
    {
        return fingerprint;
    }


    public void setFingerprint( final String fingerprint )
    {
        this.fingerprint = fingerprint;
    }


    @Override
    public String getOwner()
    {
        return fingerprint;
    }


    @Override
    public Object getId()
    {

        if ( fingerprint != null && md5sum != null )
        {
            return fingerprint + "." + md5sum;
        }
        return null;
    }


    @Override
    public String getMd5Sum()
    {
        return md5sum;
    }


    public void setMd5sum( String md5sum )
    {
        this.md5sum = md5sum;
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
        return version;
    }


    public void setVersion( String version )
    {
        this.version = version;
    }


    @Override
    public String serialize()
    {
        return serialized;
    }


    public void setSerialized( String serialized )
    {
        this.serialized = serialized;
    }


    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 17 * hash + this.md5sum.hashCode();
        return hash;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof Metadata )
        {
            Metadata other = ( Metadata ) obj;
            return this.md5sum.equalsIgnoreCase( other.getMd5Sum() );
        }
        return false;
    }
}


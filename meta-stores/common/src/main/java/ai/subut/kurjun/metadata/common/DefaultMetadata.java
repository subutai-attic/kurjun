package ai.subut.kurjun.metadata.common;


import java.util.Arrays;

import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


/**
 * Default POJO implementation of {@link Metadata}.
 *
 */
public class DefaultMetadata implements SerializableMetadata
{

    private byte[] md5sum;
    private String name;
    private String version;
    private String serialized;


    @Override
    public byte[] getMd5Sum()
    {
        return Arrays.copyOf( md5sum, md5sum.length );
    }


    public void setMd5sum( byte[] md5sum )
    {
        this.md5sum = Arrays.copyOf( md5sum, md5sum.length );
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
        hash = 17 * hash + Arrays.hashCode( this.md5sum );
        return hash;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof Metadata )
        {
            Metadata other = ( Metadata ) obj;
            return Arrays.equals( this.md5sum, other.getMd5Sum() );
        }
        return false;
    }


}


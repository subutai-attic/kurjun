package ai.subut.kurjun.metadata.common.raw;


import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;

import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


/**
 * Metadata for raw resources.
 *
 */
public class RawMetadata implements Metadata, SerializableMetadata
{
    private byte[] md5Sum;
    private String name;
    private long size;


    public RawMetadata( final byte[] md5Sum, final String name, final long size )
    {
        this.md5Sum = md5Sum;
        this.name = name;
        this.size = size;
    }


    public RawMetadata( byte[] md5Sum, String name )
    {
        this.md5Sum = md5Sum;
        this.name = name;
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
        return md5Sum != null ? Hex.encodeHexString( md5Sum ) : null;
    }


    @Override
    public byte[] getMd5Sum()
    {
        return md5Sum != null ? Arrays.copyOf( md5Sum, md5Sum.length ) : null;
    }


    public void setMd5Sum( byte[] md5Sum )
    {
        this.md5Sum = md5Sum != null ? Arrays.copyOf( md5Sum, md5Sum.length ) : null;
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
        throw new UnsupportedOperationException( "Version is not supported for raw resource" );
    }


    @Override
    public String serialize()
    {
        return MetadataUtils.JSON.toJson( this );
    }


    @Override
    public int hashCode()
    {
        int hash = 13;
        hash = 17 * hash + Arrays.hashCode( this.md5Sum );
        return hash;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof RawMetadata )
        {
            RawMetadata other = ( RawMetadata ) obj;
            return Arrays.equals( this.md5Sum, other.md5Sum );
        }
        return false;
    }

}

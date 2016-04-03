package ai.subut.kurjun.metadata.common.raw;


import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


/**
 * Metadata for raw resources.
 */
public class RawMetadata implements Metadata, SerializableMetadata
{
    private String md5Sum;
    private String name;
    private long size;
    private String fingerprint;


    public RawMetadata()
    {
    }


    public RawMetadata( final String md5Sum, final String name, final long size, final String fingerprint )
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
        return MetadataUtils.JSON.toJson( this );
    }


    public String getFingerprint()
    {
        return fingerprint;
    }


    public void setFingerprint( final String fingerprint )
    {
        this.fingerprint = fingerprint;
    }


    @Override
    public boolean equals( final Object o )
    {

        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof RawMetadata ) )
        {
            return false;
        }

        final RawMetadata that = ( RawMetadata ) o;

        if ( size != that.size )
        {
            return false;
        }
        if ( md5Sum != null ? !md5Sum.equals( that.md5Sum ) : that.md5Sum != null )
        {
            return false;
        }
        if ( name != null ? !name.equals( that.name ) : that.name != null )
        {
            return false;
        }
        return !( fingerprint != null ? !fingerprint.equals( that.fingerprint ) : that.fingerprint != null );
    }


    @Override
    public int hashCode()
    {
        int result = md5Sum != null ? md5Sum.hashCode() : 0;
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        result = 31 * result + ( int ) ( size ^ ( size >>> 32 ) );
        result = 31 * result + ( fingerprint != null ? fingerprint.hashCode() : 0 );
        return result;
    }


    @Override
    public String toString()
    {
        return "RawMetadata{" +
                "md5Sum='" + md5Sum + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", fingerprint='" + fingerprint + '\'' +
                '}';
    }
}

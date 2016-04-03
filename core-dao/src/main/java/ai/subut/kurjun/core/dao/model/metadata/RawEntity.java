package ai.subut.kurjun.core.dao.model.metadata;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;

import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


@Entity
@Table( name = RawEntity.TABLE_NAME )
@Access( AccessType.FIELD )
public class RawEntity implements SerializableMetadata, Metadata
{

    public static final String TABLE_NAME = "raw_file";

    private String md5Sum;
    private String name;
    private long size;
    private String fingerprint;
    private long uploadDate;
    private String version;

    private String id;


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


    public long getUploadDate()
    {
        return uploadDate;
    }


    public void setUploadDate( final long uploadDate )
    {
        this.uploadDate = uploadDate;
    }


    public void setVersion( final String version )
    {
        this.version = version;
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


    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof RawEntity ) )
        {
            return false;
        }

        final RawEntity rawEntity = ( RawEntity ) o;

        if ( size != rawEntity.size )
        {
            return false;
        }
        if ( uploadDate != rawEntity.uploadDate )
        {
            return false;
        }
        if ( md5Sum != null ? !md5Sum.equals( rawEntity.md5Sum ) : rawEntity.md5Sum != null )
        {
            return false;
        }
        if ( name != null ? !name.equals( rawEntity.name ) : rawEntity.name != null )
        {
            return false;
        }
        if ( fingerprint != null ? !fingerprint.equals( rawEntity.fingerprint ) : rawEntity.fingerprint != null )
        {
            return false;
        }
        return !( version != null ? !version.equals( rawEntity.version ) : rawEntity.version != null );
    }


    @Override
    public int hashCode()
    {
        int result = md5Sum != null ? md5Sum.hashCode() : 0;
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        result = 31 * result + ( int ) ( size ^ ( size >>> 32 ) );
        result = 31 * result + ( fingerprint != null ? fingerprint.hashCode() : 0 );
        result = 31 * result + ( int ) ( uploadDate ^ ( uploadDate >>> 32 ) );
        result = 31 * result + ( version != null ? version.hashCode() : 0 );
        return result;
    }
}

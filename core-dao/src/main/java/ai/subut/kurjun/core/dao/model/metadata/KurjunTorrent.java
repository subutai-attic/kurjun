package ai.subut.kurjun.core.dao.model.metadata;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table( name = KurjunTorrent.TABLE_NAME )
@Access( AccessType.FIELD )
public class KurjunTorrent
{
    public static final String TABLE_NAME = "kurjun_torrent";

    private String id;
    private String absolutePath;
    private String templateId;


    public String getTemplateId()
    {
        return templateId;
    }


    public void setTemplateId( final String templateId )
    {
        this.templateId = templateId;
    }


    public String getAbsolutePath()
    {
        return absolutePath;
    }


    public void setAbsolutePath( final String absolutePath )
    {
        this.absolutePath = absolutePath;
    }


    public String getId()
    {
        return id;
    }


    public void setId( final String id )
    {
        this.id = id;
    }


    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof KurjunTorrent ) )
        {
            return false;
        }

        final KurjunTorrent that = ( KurjunTorrent ) o;

        if ( id != null ? !id.equals( that.id ) : that.id != null )
        {
            return false;
        }
        if ( absolutePath != null ? !absolutePath.equals( that.absolutePath ) : that.absolutePath != null )
        {
            return false;
        }
        return templateId != null ? templateId.equals( that.templateId ) : that.templateId == null;
    }


    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + ( absolutePath != null ? absolutePath.hashCode() : 0 );
        result = 31 * result + ( templateId != null ? templateId.hashCode() : 0 );
        return result;
    }
}

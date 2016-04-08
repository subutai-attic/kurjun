package ai.subut.kurjun.core.dao.model.metadata;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import ai.subut.kurjun.model.metadata.raw.RawData;
import ai.subut.kurjun.model.repository.ArtifactId;


@Entity
@Table( name = RawDataEntity.TABLE_NAME )
@Access( AccessType.FIELD )
public class RawDataEntity implements RawData
{

    public static final String TABLE_NAME = "raw_file";

    @EmbeddedId
    RepositoryArtifactId id;

    @Column(name = "owner" ,nullable = false)
    String owner;

    @Column(name = "name" ,nullable = false)
    private String name;

    @Column(name = "file_path")
    private String filePath = "";

    @Column(name = "size")
    private long size = 0;

    @Column(name = "upload_date")
    private long uploadDate;

    @Column(name = "version")
    private String version;


    public RawDataEntity()
    {

    }

    public RawDataEntity( String md5Sum, String context , int type)
    {
        id = new RepositoryArtifactId( md5Sum , context , type );
    }


    @Override
    public ArtifactId getId()
    {
        return id;
    }


    @Override
    public void setId( final ArtifactId id )
    {
        this.id = (RepositoryArtifactId)id;
    }


    @Override
    public String getFilePath()
    {
        return filePath;
    }


    @Override
    public void setFilePath( final String filePath )
    {
        this.filePath = filePath;
    }


    @Override
    public String getUniqId()
    {
        return ( this.id != null ) ? this.id.getContext() + "." + this.id.getMd5Sum() : "";
    }


    @Override
    public String getContext()
    {
        return ( this.id != null ) ? this.id.getContext() : "";
    }

    @Override
    public int getType()
    {
        return ( this.id != null ) ? this.id.getType() : 0;
    }


    @Override
    public String getMd5Sum()
    {
        return ( this.id != null ) ? this.id.getMd5Sum() : "";
    }



    @Override
    public long getUploadDate()
    {
        return uploadDate;
    }


    @Override
    public void setUploadDate( final long uploadDate )
    {
        this.uploadDate = uploadDate;
    }


    @Override
    public void setVersion( final String version )
    {
        this.version = version;
    }


    @Override
    public long getSize()
    {
        return size;
    }


    @Override
    public void setSize( final long size )
    {
        this.size = size;
    }


    @Override
    public String getName()
    {
        return name;
    }


    @Override
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


    @Override
    public void setOwner( final String owner )
    {
        this.owner = owner;
    }


    @Override
    public String getOwner()
    {
        return owner;
    }


}

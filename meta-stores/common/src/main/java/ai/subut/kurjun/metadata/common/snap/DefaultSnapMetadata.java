package ai.subut.kurjun.metadata.common.snap;


import java.util.List;

import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.snap.Framework;
import ai.subut.kurjun.model.metadata.snap.SnapMetadata;


/**
 * Default POJO implementation of {@link SnapMetadata}.
 *
 */
public class DefaultSnapMetadata implements SnapMetadata, SerializableMetadata
{

    private String md5Sum;
    private String name;
    private String version;
    private String vendor;
    private String source;
    private String owner;

    private List<Framework> frameworks;


    @Override
    public String getOwner()
    {
        return owner;
    }


    @Override
    public Object getId()
    {

        return md5Sum;
    }


    @Override
    public String getMd5Sum()
    {
        return md5Sum;
    }


    public void setMd5Sum( String md5Sum )
    {
        this.md5Sum = md5Sum ;
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


    @Override
    public String getFilePath()
    {
        return null;
    }


    public void setVersion( String version )
    {
        this.version = version;
    }


    @Override
    public String getVendor()
    {
        return vendor;
    }


    public void setVendor( String vendor )
    {
        this.vendor = vendor;
    }


    @Override
    public String getSource()
    {
        return source;
    }


    public void setSource( String source )
    {
        this.source = source;
    }


    @Override
    public List<Framework> getFrameworks()
    {
        return frameworks;
    }


    public void setFrameworks( List<Framework> frameworks )
    {
        this.frameworks = frameworks;
    }


    @Override
    public String serialize()
    {
        return MetadataUtils.JSON.toJson( this );
    }


}


package ai.subut.kurjun.metadata.common.snap;


import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Hex;

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

    private byte[] md5Sum;
    private String name;
    private String version;
    private String vendor;
    private String source;
    private List<Framework> frameworks;


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
        return version;
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


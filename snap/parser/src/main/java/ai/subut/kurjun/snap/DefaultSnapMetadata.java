package ai.subut.kurjun.snap;


import java.util.Arrays;
import java.util.List;

import ai.subut.kurjun.model.metadata.snap.Framework;
import ai.subut.kurjun.model.metadata.snap.SnapMetadata;


public class DefaultSnapMetadata implements SnapMetadata
{

    private byte[] md5;
    private String name;
    private String version;
    private String vendor;
    private String source;
    private List<Framework> frameworks;


    @Override
    public byte[] getMd5Sum()
    {
        return md5 != null ? Arrays.copyOf( md5, md5.length ) : null;
    }


    public void setMd5( byte[] md5 )
    {
        this.md5 = Arrays.copyOf( md5, md5.length );
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


}


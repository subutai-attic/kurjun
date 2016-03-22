package ai.subut.kurjun.riparser;


import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.subut.kurjun.model.index.ChecksummedResource;
import ai.subut.kurjun.model.index.Component;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Architecture;


/**
 * POJO implementation of {@link ReleaseFile} interface.
 *
 */
public class DefaultRelease implements ReleaseFile
{

    private String origin;
    private String label;
    private String suite;
    private String version;
    private String codename;
    private String date;
    private List<Architecture> architectures;
    private List<String> components;
    private String description;
    private List<ChecksummedResource> indices;
    private URL source;


    @Override
    public String getOrigin()
    {
        return origin;
    }


    public void setOrigin( String origin )
    {
        this.origin = origin;
    }


    @Override
    public String getLabel()
    {
        return label;
    }


    public void setLabel( String label )
    {
        this.label = label;
    }


    @Override
    public String getSuite()
    {
        return suite;
    }


    public void setSuite( String suite )
    {
        this.suite = suite;
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
    public String getCodename()
    {
        return codename;
    }


    public void setCodename( String codename )
    {
        this.codename = codename;
    }


    @Override
    public String getDate()
    {
        return date;
    }


    public void setDate( String date )
    {
        this.date = date;
    }


    @Override
    public List<Architecture> getArchitectures()
    {
        return architectures;
    }


    public void setArchitectures( List<Architecture> architectures )
    {
        this.architectures = architectures;
    }


    @Override
    public List<String> getComponents()
    {
        return components;
    }


    @Override
    public Component getComponent( String compName )
    {
        if ( components != null )
        {
            // prepare map of package indices by relative paths
            Map<String, ChecksummedResource> map = new HashMap<>();
            if ( indices != null )
            {
                for ( ChecksummedResource r : indices )
                {
                    map.put( r.getRelativePath(), r );
                }
            }

            for ( String c : components )
            {
                if ( compName.equals( c ) )
                {
                    return new DefaultComponent( c, map );
                }
            }
        }
        return null;
    }


    public void setComponents( List<String> components )
    {
        this.components = components;
    }


    @Override
    public String getDescription()
    {
        return description;
    }


    public void setDescription( String description )
    {
        this.description = description;
    }


    @Override
    public List<ChecksummedResource> getIndices()
    {
        return indices;
    }


    @Override
    public ChecksummedResource getIndexResource( String relativePath )
    {
        if ( indices != null )
        {
            for ( ChecksummedResource r : indices )
            {
                if ( relativePath.equals( r.getRelativePath() ) )
                {
                    return r;
                }
            }
        }
        return null;
    }


    public void setIndices( List<ChecksummedResource> indices )
    {
        this.indices = indices;
    }


    @Override
    public URL getSource()
    {
        return source;
    }


    public void setSource( URL source )
    {
        this.source = source;
    }


}


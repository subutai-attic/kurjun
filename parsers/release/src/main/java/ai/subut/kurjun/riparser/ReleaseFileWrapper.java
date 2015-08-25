package ai.subut.kurjun.riparser;


import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vafer.jdeb.debian.ControlFile;

import ai.subut.kurjun.model.index.ChecksummedResource;
import ai.subut.kurjun.model.index.Component;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Architecture;


/**
 * {@link ReleaseFile} implementation that wraps {@link ControlFile} instance constructed by fields of a release index
 * file.
 *
 */
class ReleaseFileWrapper implements ReleaseFile
{

    private final ControlFile cf;

    /**
     * This map holds all resources for all components in this release, the keys are the relative paths of resources.
     *
     * @see ChecksummedResource#getRelativePath()
     */
    private final Map<String, ChecksummedResource> indexResources = new HashMap<>();


    public ReleaseFileWrapper( ControlFile cf )
    {
        this.cf = cf;
    }


    @Override
    public String getOrigin()
    {
        return cf.get( ORIGIN_FIELD );
    }


    @Override
    public String getLabel()
    {
        return cf.get( LABEL_FILED );
    }


    @Override
    public String getSuite()
    {
        return cf.get( SUITE_FILED );
    }


    @Override
    public String getVersion()
    {
        return cf.get( VERSION_FILED );
    }


    @Override
    public String getCodename()
    {
        return cf.get( CODENAME_FILED );
    }


    @Override
    public String getDate()
    {
        return cf.get( DATE_FILED );
    }


    @Override
    public List<Architecture> getArchitectures()
    {
        String value = cf.get( ARCHITECTURES_FILED );
        if ( value == null )
        {
            return Collections.emptyList();
        }

        String[] arr = value.split( " " );
        List<Architecture> ls = new ArrayList<>();
        for ( String s : arr )
        {
            if ( !s.isEmpty() )
            {
                ls.add( Architecture.valueOf( s ) );
            }
        }
        return ls;
    }


    @Override
    public List<String> getComponents()
    {
        String value = cf.get( COMPONENTS_FILED );
        if ( value == null )
        {
            return Collections.emptyList();
        }

        String[] arr = value.split( " " );
        List<String> ls = new ArrayList<>();
        for ( String s : arr )
        {
            if ( !s.isEmpty() )
            {
                ls.add( s );
            }
        }
        return ls;
    }


    @Override
    public String getDescription()
    {
        return cf.get( DESCRIPTION_FILED );
    }


    @Override
    public List<ChecksummedResource> getIndices()
    {
        return new ArrayList<>( indexResources.values() );
    }


    @Override
    public ChecksummedResource getIndexResource( String relativePath )
    {
        return indexResources.get( relativePath );
    }


    @Override
    public Component getComponent( String compName )
    {
        List<String> components = getComponents();
        if ( components.contains( compName ) )
        {
            return new DefaultComponent( compName, indexResources );
        }
        return null;
    }


    @Override
    public URL getSource()
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }


    void setIndexResources( Map<String, ReleaseChecksummedResource> indexResources )
    {
        this.indexResources.putAll( indexResources );
    }


}


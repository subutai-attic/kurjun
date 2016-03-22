package ai.subut.kurjun.riparser;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.subut.kurjun.model.index.ChecksummedResource;
import ai.subut.kurjun.model.index.Component;


/**
 * Simple {@link Component} implementation that is constructed by selecting its index resources from resources
 * collection of a release that this component belongs to.
 *
 */
public class DefaultComponent implements Component
{

    private final String name;
    private final Map<String, ChecksummedResource> indices = new HashMap<>();


    /**
     * Constructor of a component.
     *
     * @param name name of a component
     * @param releaseIndices index resources of a release that this component belongs to
     */
    public DefaultComponent( String name, Map<String, ChecksummedResource> releaseIndices )
    {
        this.name = name;
        for ( Map.Entry<String, ChecksummedResource> e : releaseIndices.entrySet() )
        {
            String[] arr = e.getKey().split( "/" );
            if ( arr.length > 0 && arr[0].equals( name ) )
            {
                indices.put( e.getKey(), e.getValue() );
            }
        }
    }


    @Override
    public String getName()
    {
        return name;
    }


    @Override
    public List<ChecksummedResource> getIndicies()
    {
        return new ArrayList<>( indices.values() );
    }


    @Override
    public ChecksummedResource getIndexResource( String relativePath )
    {
        return indices.get( relativePath );
    }

}


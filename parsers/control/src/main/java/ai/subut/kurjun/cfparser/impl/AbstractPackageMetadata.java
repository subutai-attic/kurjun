package ai.subut.kurjun.cfparser.impl;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vafer.jdeb.debian.ControlFile;

import ai.subut.kurjun.model.metadata.Dependency;
import ai.subut.kurjun.model.metadata.PackageMetadata;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * An abstract package meta data class.
 */
public abstract class AbstractPackageMetadata implements PackageMetadata
{
    private static final Logger LOG = LoggerFactory.getLogger( BinaryPackageMetadata.class );
    protected ControlFile controlFile;
    private DependencyParser parser = new DependencyParser();
    private Map<String,List<Dependency>> depCache = new HashMap<>();
    private byte[] md5;
    private String filename;


    AbstractPackageMetadata( byte[] md5, String filename, ControlFile controlFile )
    {
        checkNotNull( "The md5 sum for the Debian package must not be null", md5 );
        checkNotNull( "The filename for the Debian package must not be null", filename );
        checkNotNull( "The control file argument cannot be null.", controlFile );

        // copy it so no one can fuck with the value later
        this.md5 = Arrays.copyOf( md5, md5.length );
        this.filename = filename;
        this.controlFile = controlFile;

    }


    /**
     * Accesses the dependencies cache to look for an already parsed list of
     * dependencies for a relationship based field value, and if found returns
     * it, if not it invokes the parser to generate the dependencies in a list
     * for the field and adds the entry into the cache.
     *
     * @param key the relationship field key used to lookup the dependency list
     * @return the cached dependency list or a parsed form of the raw value provided
     */
    protected List<Dependency> getCached( String key )
    {
        checkNotNull( key );

        String raw = controlFile.get( key );
        if ( raw == null || raw.isEmpty() )
        {
            return null;
        }

        List<Dependency> dependencies = depCache.get( key );

        if ( dependencies == null )
        {
            dependencies = parser.getDependencies( raw );
            depCache.put( key, dependencies );
        }

        return dependencies;
    }


    /**
     * Gets the md5 sum of the Debian package.
     *
     * @return the md5 sum of the Debian package
     */
    public byte[] getMd5Sum()
    {
        // again give back a copy so no one can fuck with it
        return Arrays.copyOf( md5, md5.length );
    }


    /**
     * Gets the filename of the Debian package.
     *
     * @return the filename of the Debian package
     */
    public String getFilename()
    {
        return filename;
    }
}

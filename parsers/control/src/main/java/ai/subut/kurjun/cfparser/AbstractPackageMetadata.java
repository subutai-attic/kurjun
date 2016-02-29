package ai.subut.kurjun.cfparser;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vafer.jdeb.debian.ControlFile;

import org.apache.commons.codec.binary.Hex;

import ai.subut.kurjun.model.metadata.apt.Dependency;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * An abstract package meta data class.
 */
public abstract class AbstractPackageMetadata implements PackageMetadata
{
    private static final Logger LOG = LoggerFactory.getLogger( BinaryPackageMetadata.class );
    protected ControlFile controlFile;
    private DependencyParser parser = new DependencyParser();
    private Map<String, List<Dependency>> depCache = new HashMap<>();
    private byte[] md5;
    private String filename;


    AbstractPackageMetadata( byte[] md5, String filename, ControlFile controlFile )
    {
        checkNotNull( md5, "The md5 sum for the Debian package must not be null" );
        checkNotNull( filename, "The filename for the Debian package must not be null" );
        checkNotNull( controlFile, "The control file argument cannot be null." );

        // copy it so no one can fuck with the value later
        this.md5 = Arrays.copyOf( md5, md5.length );
        this.filename = filename;
        this.controlFile = controlFile;
    }


    /**
     * Accesses the dependencies cache to look for an already parsed list of dependencies for a relationship based field
     * value, and if found returns it, if not it invokes the parser to generate the dependencies in a list for the field
     * and adds the entry into the cache.
     *
     * @param key the relationship field key used to lookup the dependency list
     *
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
    @Override
    public byte[] getMd5Sum()
    {
        // again give back a copy so no one can fuck with it
        return Arrays.copyOf( md5, md5.length );
    }


    @Override
    public Object getId()
    {
        // For package metadatas the unique identifier is its md5sum
        return Hex.encodeHexString( getMd5Sum() );
    }


    /**
     * Gets the filename of the Debian package.
     *
     * @return the filename of the Debian package
     */
    @Override
    public String getFilename()
    {
        return filename;
    }
}

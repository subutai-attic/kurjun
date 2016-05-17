package ai.subut.kurjun.cfparser;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vafer.jdeb.debian.ControlFile;

import ai.subut.kurjun.model.metadata.apt.Dependency;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * An abstract package meta data class.
 */
public abstract class AbstractPackageMetadata implements PackageMetadata
{
    protected ControlFile controlFile;
    private final DependencyParser parser = new DependencyParser();
    private final Map<String, List<Dependency>> depCache = new HashMap<>();
    private String md5;
    private final String filename;


    AbstractPackageMetadata( String md5, String filename, ControlFile controlFile )
    {
        checkNotNull( md5, "The md5 sum for the Debian package must not be null" );
        checkNotNull( filename, "The filename for the Debian package must not be null" );
        checkNotNull( controlFile, "The control file argument cannot be null." );

        // copy it so no one can fuck with the value later
        this.md5 = md5;
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
    public String getMd5Sum()
    {

        return this.md5;
    }


    @Override
    public Object getId()
    {
        // For package metadatas the unique identifier is its md5sum
        return md5;
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

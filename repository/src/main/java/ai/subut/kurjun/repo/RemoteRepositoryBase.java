package ai.subut.kurjun.repo;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.slf4j.Logger;

import org.apache.commons.codec.binary.Hex;

import com.google.inject.Inject;

import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.MetadataCache;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.RemoteRepository;
import ai.subut.kurjun.repo.cache.MetadataCacheFactory;
import ai.subut.kurjun.repo.cache.PackageCache;


/**
 * Base abstract class for non-local repositories. Common operations to non-local repositories should go here.
 */
abstract class RemoteRepositoryBase extends RepositoryBase implements RemoteRepository
{

    @Inject
    private PackageCache packageCache;

    @Inject
    private MetadataCacheFactory metadataCacheFactory;


    @Override
    public MetadataCache getMetadataCache()
    {
        return metadataCacheFactory.get( this );
    }


    protected abstract Logger getLogger();


    /**
     * Checks if there is a cached package file for the supplied meta data.
     *
     * @param metadata meta data for which package is looked up in cache
     *
     * @return stream of a package file if found in cache; {@code null} otherwise
     */
    protected InputStream checkCache( Metadata metadata )
    {
        if ( metadata.getMd5Sum() != null )
        {
            if ( packageCache.contains( metadata.getMd5Sum() ) )
            {
                return packageCache.get( metadata.getMd5Sum() );
            }
        }
        else
        {
            SerializableMetadata m = getPackageInfo( metadata );
            if ( m != null && packageCache.contains( m.getMd5Sum() ) )
            {
                return packageCache.get( m.getMd5Sum() );
            }
        }
        return null;
    }


    /**
     * Caches the supplied input stream of a package file. MD5 checksum of the package is returned in response so that
     * stream can be retrieved from the cache by
     * {@link RemoteRepositoryBase#checkCache(ai.subut.kurjun.model.metadata.Metadata)}
     * method.
     *
     * @param is input stream of package file to cache
     *
     * @return md5 checksum of the cached package file
     *
     * @see RemoteRepositoryBase#checkCache(ai.subut.kurjun.model.metadata.Metadata)
     */
    protected byte[] cacheStream( InputStream is )
    {
        Path target = null;
        try
        {
            target = Files.createTempFile( null, null );
            Files.copy( is, target, StandardCopyOption.REPLACE_EXISTING );
            return packageCache.put( target.toFile() );
        }
        catch ( IOException ex )
        {
            getLogger().error( "Failed to cache package", ex );
        }
        finally
        {
            if ( target != null )
            {
                target.toFile().delete();
            }
        }
        return null;
    }


    protected byte[] importTemplate( InputStream is, Metadata metadata )
    {

        return new byte[0];
    }


    protected File getTempFile()
    {
        try
        {
            Path target = Files.createTempFile( null, null );
            return target.toFile();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return null;
    }


    protected byte[] put( File file )
    {
        return packageCache.put( file );
    }


    public abstract String getMd5();


    public boolean isUpdated( String md5 )
    {
        return !md5.equalsIgnoreCase( getMd5() );
    }


    public abstract List<SerializableMetadata> getCachedData();


    protected void deleteCache( byte[] md5 )
    {
        boolean deleted = packageCache.delete( md5 );
        if ( deleted )
        {
            getLogger().debug( "Package with md5 {} deleted from the cache", Hex.encodeHexString( md5 ) );
        }
        else
        {
            getLogger().debug( "Package with md5 {} cannot be found in the cache", Hex.encodeHexString( md5 ) );
        }
    }
}


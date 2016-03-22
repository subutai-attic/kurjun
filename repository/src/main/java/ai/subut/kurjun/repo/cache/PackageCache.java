package ai.subut.kurjun.repo.cache;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.storage.factory.FileStoreFactory;


/**
 * Package cache that can be used in non-local repository implementations where packages fetched from remote repository
 * need to be cached locally.
 * <p>
 * This is basically a wrapper to local file system backed file store. A {@link CachingContext} instance is used to
 * inject such a file store.
 */
public class PackageCache
{
    private static final Logger LOGGER = LoggerFactory.getLogger( PackageCache.class );

    private FileStoreFactory fileStoreFactory;

    private final String LOG_MESSAGE = "Failed to check cache";
    private KurjunContext context = new CachingContext();


    @Inject
    public PackageCache( KurjunProperties kurjunProperties, FileStoreFactory fileStoreFactory )
    {
        this.fileStoreFactory = fileStoreFactory;
        CachingContext.setProperties( kurjunProperties.getContextProperties( context ) );
    }


    public boolean contains( byte[] md5 )
    {
        try
        {
            return getFileStore().contains( md5 );
        }
        catch ( IOException ex )
        {
            LOGGER.info( LOG_MESSAGE, ex );
            return false;
        }
    }
    
    
    public boolean delete( byte[] md5 )
    {
        try
        {
            return getFileStore().remove( md5 );
        }
        catch ( IOException ex )
        {
            LOGGER.info( "Failed to delete from cache", ex );
            return false;
        }
    }

    public InputStream get( byte[] md5 )
    {
        try
        {
            return getFileStore().get( md5 );
        }
        catch ( IOException ex )
        {
            LOGGER.info( LOG_MESSAGE, ex );
            return null;
        }
    }


    public byte[] put( File file )
    {
        try
        {
            return getFileStore().put( file );
        }
        catch ( IOException ex )
        {
            LOGGER.info( LOG_MESSAGE, ex );
            return null;
        }
    }


    private FileStore getFileStore()
    {
        return fileStoreFactory.create( context );
    }

}


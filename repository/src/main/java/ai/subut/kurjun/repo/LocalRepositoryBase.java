package ai.subut.kurjun.repo;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;

import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.storage.FileStore;


/**
 * Abstract base class for local repositories.
 *
 */
abstract class LocalRepositoryBase extends RepositoryBase implements LocalRepository
{


    @Override
    public InputStream getPackage( Metadata metadata )
    {
        try
        {
            if ( metadata.getMd5Sum() != null )
            {
                return getPackageByMd5( metadata.getMd5Sum() );
            }
            if ( metadata.getName() != null )
            {
                return getPackage( metadata.getName(), metadata.getVersion() );
            }
        }
        catch ( IOException ex )
        {
            getLogger().error( "Failed to get package", ex );
            return null;
        }
        throw new IllegalArgumentException( "Metadata has neither md5 checksum nor name and version" );
    }


    /**
     * Gets logger instance associated classes that extend this abstract class.
     *
     * @return logger instance
     */
    protected abstract Logger getLogger();


    /**
     * Gets meta data store to be used in implementations classes of this abstract class.
     *
     * @return meta data store
     */
    protected abstract PackageMetadataStore getMetadataStore();


    /**
     * Gets file store to be used in implementations classes of this abstract class.
     *
     * @return file store
     */
    protected abstract FileStore getFileStore();


    private InputStream getPackageByMd5( byte[] md5Sum ) throws IOException
    {
        PackageMetadataStore metadataStore = getMetadataStore();
        FileStore fileStore = getFileStore();

        if ( metadataStore.contains( md5Sum ) )
        {
            if ( fileStore.contains( md5Sum ) )
            {
                return fileStore.get( md5Sum );
            }
            throw new IllegalStateException( "File does not exist for metadata" );
        }
        return null;
    }


    private InputStream getPackage( String name, String version ) throws IOException
    {
        PackageMetadataStore metadataStore = getMetadataStore();
        FileStore fileStore = getFileStore();

        List<SerializableMetadata> items = metadataStore.get( name );
        if ( items.isEmpty() )
        {
            return null;
        }

        if ( version != null )
        {
            for ( SerializableMetadata item : items )
            {
                if ( version.equals( item.getVersion() ) )
                {
                    return fileStore.get( item.getMd5Sum() );
                }
            }
        }
        else
        {
            // sort by version in descending fasion and get the first item which is will be the latest version
            items.sort( (m1, m2) -> -1 * m1.getVersion().compareTo( m2.getVersion() ) );
            return fileStore.get( items.get( 0 ).getMd5Sum() );
        }
        return null;
    }
}


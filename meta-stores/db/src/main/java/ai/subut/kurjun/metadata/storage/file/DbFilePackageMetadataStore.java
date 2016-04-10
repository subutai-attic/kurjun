package ai.subut.kurjun.metadata.storage.file;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.inject.Inject;
import com.google.inject.ProvisionException;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.metadata.common.MetadataListingImpl;
import ai.subut.kurjun.model.metadata.MetadataListing;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


class DbFilePackageMetadataStore implements PackageMetadataStore
{
    private static final String MAP_NAME = "checksum-to-metadata";

    int batchSize = 1000;

    private Path fileDbPath;


    /**
     * Constructs a package metadata store backed by a file db. A directory should be given where file db will be
     * created or there should be a binding of {@link String} instance annotated with name {@link
     * DbFilePackageMetadataStoreModule#DB_FILE_LOCATION_NAME}.
     *
     * @param fileDbPath parent directory
     */
    public DbFilePackageMetadataStore( String fileDbPath )
    {
        this.fileDbPath = Paths.get( fileDbPath, "metadata" );
    }


    @Inject
    public DbFilePackageMetadataStore( KurjunProperties properties, @Assisted KurjunContext context )
    {
        String fileDbDirectory = properties.get( DbFilePackageMetadataStoreModule.DB_FILE_LOCATION_NAME );
        if ( fileDbDirectory == null )
        {
            throw new ProvisionException( "File db location not specified for context " + context );
        }
        this.fileDbPath = Paths.get( fileDbDirectory, context.getName(), "metadata" );
    }


    @Override
    public boolean contains( Object id ) throws IOException
    {
        FileDb fileDb = null;
        try
        {
            fileDb = new FileDb( fileDbPath.toString(), true );
            boolean contains = fileDb.contains( MAP_NAME, id );

            return contains;
        }
        catch(Exception ex)
        {
             return false;
        }
        finally
        {
            if ( fileDb != null ) fileDb.close();
        }
    }


    @Override
    public SerializableMetadata get( Object id ) throws IOException
    {
        FileDb fileDb = null;

        try
        {
            fileDb = new FileDb( fileDbPath.toString(), true );
            return fileDb.get( MAP_NAME, id, SerializableMetadata.class );
        }
        finally
        {
            if(fileDb != null)
                fileDb.close();
        }
    }


    @Override
    public List<SerializableMetadata> get( String name ) throws IOException
    {
        if ( name == null )
        {
            return Collections.emptyList();
        }

        Collection<SerializableMetadata> items;

        FileDb fileDb = null;

        try
        {
            fileDb = new FileDb( fileDbPath.toString(), true );
            Map<String, SerializableMetadata> map = fileDb.get( MAP_NAME );
            items = map.values();
        }
        finally
        {
            if(fileDb != null)
                fileDb.close();
        }


        List<SerializableMetadata> result = new LinkedList<>();
        for ( SerializableMetadata item : items )
        {
            if ( name.equals( item.getName() ) )
            {
                result.add( item );
            }
        }
        return result;
    }


    @Override
    public boolean put( SerializableMetadata meta ) throws IOException
    {
        if ( !contains( meta.getId() ) )
        {
            FileDb fileDb = null;

            try
            {
                fileDb = new FileDb( fileDbPath.toString(), false );
                fileDb.put( MAP_NAME, meta.getId(), meta );
            }
            finally
            {
                if(fileDb != null)
                    fileDb.close();
            }

            return true;
        }
        return false;
    }


    @Override
    public boolean remove( Object id ) throws IOException
    {
        FileDb fileDb = null;

        try
        {
            fileDb = new FileDb( fileDbPath.toString(), false );

            return fileDb.remove( MAP_NAME, id ) != null;
        }
        catch(Exception ex)
        {
            return false;
        }
        finally
        {
            if(fileDb != null)
                fileDb.close();
        }
    }


    @Override
    public MetadataListing list() throws IOException
    {
        return listPackageMetadata( null );
    }


    @Override
    public MetadataListing listNextBatch( MetadataListing listing ) throws IOException
    {
        if ( listing.isTruncated() && listing.getMarker() != null )
        {
            return listPackageMetadata( listing.getMarker().toString() );
        }
        throw new IllegalStateException( "Listing is not truncated or no marker specified" );
    }


    private MetadataListing listPackageMetadata( final String marker ) throws IOException
    {
        Map<String, SerializableMetadata> map;
        FileDb fileDb = null;

        try
        {
            fileDb = new FileDb( fileDbPath.toString(), true );
            map = fileDb.get( MAP_NAME );
        }
        finally
        {
            if(fileDb != null)
                fileDb.close();
        }

        Collection<SerializableMetadata> items = map.values();

        // sort items by names
        Stream<SerializableMetadata> stream =
                items.stream().sorted( ( m1, m2 ) -> m1.getName().compareTo( m2.getName() ) );

        // filter items if marker is set
        if ( marker != null )
        {
            stream = stream.filter( m -> m.getName().compareTo( marker ) > 0 );
        }

        MetadataListingImpl pml = new MetadataListingImpl();

        // terminate stream limiting result set to (batch size + 1)
        // one more item is used to determine whether there is more result to fetch
        Iterator<SerializableMetadata> it = stream.limit( batchSize + 1 ).iterator();
        while ( it.hasNext() )
        {
            SerializableMetadata item = it.next();
            if ( pml.getPackageMetadata().size() < batchSize )
            {
                pml.getPackageMetadata().add( item );
                pml.setMarker( item.getName() );
            }
            else
            {
                pml.setTruncated( true );
                break;
            }
        }
        return pml;
    }
}


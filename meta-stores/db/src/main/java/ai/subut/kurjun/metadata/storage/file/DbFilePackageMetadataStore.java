package ai.subut.kurjun.metadata.storage.file;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Hex;

import com.google.inject.Inject;
import com.google.inject.ProvisionException;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.metadata.common.PackageMetadataListingImpl;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.metadata.PackageMetadata;
import ai.subut.kurjun.model.metadata.PackageMetadataListing;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


class DbFilePackageMetadataStore implements PackageMetadataStore
{
    private static final String MAP_NAME = "checksum-to-metadata";

    int batchSize = 1000;

    private Path fileDbPath;


    /**
     * Constructs a package metadata store backed by a file db. A directory should be given where file db will be
     * created or there should be a binding of {@link String} instance annotated with name
     * {@link DbFilePackageMetadataStoreModule#DB_FILE_LOCATION_NAME}.
     *
     * @param location parent directory
     */
    public DbFilePackageMetadataStore( String fileDbPath )
    {
        this.fileDbPath = Paths.get( fileDbPath, "metadata" );
    }


    @Inject
    public DbFilePackageMetadataStore( KurjunProperties properties, @Assisted KurjunContext context )
    {
        Properties cp = properties.getContextProperties( context );
        String fileDbDirectory = cp.getProperty( DbFilePackageMetadataStoreModule.DB_FILE_LOCATION_NAME );
        if ( fileDbDirectory == null )
        {
            throw new ProvisionException( "File db location not specified for context " + context );
        }
        this.fileDbPath = Paths.get( fileDbDirectory, "metadata" );
    }


    @Override
    public boolean contains( byte[] md5 ) throws IOException
    {
        try ( FileDb fileDb = new FileDb( fileDbPath.toString() ) )
        {
            return fileDb.contains( MAP_NAME, Hex.encodeHexString( md5 ) );
        }
    }


    @Override
    public SerializableMetadata get( byte[] md5 ) throws IOException
    {
        try ( FileDb fileDb = new FileDb( fileDbPath.toString() ) )
        {
            return fileDb.get( MAP_NAME, Hex.encodeHexString( md5 ), SerializableMetadata.class );
        }
    }


    @Override
    public boolean put( SerializableMetadata meta ) throws IOException
    {
        if ( !contains( meta.getMd5Sum() ) )
        {
            try ( FileDb fileDb = new FileDb( fileDbPath.toString() ) )
            {
                fileDb.put( MAP_NAME, Hex.encodeHexString( meta.getMd5Sum() ), meta );
            }
            return true;
        }
        return false;
    }


    @Override
    public boolean remove( byte[] md5 ) throws IOException
    {
        try ( FileDb fileDb = new FileDb( fileDbPath.toString() ) )
        {
            return fileDb.remove( MAP_NAME, Hex.encodeHexString( md5 ) ) != null;
        }
    }


    @Override
    public PackageMetadataListing list() throws IOException
    {
        return listPackageMetadata( null );
    }


    @Override
    public PackageMetadataListing listNextBatch( PackageMetadataListing listing ) throws IOException
    {
        if ( listing.isTruncated() && listing.getMarker() != null )
        {
            return listPackageMetadata( listing.getMarker().toString() );
        }
        throw new IllegalStateException( "Listing is not truncated or no marker specified" );
    }


    private PackageMetadataListing listPackageMetadata( final String marker ) throws IOException
    {
        Map<String, PackageMetadata> map;
        try ( FileDb fileDb = new FileDb( fileDbPath.toString() ) )
        {
            map = fileDb.get( MAP_NAME );
        }
        Collection<PackageMetadata> items = map.values();

        // sort items by names
        Stream<PackageMetadata> stream = items.stream().sorted(
                (m1, m2) -> m1.getPackage().compareTo( m2.getPackage() ) );

        // filter items if marker is set
        if ( marker != null )
        {
            stream = stream.filter( m -> m.getPackage().compareTo( marker ) > 0 );
        }

        PackageMetadataListingImpl pml = new PackageMetadataListingImpl();

        // terminate stream limiting result set to (batch size + 1)
        // one more item is used to determine whether there is more result to fetch
        Iterator<PackageMetadata> it = stream.limit( batchSize + 1 ).iterator();
        while ( it.hasNext() )
        {
            PackageMetadata item = it.next();
            if ( pml.getPackageMetadata().size() < batchSize )
            {
                pml.getPackageMetadata().add( MetadataUtils.serializablePackageMetadata( item ) );
                pml.setMarker( item.getPackage() );
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


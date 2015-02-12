package ai.subut.kurjun.metadata.storage.file;


import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Hex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;

import ai.subut.kurjun.metadata.common.DependencyImpl;
import ai.subut.kurjun.metadata.common.PackageMetadataImpl;
import ai.subut.kurjun.metadata.common.PackageMetadataListingImpl;
import ai.subut.kurjun.model.metadata.Dependency;
import ai.subut.kurjun.model.metadata.PackageMetadata;
import ai.subut.kurjun.model.metadata.PackageMetadataListing;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;


public class DbFilePackageMetadataStore implements PackageMetadataStore
{
    private static final Gson GSON;

    Path location;
    int batchSize = 1000;


    static
    {
        GsonBuilder gb = new GsonBuilder().setPrettyPrinting();
        InstanceCreator<Dependency> depInstanceCreator = new InstanceCreator<Dependency>()
        {
            @Override
            public Dependency createInstance( Type type )
            {
                return new DependencyImpl();
            }
        };
        gb.registerTypeAdapter( Dependency.class, depInstanceCreator );

        GSON = gb.create();
    }


    public DbFilePackageMetadataStore( String location )
    {
        this.location = Paths.get( location );
    }


    @Override
    public boolean contains( byte[] md5 ) throws IOException
    {
        try ( MapDb db = new MapDb( location ) )
        {
            return db.getMap().containsKey( Hex.encodeHexString( md5 ) );
        }
    }


    @Override
    public PackageMetadata get( byte[] md5 ) throws IOException
    {
        try ( MapDb db = new MapDb( location ) )
        {
            String metadata = db.getMap().get( Hex.encodeHexString( md5 ) );
            return GSON.fromJson( metadata, PackageMetadataImpl.class );
        }
    }


    @Override
    public boolean put( PackageMetadata meta ) throws IOException
    {
        String hex = Hex.encodeHexString( meta.getMd5Sum() );
        try ( MapDb db = new MapDb( location ) )
        {
            return db.getMap().putIfAbsent( hex, GSON.toJson( meta ) ) == null;
        }
    }


    @Override
    public boolean remove( byte[] md5 ) throws IOException
    {
        try ( MapDb db = new MapDb( location ) )
        {
            return db.getMap().remove( Hex.encodeHexString( md5 ) ) != null;
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
        PackageMetadataListingImpl pml = new PackageMetadataListingImpl();
        try ( MapDb db = new MapDb( location ) )
        {
            // first sort by keys
            Comparator<Map.Entry<String, String>> comparator = new Comparator<Map.Entry<String, String>>()
            {
                @Override
                public int compare( Map.Entry<String, String> e1, Map.Entry<String, String> e2 )
                {
                    return e1.getKey().compareToIgnoreCase( e2.getKey() );
                }
            };
            Stream<Map.Entry<String, String>> stream = db.getMap().entrySet().stream().sorted( comparator );

            // if marker is given filter result set
            if ( marker != null && !marker.isEmpty() )
            {
                Predicate<Map.Entry<String, String>> predicate = new Predicate<Map.Entry<String, String>>()
                {
                    @Override
                    public boolean test( Map.Entry<String, String> e )
                    {
                        return e.getKey().compareToIgnoreCase( marker ) > 0;
                    }
                };
                stream = stream.filter( predicate );
            }

            // terminate stream limiting result set to (batch size + 1)
            // one more item is used to determine whether there is more result to fetch
            Iterator<Map.Entry<String, String>> it = stream.limit( batchSize + 1 ).iterator();
            while ( it.hasNext() && pml.getPackageMetadata().size() < batchSize )
            {
                Map.Entry<String, String> item = it.next();
                PackageMetadataImpl meta = GSON.fromJson( item.getValue(), PackageMetadataImpl.class );
                pml.getPackageMetadata().add( meta );
                pml.setMarker( item.getKey() );
            }
            pml.setTruncated( it.hasNext() );
        }
        return pml;
    }

}


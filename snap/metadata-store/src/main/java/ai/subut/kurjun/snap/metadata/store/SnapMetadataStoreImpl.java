package ai.subut.kurjun.snap.metadata.store;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.binary.Hex;

import com.google.inject.Inject;
import com.google.inject.ProvisionException;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.model.metadata.snap.SnapMetadata;
import ai.subut.kurjun.model.metadata.snap.SnapMetadataFilter;
import ai.subut.kurjun.model.metadata.snap.SnapMetadataStore;


/**
 * Snap metadata store that is backed by a file db.
 *
 */
class SnapMetadataStoreImpl implements SnapMetadataStore
{

    private static final String MAP_NAME = "snap-metadata";
    private String fileDbPath;


    @Inject
    public SnapMetadataStoreImpl( KurjunProperties properties, @Assisted KurjunContext context )
    {
        Properties cp = properties.getContextProperties( context );
        String dbFilePath = cp.getProperty( SnapMetadataStoreModule.DB_FILE_PATH );
        if ( dbFilePath != null )
        {
            this.fileDbPath = dbFilePath;
        }
        else
        {
            throw new ProvisionException( "File db path not specified for context " + context );
        }
    }


    SnapMetadataStoreImpl( String fileDbPath )
    {
        this.fileDbPath = fileDbPath;
    }


    @Override
    public boolean contains( byte[] md5 ) throws IOException
    {
        String md5hex = Hex.encodeHexString( md5 );
        try ( FileDb fileDb = new FileDb( fileDbPath ) )
        {
            return fileDb.contains( MAP_NAME, md5hex );
        }
    }


    @Override
    public SnapMetadata get( byte[] md5 ) throws IOException
    {
        String md5hex = Hex.encodeHexString( md5 );
        try ( FileDb fileDb = new FileDb( fileDbPath ) )
        {
            return fileDb.get( MAP_NAME, md5hex, SnapMetadata.class );
        }
    }


    @Override
    public List<SnapMetadata> list( SnapMetadataFilter filter ) throws IOException
    {
        Map<String, SnapMetadata> map;
        try ( FileDb fileDb = new FileDb( fileDbPath ) )
        {
            map = fileDb.get( MAP_NAME );
        }
        SnapMetadata[] items = map.values().stream().filter( filter ).toArray( SnapMetadata[]::new );

        List<SnapMetadata> ls = new ArrayList<>( items.length );
        ls.addAll( Arrays.asList( items ) );
        return ls;
    }


    @Override
    public boolean put( SnapMetadata metadata ) throws IOException
    {
        if ( contains( metadata.getMd5() ) )
        {
            return false;
        }
        try ( FileDb fileDb = new FileDb( fileDbPath ) )
        {
            fileDb.put( MAP_NAME, Hex.encodeHexString( metadata.getMd5() ), metadata );
            return true;
        }
    }


    @Override
    public boolean remove( byte[] md5 ) throws IOException
    {
        try ( FileDb fileDb = new FileDb( fileDbPath ) )
        {
            return fileDb.remove( MAP_NAME, Hex.encodeHexString( md5 ) ) != null;
        }
    }

}


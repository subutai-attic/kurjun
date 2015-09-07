package ai.subut.kurjun.http.snap;


import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.http.HttpServer;
import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.http.ServletUtils;
import ai.subut.kurjun.model.metadata.snap.SnapMetadata;
import ai.subut.kurjun.model.metadata.snap.SnapMetadataFilter;
import ai.subut.kurjun.model.metadata.snap.SnapMetadataStore;
import ai.subut.kurjun.model.metadata.snap.SnapUtils;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.snap.metadata.store.SnapMetadataStoreFactory;
import ai.subut.kurjun.snap.metadata.store.SnapMetadataStoreModule;
import ai.subut.kurjun.storage.factory.FileStoreFactory;


@Singleton
class SnapServlet extends HttpServletBase
{

    private static final Logger LOGGER = LoggerFactory.getLogger( SnapServlet.class );

    static final String SNAPS_GET_PATH = "get";
    static final String SNAPS_MD5_PARAM = "md5";
    static final String SNAPS_NAME_PARAM = "name";
    static final String SNAPS_VERSION_PARAM = "version";

    @Inject
    private KurjunProperties properties;

    @Inject
    private SnapMetadataStoreFactory metadataStoreFactory;

    @Inject
    private FileStoreFactory fileStoreFactory;

    private KurjunContext context;


    @Override
    public void init() throws ServletException
    {
        this.context = HttpServer.CONTEXT;
    }


    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        List<String> paths = ServletUtils.splitPath( req.getPathInfo() );
        if ( paths.size() == 1 && paths.get( 0 ).equals( SNAPS_GET_PATH ) )
        {
            String md5 = req.getParameter( SNAPS_MD5_PARAM );
            if ( md5 != null )
            {
                getByMd5( md5, resp );
                return;
            }

            String name = req.getParameter( SNAPS_NAME_PARAM );
            String version = req.getParameter( SNAPS_VERSION_PARAM );
            if ( name != null )
            {
                getByNameAndVersion( name, version, resp );
            }
            else
            {
                String msg = "Neither 'md5' nor 'name' and 'version' params specified";
                badRequest( resp, msg );
            }
        }
        else
        {
            badRequest( resp, "Invalid request path: " + req.getPathInfo() );
        }
    }


    @Override
    protected void doDelete( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        String md5 = req.getParameter( SNAPS_MD5_PARAM );
        if ( md5 != null )
        {
            deletePackage( md5, resp );
        }
        else
        {
            badRequest( resp, "Provide md5 checksum of the package to remove" );
        }
    }


    protected void getByMd5( String md5, HttpServletResponse resp ) throws IOException
    {
        SnapMetadataStore metadataStore = getMetadataStore();
        try
        {
            byte[] md5bytes = Hex.decodeHex( md5.toCharArray() );
            if ( metadataStore.contains( md5bytes ) )
            {
                streamPackage( metadataStore.get( md5bytes ), resp );
            }
            else
            {
                notFound( resp, "Package not found in metadata store" );
            }
        }
        catch ( DecoderException ex )
        {
            LOGGER.info( "Invalid md5 provided: {}", md5, ex );
            badRequest( resp, "Invalid MD5 checksum" );
        }
    }


    protected void getByNameAndVersion( String name, String version, HttpServletResponse resp ) throws IOException
    {
        SnapMetadataStore metadataStore = getMetadataStore();
        List<SnapMetadata> ls = metadataStore.list( SnapMetadataFilter.getNameFilter( name ) );
        // filter by version if specified
        if ( version != null )
        {
            Iterator<SnapMetadata> it = ls.iterator();
            while ( it.hasNext() )
            {
                if ( !it.next().getVersion().equals( version ) )
                {
                    it.remove();
                }
            }
        }

        if ( ls.isEmpty() )
        {
            notFound( resp, "No package(s) found" );
        }
        else if ( ls.size() == 1 )
        {
            streamPackage( ls.get( 0 ), resp );
        }
        else
        {
            String index = makePackagesIndex( ls );
            ok( resp, index );
        }
    }


    protected void deletePackage( String md5, HttpServletResponse resp ) throws IOException
    {
        SnapMetadataStore metadataStore = getMetadataStore();
        try
        {
            byte[] md5bytes = Hex.decodeHex( md5.toCharArray() );
            if ( metadataStore.contains( md5bytes ) )
            {
                FileStore fileStore = getFileStore();
                fileStore.remove( md5bytes );
                metadataStore.remove( md5bytes );
                ok( resp, "Package successfully removed" );
            }
            else
            {
                notFound( resp, "Package with supplied checksum not found" );
            }
        }
        catch ( DecoderException ex )
        {
            LOGGER.info( "Invalid md5 provided: {}", md5, ex );
            badRequest( resp, "Invalid md5 checksum provided" );
        }
    }


    private void streamPackage( SnapMetadata meta, HttpServletResponse resp ) throws IOException
    {
        FileStore fileStore = getFileStore();
        try ( InputStream is = fileStore.get( meta.getMd5() ) )
        {
            if ( is != null )
            {
                resp.setStatus( HttpServletResponse.SC_OK );
                resp.setHeader( "Content-Disposition", "attachment; filename=" + SnapUtils.makeFileName( meta ) );
                IOUtils.copy( is, resp.getOutputStream() );
            }
            else
            {
                notFound( resp, "Package not found in file store" );
            }
        }
    }


    private String makePackagesIndex( List<SnapMetadata> ls )
    {
        // TODO: maybe better to render html with links to packages
        String delim = ";";
        StringBuilder sb = new StringBuilder();
        sb.append( "count: " ).append( ls.size() ).append( System.lineSeparator() );
        for ( SnapMetadata meta : ls )
        {
            sb.append( "name: " ).append( meta.getName() ).append( delim );
            sb.append( "version: " ).append( meta.getVersion() ).append( delim );
            sb.append( "vendor: " ).append( meta.getName() ).append( delim );
            sb.append( "md5: " ).append( Hex.encodeHexString( meta.getMd5() ) );
            sb.append( System.lineSeparator() );
        }
        return sb.toString();
    }


    private FileStore getFileStore()
    {
        return fileStoreFactory.create( context );
    }


    private SnapMetadataStore getMetadataStore()
    {
        return getMetadataStore( properties, context, metadataStoreFactory );
    }


    static SnapMetadataStore getMetadataStore( KurjunProperties properties, KurjunContext context,
                                               SnapMetadataStoreFactory metadataStoreFactory )
    {
        Properties cp = properties.getContextProperties( context );
        String type = cp.getProperty( SnapMetadataStoreModule.TYPE, "to_avoid_npe" );
        switch ( type )
        {
            case SnapMetadataStoreModule.NOSQL_DB:
                return metadataStoreFactory.createCassandraStore( context );
            case SnapMetadataStoreModule.FILE_DB:
            default:
                return metadataStoreFactory.create( context );
        }
    }

}


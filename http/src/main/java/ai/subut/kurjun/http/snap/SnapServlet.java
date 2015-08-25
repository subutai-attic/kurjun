package ai.subut.kurjun.http.snap;


import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

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

import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.http.ServletUtils;
import ai.subut.kurjun.model.metadata.snap.SnapMetadata;
import ai.subut.kurjun.model.metadata.snap.SnapMetadataFilter;
import ai.subut.kurjun.model.metadata.snap.SnapMetadataStore;
import ai.subut.kurjun.model.metadata.snap.SnapUtils;
import ai.subut.kurjun.model.storage.FileStore;


@Singleton
class SnapServlet extends HttpServletBase
{

    private static final Logger LOGGER = LoggerFactory.getLogger( SnapServlet.class );

    private static final String SNAPS_GET_PATH = "get";
    private static final String SNAPS_MD5_PARAM = "md5";
    private static final String SNAPS_NAME_PARAM = "name";
    private static final String SNAPS_VERSION_PARAM = "version";

    @Inject
    private SnapMetadataStore metadataStore;

    @Inject
    private FileStore fileStore;


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
                String msg = "Neither 'md5' nor 'name' and 'version' params specififed";
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
        if ( md5 == null )
        {
            badRequest( resp, "Provide md5 checksum of the package to remove" );
            return;
        }
        try
        {
            byte[] md5bytes = Hex.decodeHex( md5.toCharArray() );
            if ( metadataStore.contains( md5bytes ) )
            {
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
            badRequest( resp, "Invalid md5 checksum provided" );
        }
    }


    private void getByMd5( String md5, HttpServletResponse resp ) throws IOException
    {
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


    private void getByNameAndVersion( String name, String version, HttpServletResponse resp ) throws IOException
    {

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


    private void streamPackage( SnapMetadata meta, HttpServletResponse resp ) throws IOException
    {
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


}


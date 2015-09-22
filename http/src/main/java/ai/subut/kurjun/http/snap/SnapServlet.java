package ai.subut.kurjun.http.snap;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.common.utils.SnapUtils;
import ai.subut.kurjun.http.HttpServer;
import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.http.ServletUtils;
import ai.subut.kurjun.metadata.common.snap.DefaultSnapMetadata;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.metadata.MetadataListing;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.snap.SnapMetadata;
import ai.subut.kurjun.model.storage.FileStore;
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
    private Gson gson;

    @Inject
    private PackageMetadataStoreFactory metadataStoreFactory;

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
        PackageMetadataStore metadataStore = getMetadataStore();
        try
        {
            byte[] md5bytes = Hex.decodeHex( md5.toCharArray() );
            if ( metadataStore.contains( md5bytes ) )
            {
                SerializableMetadata meta = metadataStore.get( md5bytes );
                DefaultSnapMetadata snapMeta = gson.fromJson( meta.serialize(), DefaultSnapMetadata.class );
                streamPackage( snapMeta, resp );
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
        PackageMetadataStore metadataStore = getMetadataStore();

        MetadataListing list;
        List<SerializableMetadata> all = new LinkedList<>();
        do
        {
            list = metadataStore.list();
            List<SerializableMetadata> filtered = filterByNameAndVersion( name, version, list.getPackageMetadata() );
            all.addAll( filtered );
        }
        while ( list.isTruncated() );

        if ( all.isEmpty() )
        {
            notFound( resp, "No package(s) found" );
        }
        else if ( all.size() == 1 )
        {
            DefaultSnapMetadata meta = gson.fromJson( all.get( 0 ).serialize(), DefaultSnapMetadata.class );
            streamPackage( meta, resp );
        }
        else
        {
            String index = makePackagesIndex( all );
            ok( resp, index );
        }
    }


    protected void deletePackage( String md5, HttpServletResponse resp ) throws IOException
    {
        PackageMetadataStore metadataStore = getMetadataStore();
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
        try ( InputStream is = fileStore.get( meta.getMd5Sum() ) )
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


    private List<SerializableMetadata> filterByNameAndVersion( String name, String version,
                                                               Collection<SerializableMetadata> ls )
    {
        Pattern namePattern = Pattern.compile( name, Pattern.CASE_INSENSITIVE );
        List<SerializableMetadata> result = new LinkedList<>();
        for ( SerializableMetadata m : ls )
        {
            if ( namePattern.matcher( m.getName() ).matches() )
            {
                if ( version != null && !version.equals( m.getVersion() ) )
                {
                    continue;
                }
                result.add( m );
            }
        }
        return result;
    }


    private String makePackagesIndex( List<SerializableMetadata> ls )
    {
        // TODO: maybe better to render html with links to packages
        String delim = "; ";
        StringBuilder sb = new StringBuilder();
        sb.append( "count: " ).append( ls.size() ).append( System.lineSeparator() );
        for ( SerializableMetadata meta : ls )
        {
            sb.append( "md5: " ).append( Hex.encodeHexString( meta.getMd5Sum() ) ).append( delim );
            sb.append( "name: " ).append( meta.getName() ).append( delim );
            sb.append( "version: " ).append( meta.getVersion() ).append( delim );
            sb.append( System.lineSeparator() );
        }
        return sb.toString();
    }


    private FileStore getFileStore()
    {
        return fileStoreFactory.create( context );
    }


    private PackageMetadataStore getMetadataStore()
    {
        return metadataStoreFactory.create( context );
    }


}


package ai.subut.kurjun.http.snap;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.http.HttpServer;
import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.http.ServletUtils;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.snap.SnapMetadata;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.snap.service.SnapMetadataParser;
import ai.subut.kurjun.storage.factory.FileStoreFactory;


@Singleton
@MultipartConfig
class SnapUploadServlet extends HttpServletBase
{

    @Inject
    private SnapMetadataParser metadataParser;

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
    protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        if ( ServletUtils.isMultipart( req ) )
        {
            ServletUtils.setMultipartConfig( req, this.getClass() );

            Part part = req.getPart( PACKAGE_FILE_PART_NAME );
            if ( part != null && part.getSubmittedFileName() != null )
            {
                parsePackageFile( part, resp );
            }
            else
            {
                String msg = String.format( "No package file attached with name '%s'", PACKAGE_FILE_PART_NAME );
                badRequest( resp, msg );
            }
        }
        else
        {
            badRequest( resp, "Request is not a multipart request" );
        }
    }


    private void parsePackageFile( Part part, HttpServletResponse resp ) throws IOException
    {
        byte[] md5 = null;
        SnapMetadata meta;

        // define file extension based on submitted file name
        String fileName = part.getSubmittedFileName();
        String ext = CompressionType.getExtension( fileName );
        if ( ext != null )
        {
            ext = "." + ext;
        }

        FileStore fileStore = fileStoreFactory.create( HttpServer.CONTEXT );

        Path path = Files.createTempFile( "snap-uplaod", ext );
        try ( InputStream is = part.getInputStream() )
        {
            Files.copy( is, path, StandardCopyOption.REPLACE_EXISTING );

            meta = metadataParser.parse( path.toFile() );
            md5 = fileStore.put( path.toFile() );
        }
        finally
        {
            Files.delete( path );
        }

        if ( Arrays.equals( meta.getMd5Sum(), md5 ) )
        {
            PackageMetadataStore metadataStore = metadataStoreFactory.create( context );
            metadataStore.put( MetadataUtils.serializableSnapMetadata( meta ) );
            ok( resp, "Package successfully saved" );
        }
        else
        {
            fileStore.remove( md5 );
            internalServerError( resp, "Package integrity failure" );
        }
    }


}


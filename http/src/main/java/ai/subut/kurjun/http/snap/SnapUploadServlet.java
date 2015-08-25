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
import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.http.ServletUtils;
import ai.subut.kurjun.model.metadata.snap.SnapMetadata;
import ai.subut.kurjun.model.metadata.snap.SnapMetadataStore;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.snap.service.SnapMetadataParser;


@Singleton
@MultipartConfig
class SnapUploadServlet extends HttpServletBase
{
    public static final String SNAPS_PACKAGE_PART = "package";

    @Inject
    private SnapMetadataParser metadataParser;

    @Inject
    private SnapMetadataStore metadataStore;

    @Inject
    private FileStore fileStore;


    @Override
    protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        if ( ServletUtils.isMultipart( req ) )
        {
            ServletUtils.setMultipartConfig( req, this.getClass() );

            Part part = req.getPart( SNAPS_PACKAGE_PART );
            if ( part != null )
            {
                parsePackageFile( part, resp );
            }
            else
            {
                String msg = String.format( "No package file attached with name '%s'", SNAPS_PACKAGE_PART );
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

        if ( Arrays.equals( meta.getMd5(), md5 ) )
        {
            metadataStore.put( meta );
            ok( resp, "Package successfully saved" );
        }
        else
        {
            fileStore.remove( md5 );
            internalServerError( resp, "Package integrity failure" );
        }
    }


}


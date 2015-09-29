package ai.subut.kurjun.http.subutai;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.http.ServletUtils;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.storage.factory.FileStoreFactory;
import ai.subut.kurjun.subutai.service.SubutaiTemplateParser;


@Singleton
@MultipartConfig
class TemplateUploadServlet extends TemplateServletBase
{
    @Inject
    private PackageMetadataStoreFactory metadataStoreFactory;

    @Inject
    private FileStoreFactory fileStoreFactory;

    @Inject
    private SubutaiTemplateParser templateParser;


    @Override
    protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        if ( !ServletUtils.isMultipart( req ) )
        {
            badRequest( resp, "Request is not a multipart request" );
            return;
        }
        ServletUtils.setMultipartConfig( req, TemplateUploadServlet.class );

        List<String> pathItems = ServletUtils.splitPath( req.getPathInfo() );
        if ( pathItems.size() == 1 )
        {
            String repo = pathItems.get( 0 );
            KurjunContext context = getContextForType( repo );
            if ( context == null )
            {
                badRequest( resp, "Invalid template type: " + repo );
                return;
            }
            Part part = req.getPart( PACKAGE_FILE_PART_NAME );
            if ( part != null && part.getSubmittedFileName() != null )
            {
                parsePackageFile( part, resp, context );
            }
            else
            {
                String msg = String.format( "No package file attached with name '%s'", PACKAGE_FILE_PART_NAME );
                badRequest( resp, msg );
            }
        }
        else
        {
            badRequest( resp, "Specify repository to upload to" );
        }
    }


    private void parsePackageFile( Part part, HttpServletResponse resp, KurjunContext context ) throws IOException
    {
        // define file extension based on submitted file name
        String fileName = part.getSubmittedFileName();
        String ext = CompressionType.getExtension( fileName );
        if ( ext != null )
        {
            ext = "." + ext;
        }

        FileStore fileStore = fileStoreFactory.create( context );
        PackageMetadataStore metadataStore = metadataStoreFactory.create( context );

        SubutaiTemplateMetadata meta;
        Path temp = Files.createTempFile( "template-", ext );
        try ( InputStream is = part.getInputStream() )
        {
            Files.copy( is, temp, StandardCopyOption.REPLACE_EXISTING );
            meta = templateParser.parseTemplate( temp.toFile() );
            fileStore.put( temp.toFile() );
        }
        finally
        {
            Files.delete( temp );
        }

        // store meta data separately and catch exception to revert in case meta data storing fails
        // when package file is already stored
        try
        {
            metadataStore.put( MetadataUtils.serializableTemplateMetadata( meta ) );
        }
        catch ( IOException ex )
        {
            fileStore.remove( meta.getMd5Sum() );
            throw ex;
        }
    }


}


package ai.subut.kurjun.web.handler;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemStream;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.web.model.KurjunFileItem;
import ninja.uploads.FileItem;
import ninja.uploads.FileItemProvider;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;


@Singleton
public class SubutaiTemplateFileHandler implements FileItemProvider
{

    private File tmpFolder;


    @Inject
    public SubutaiTemplateFileHandler( NinjaProperties ninjaProperties )
    {
        String tempName = ninjaProperties.get( NinjaConstant.UPLOADS_TEMP_FOLDER );
        if ( tempName == null )
        {
            tempName = System.getProperty( "java.io.tmpdir" );
        }

        this.tmpFolder = new File( tempName );

        if ( !tmpFolder.exists() )
        {
            tmpFolder.mkdirs();
        }
    }


    @Override
    public FileItem create( FileItemStream item )
    {

        File tmpFile;
        byte[] md5Digest;

        try ( InputStream is = item.openStream() )
        {
            tmpFile = File.createTempFile( "nju", null, tmpFolder );
            md5Digest = copyStream( is, tmpFile.toPath() );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to create temporary uploaded file on disk", e );
        }


        // return
        final String name = item.getName();
        final byte[] md5 = md5Digest;
        final File file = tmpFile;
        final String contentType = item.getContentType();
        final FileItemHeaders headers = item.getHeaders();

        return new KurjunFileItem( name, md5, file, contentType, headers );
    }


    private byte[] copyStream( InputStream source, Path dest ) throws IOException
    {
        try ( DigestInputStream is = new DigestInputStream( source, DigestUtils.getMd5Digest() ) )
        {
            Files.copy( is, dest, StandardCopyOption.REPLACE_EXISTING );
            return is.getMessageDigest().digest();
        }
    }
}


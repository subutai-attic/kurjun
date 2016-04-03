package ai.subut.kurjun.web.model;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItemHeaders;

import ninja.uploads.FileItem;


public class KurjunFileItem implements FileItem
{

    private String name;
    private String md5;
    private File file;
    private String contentType;
    private FileItemHeaders headers;


    public KurjunFileItem( final String name, final String md5, final File file, final String contentType,
                           final FileItemHeaders headers )
    {
        this.name = name;
        this.md5 = md5;
        this.file = file;
        this.contentType = contentType;
        this.headers = headers;
    }


    //return md5 digest of the file
    public String md5()
    {
        return this.md5;
    }


    @Override
    public String getFileName()
    {
        return this.name;
    }


    @Override
    public InputStream getInputStream()
    {
        try
        {
            return new FileInputStream( file );
        }
        catch ( FileNotFoundException e )
        {
            throw new RuntimeException( "Failed to read temporary uploaded file from disk", e );
        }
    }


    @Override
    public File getFile()
    {
        return this.file;
    }


    @Override
    public String getContentType()
    {
        return this.contentType;
    }


    @Override
    public FileItemHeaders getHeaders()
    {
        return this.headers;
    }


    @Override
    public void cleanup()
    {
        try
        {
            file.delete();
        }
        catch ( Exception e )
        {
        }
    }


    public boolean move( String path )
    {
        return false;
    }

}

package ai.subut.kurjun.repo.http;


import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import ai.subut.kurjun.model.index.ChecksummedResource;
import ai.subut.kurjun.model.index.IndexPackageMetaData;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.repository.Repository;


public class HttpHandler
{

    private final Repository repository;


    public HttpHandler( Repository repository )
    {
        this.repository = repository;
    }


    public InputStream streamDistributionsFile() throws IOException
    {
        URL url = makeUrlWithPath( "/conf/distributions" );
        return url.openStream();
    }


    public InputStream streamReleaseIndexFile( String release, boolean signed ) throws IOException
    {
        String path = PathBuilder.instance().setReleaseIndexFileSigned( signed ).setRelease( release ).build();
        URL url = makeUrlWithPath( path );
        return url.openStream();
    }


    public InputStream streamReleaseIndexFile( ReleaseFile release, boolean signed ) throws IOException
    {
        return streamReleaseIndexFile( release.getCodename(), signed );
    }


    public InputStream streamChecksummedResource( ReleaseFile release, ChecksummedResource resource ) throws IOException
    {
        String path = PathBuilder.instance().setRelease( release ).setResource( resource ).build();
        URL url = makeUrlWithPath( path );
        return url.openStream();
    }


    public InputStream streamPackage( IndexPackageMetaData packageMetaData ) throws IOException
    {
        String path = PathBuilder.instance().setPackageMetaData( packageMetaData ).build();
        URL url = makeUrlWithPath( path );
        return url.openStream();
    }


    private URL makeUrlWithPath( String path ) throws IOException
    {
        Path appneded = Paths.get( repository.getUrl().getPath(), path );
        try
        {
            return repository.getUrl().toURI().resolve( appneded.toString() ).toURL();
        }
        catch ( URISyntaxException ex )
        {
            throw new IOException( "Repo URL is not strictly formatted according to RFC2396", ex );
        }
    }

}


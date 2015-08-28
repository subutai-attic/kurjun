package ai.subut.kurjun.repo;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import com.google.inject.Inject;

import ai.subut.kurjun.ar.DebAr;
import ai.subut.kurjun.ar.DefaultDebAr;
import ai.subut.kurjun.cfparser.service.ControlFileParser;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.PackageMetadata;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.repository.Protocol;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.riparser.DefaultRelease;


public class DefaultLocalRepository implements LocalRepository
{

    @Inject
    private PackageMetadataStore metadataStore;

    @Inject
    private ControlFileParser controlFileParser;

    private FileStore fileStore;

    private Set<ReleaseFile> releases = new HashSet<>();


    public DefaultLocalRepository()
    {
        // TODO: setup mechanism for repos
        DefaultRelease r = new DefaultRelease();
        r.setCodename( "trusty" );
        r.setArchitectures( Arrays.asList( Architecture.amd64, Architecture.i386 ) );
        r.setComponents( Arrays.asList( "main" ) );
        r.setDescription( "Short description of the repo" );
        r.setVersion( "12.04" );
        releases.add( r );
    }


    @Override
    public void setFileStore( FileStore fileStore )
    {
        this.fileStore = fileStore;
    }


    @Override
    public void init( String baseDirectory )
    {
    }


    @Override
    public Path getBaseDirectoryPath()
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public URL getUrl()
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public String getPath()
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public String getHostname()
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public int getPort()
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public boolean isSecure()
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public Protocol getProtocol()
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public boolean isKurjun()
    {
        return true;
    }


    @Override
    public Set<ReleaseFile> getDistributions()
    {
        return releases;
    }


    @Override
    public PackageMetadata put( InputStream is ) throws IOException
    {
        Path target = Files.createTempFile( null, null );
        Path tempDir = Files.createTempDirectory( null );

        try ( DigestInputStream wrapped = new DigestInputStream( is, DigestUtils.getMd5Digest() ) )
        {
            Files.copy( wrapped, target, StandardCopyOption.REPLACE_EXISTING );
            byte[] md5 = wrapped.getMessageDigest().digest();

            Map<String, Object> params = new HashMap<>();
            params.put( "md5sum", md5 );
            params.put( "filename", "" );

            DebAr deb = new DefaultDebAr( target.toFile(), tempDir.toFile() );
            PackageMetadata meta = controlFileParser.parse( params, deb.getControlFile() );

            // TODO: we need release and component supplied here!!!

            metadataStore.put( meta );
            fileStore.put( target.toFile() );
            return meta;
        }
        catch ( ParseException ex )
        {
            throw new IOException( "Failed to parse control file", ex );
        }
        finally
        {
            Files.delete( target );
            FileUtils.deleteDirectory( tempDir.toFile() );
        }
    }

}


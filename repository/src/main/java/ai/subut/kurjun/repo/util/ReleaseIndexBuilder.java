package ai.subut.kurjun.repo.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.index.Checksum;
import ai.subut.kurjun.model.index.ChecksummedResource;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.repo.service.PackagesIndexBuilder;
import ai.subut.kurjun.riparser.ReleaseChecksummedResource;


/**
 * This class generates a release index file for specific release. The supplied release should have checksummed
 * resources ready so that they can be included in the generated index.
 *
 */
public class ReleaseIndexBuilder
{

    private static final Logger LOGGER = LoggerFactory.getLogger( ReleaseIndexBuilder.class );

    private String line = System.lineSeparator();
    private String semi = ": ";

    private final Set<CompressionType> compressionTypes = new HashSet<>();

    private final KurjunContext context;
    private PackagesIndexBuilder packagesIndexBuilder;
    private PackagesProviderFactory packagesProviderFactory;


    @Inject
    public ReleaseIndexBuilder( AptIndexBuilderFactory indexBuilderFactory,
                                PackagesProviderFactory packagesProviderFactory,
                                @Assisted KurjunContext context )
    {
        compressionTypes.add( CompressionType.NONE );
        compressionTypes.add( CompressionType.GZIP );
        compressionTypes.add( CompressionType.BZIP2 );

        this.context = context;
        packagesIndexBuilder = indexBuilderFactory.createPackagesIndexBuilder( context );
        this.packagesProviderFactory = packagesProviderFactory;
    }


    /**
     * Adds compression type for which packages index files shall be generated. By default gzip and bzip2 indices are
     * generated.
     *
     * @param compressionType compression type to include
     */
    public void addCompressionType( CompressionType compressionType )
    {
        compressionTypes.add( compressionType );
    }


    /**
     * Builds release index file for supplied release. If release comes from a virtual repository then its packages
     * indices are build so that they are included in release index.
     *
     * @param release release for which index file is built
     * @param virtual indicated if release comes from virtual repository
     * @return
     */
    public String build( ReleaseFile release, boolean virtual )
    {
        StringBuilder sb = new StringBuilder();
        if ( release.getOrigin() != null )
        {
            sb.append( ReleaseFile.ORIGIN_FIELD ).append( semi ).append( release.getOrigin() ).append( line );
        }

        if ( release.getLabel() != null )
        {
            sb.append( ReleaseFile.LABEL_FILED ).append( semi ).append( release.getLabel() ).append( line );
        }

        if ( release.getVersion() != null )
        {
            sb.append( ReleaseFile.VERSION_FILED ).append( semi ).append( release.getVersion() ).append( line );
        }

        if ( release.getSuite() != null )
        {
            sb.append( ReleaseFile.SUITE_FILED ).append( semi ).append( release.getSuite() ).append( line );
        }

        if ( release.getCodename() != null )
        {
            sb.append( ReleaseFile.CODENAME_FILED ).append( semi ).append( release.getCodename() ).append( line );
        }

        if ( release.getComponents() != null && !release.getComponents().isEmpty() )
        {
            sb.append( ReleaseFile.COMPONENTS_FILED ).append( semi );
            for ( String c : release.getComponents() )
            {
                sb.append( c ).append( " " );
            }
            sb.append( line );
        }

        if ( release.getArchitectures() != null )
        {
            sb.append( ReleaseFile.ARCHITECTURES_FILED ).append( semi );
            for ( Architecture a : release.getArchitectures() )
            {
                sb.append( a ).append( " " );
            }
            sb.append( line );
        }

        if ( release.getDate() != null )
        {
            sb.append( ReleaseFile.DATE_FILED ).append( semi ).append( release.getDate() ).append( line );
        }

        if ( release.getDescription() != null )
        {
            sb.append( ReleaseFile.DESCRIPTION_FILED ).append( semi ).append( release.getDescription() ).append( line );
        }


        List<ChecksummedResource> indices = virtual ? preparePackagesIndexFiles( release ) : release.getIndices();

        // md5 checksums of package index files
        sb.append( ReleaseFile.MD5SUM_FILED ).append( semi ).append( line );
        appendPackageIndexFiles( sb, Checksum.MD5, indices );

        // sha1 checksums of package index files
        sb.append( ReleaseFile.SHA1_FILED ).append( semi ).append( line );
        appendPackageIndexFiles( sb, Checksum.SHA1, indices );

        // sha256 checksums of package index files
        sb.append( ReleaseFile.SHA256_FILED ).append( semi ).append( line );
        appendPackageIndexFiles( sb, Checksum.SHA256, indices );


        return sb.toString();
    }


    private List<ChecksummedResource> preparePackagesIndexFiles( ReleaseFile release )
    {
        List<String> components = release.getComponents();
        List<Architecture> architectures = release.getArchitectures();

        MessageDigest md5Digest = DigestUtils.getMd5Digest();
        MessageDigest sha1Digest = DigestUtils.getSha1Digest();
        MessageDigest sha2Digest = DigestUtils.getSha256Digest();

        List<ChecksummedResource> packagesIndices = new ArrayList<>();
        for ( String component : components )
        {
            for ( Architecture arch : architectures )
            {
                for ( CompressionType compressionType : compressionTypes )
                {
                    // make relative path
                    String relativePath = String.format( "%s/binary-%s/Packages", component, arch );
                    if ( compressionType != CompressionType.NONE )
                    {
                        relativePath += "." + compressionType.getExtension();
                    }
                    try ( ByteArrayOutputStream os = new ByteArrayOutputStream() )
                    {
                        packagesIndexBuilder.buildIndex( packagesProviderFactory.create( context, component, arch ),
                                                         os, compressionType );

                        byte[] md5 = md5Digest.digest( os.toByteArray() );
                        byte[] sha1 = sha1Digest.digest( os.toByteArray() );
                        byte[] sha2 = sha2Digest.digest( os.toByteArray() );

                        ReleaseChecksummedResource r = new ReleaseChecksummedResource( relativePath );
                        r.setSize( os.size() );
                        r.getChecksums().put( Checksum.MD5, Hex.encodeHexString( md5 ) );
                        r.getChecksums().put( Checksum.SHA1, Hex.encodeHexString( sha1 ) );
                        r.getChecksums().put( Checksum.SHA256, Hex.encodeHexString( sha2 ) );
                        packagesIndices.add( r );
                    }
                    catch ( IOException ex )
                    {
                        LOGGER.error( "Failed to build packages index for {}", relativePath, ex );
                    }
                }
            }
        }
        return packagesIndices;
    }


    private void appendPackageIndexFiles( StringBuilder sb, Checksum checksumType, List<ChecksummedResource> indices )
    {
        for ( ChecksummedResource index : indices )
        {
            byte[] checksum = index.getChecksum( checksumType );
            if ( checksum != null )
            {
                // indent each line by one space
                sb.append( " " ).append( Hex.encodeHexString( checksum ) ).append( " " );
                sb.append( String.format( "%16d", index.getSize() ) ).append( " " );
                sb.append( index.getRelativePath() );
                sb.append( line );
            }
        }
    }
}


package ai.subut.kurjun.index.impl;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Objects;

import org.vafer.jdeb.debian.BinaryPackageControlFile;
import org.vafer.jdeb.debian.ControlFile;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import ai.subut.kurjun.index.PackagesIndexBuilder;
import ai.subut.kurjun.model.index.IndexPackageMetaData;
import ai.subut.kurjun.model.metadata.Dependency;
import ai.subut.kurjun.model.metadata.PackageMetadata;
import ai.subut.kurjun.model.metadata.PackageMetadataListing;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.storage.FileStore;


public class PackagesIndexBuilderImpl implements PackagesIndexBuilder
{

    private PackageMetadataStore metadataStore;
    private FileStore fileStore;


    public void setMetadataStore( PackageMetadataStore metadataStore )
    {
        this.metadataStore = metadataStore;
    }


    public void setFileStore( FileStore fileStore )
    {
        this.fileStore = fileStore;
    }


    @Override
    public void buildIndex( OutputStream os ) throws IOException
    {
        Objects.requireNonNull( fileStore, "File store" );
        Objects.requireNonNull( metadataStore, "Package metadata store" );

        Charset utf8 = StandardCharsets.UTF_8;
        byte[] newLineBytes = System.lineSeparator().getBytes( utf8 );

        PackageMetadataListing list = metadataStore.list();
        for ( PackageMetadata pm : list.getPackageMetadata() )
        {
            String s = formatPackageMetadata( pm );
            os.write( s.getBytes( utf8 ) );
            os.write( newLineBytes );
        }
        while ( list.isTruncated() )
        {
            list = metadataStore.listNextBatch( list );
            for ( PackageMetadata pm : list.getPackageMetadata() )
            {
                String s = formatPackageMetadata( pm );
                os.write( s.getBytes( utf8 ) );
                os.write( newLineBytes );
            }
        }
    }


    private String formatPackageMetadata( PackageMetadata meta ) throws IOException
    {
        if ( !fileStore.contains( meta.getMd5Sum() ) )
        {
            throw new IllegalStateException( "Corresponding file not found in file store" );
        }

        BinaryPackageControlFile cf = new BinaryPackageControlFile();

        // === mandatory and recommended fields ===
        cf.set( PackageMetadata.PACKAGE_FIELD, meta.getPackage() );
        cf.set( PackageMetadata.VERSION_FIELD, meta.getVersion() );
        cf.set( PackageMetadata.SECTION_FIELD, meta.getSection() );
        cf.set( PackageMetadata.PRIORITY_FIELD, meta.getPriority().toString() );
        cf.set( PackageMetadata.ARCHITECTURE_FIELD, meta.getArchitecture().toString() );
        cf.set( PackageMetadata.MAINTAINER_FIELD, meta.getMaintainer() );
        cf.set( PackageMetadata.DESCRIPTION_FIELD, meta.getDescription() );

        // === optional fields ===
        if ( meta.getInstalledSize() > 0 )
        {
            cf.set( PackageMetadata.INSTALLED_SIZE_FIELD, Integer.toString( meta.getInstalledSize() ) );
        }
        if ( meta.getHomepage() != null )
        {
            cf.set( PackageMetadata.HOMEPAGE_FIELD, meta.getHomepage().toString() );
        }
        includeDependencyFields( cf, meta );

        // packages index specific fields
        long totalBytes = 0L;
        MessageDigest sha1 = DigestUtils.getSha1Digest();
        MessageDigest sha2 = DigestUtils.getSha256Digest();
        try ( InputStream is = fileStore.get( meta.getMd5Sum() ) )
        {
            int n;
            byte[] buf = new byte[1024 * 8];
            while ( ( n = is.read( buf ) ) > 0 )
            {
                totalBytes += n;
                sha1.update( buf, 0, n );
                sha2.update( buf, 0, n );
            }
        }
        cf.set( IndexPackageMetaData.FILENAME_FIELD, meta.getFilename() );
        cf.set( IndexPackageMetaData.SIZE_FIELD, Long.toString( totalBytes ) );
        cf.set( IndexPackageMetaData.MD5SUM_FIELD, Hex.encodeHexString( meta.getMd5Sum() ) );
        cf.set( IndexPackageMetaData.SHA1_FIELD, Hex.encodeHexString( sha1.digest() ) );
        cf.set( IndexPackageMetaData.SHA256_FIELD, Hex.encodeHexString( sha2.digest() ) );
        // TODO: description md5 does NOT match
//        cf.set( IndexPackageMetaData.DESCRIPTION_MD5_FIELD, DigestUtils.md5Hex( meta.getDescription() ) );

        if ( cf.isValid() )
        {
            return cf.toString( PackageIndexFieldsParser.FIELDS );
        }
        else
        {
            throw new IllegalArgumentException( "Given metadata has not complete info" );
        }
    }


    private void includeDependencyFields( ControlFile cf, PackageMetadata meta )
    {
        if ( meta.getDependencies() != null )
        {
            cf.set( PackageMetadata.DEPENDS_FIELD, dumpDependencies( meta.getDependencies() ) );
        }
        if ( meta.getRecommends() != null )
        {
            cf.set( PackageMetadata.RECOMMENDS_FIELD, dumpDependencies( meta.getRecommends() ) );
        }
        if ( meta.getSuggests() != null )
        {
            cf.set( PackageMetadata.SUGGESTS_FIELD, dumpDependencies( meta.getSuggests() ) );
        }
        if ( meta.getEnhances() != null )
        {
            cf.set( PackageMetadata.ENHANCES_FIELD, dumpDependencies( meta.getEnhances() ) );
        }
        if ( meta.getPreDepends() != null )
        {
            cf.set( PackageMetadata.PRE_DEPENDS_FIELD, dumpDependencies( meta.getPreDepends() ) );
        }
        if ( meta.getConflicts() != null )
        {
            cf.set( PackageMetadata.CONFLICTS_FIELD, dumpDependencies( meta.getConflicts() ) );
        }
        if ( meta.getBreaks() != null )
        {
            cf.set( PackageMetadata.BREAKS_FIELD, dumpDependencies( meta.getBreaks() ) );
        }
        if ( meta.getReplaces() != null )
        {
            cf.set( PackageMetadata.REPLACES_FIELD, dumpDependencies( meta.getReplaces() ) );
        }
        if ( meta.getProvides() != null )
        {
            cf.set( PackageMetadata.PROVIDES_FIELD, dumpDependencies( meta.getProvides() ) );
        }
    }


    /**
     * Dumps dependencies to string. See {@link PackagesIndexBuilder#dumpDependency(Dependency)} for more info.
     *
     * @param dependencies
     * @return
     */
    private String dumpDependencies( List dependencies )
    {
        StringBuilder sb = new StringBuilder();
        for ( Object dep : dependencies )
        {
            if ( sb.length() > 0 )
            {
                sb.append( ", " );
            }
            sb.append( dep instanceof Dependency ? dumpDependency( ( Dependency ) dep ) : dep );
        }
        return sb.toString();
    }


    /**
     * Dumps dependency to string according to syntax of relationship fields described in
     * <a href="https://www.debian.org/doc/debian-policy/ch-relationships.html">Section 7.1 of the Debian Policy</a>
     *
     * @param dependency
     * @return
     */
    private String dumpDependency( Dependency dependency )
    {
        StringBuilder sb = new StringBuilder( dependency.getPackage() );
        if ( dependency.getVersion() != null )
        {
            sb.append( " (" ).append( dependency.getDependencyOperator().getSymbol() );
            sb.append( " " ).append( dependency.getVersion() ).append( ")" );
        }
        if ( dependency.getAlternatives() != null )
        {
            for ( Dependency alt : dependency.getAlternatives() )
            {
                sb.append( " | " ).append( dumpDependency( alt ) );
            }
        }
        return sb.toString();
    }

}


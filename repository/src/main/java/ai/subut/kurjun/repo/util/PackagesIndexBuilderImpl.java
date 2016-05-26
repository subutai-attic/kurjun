package ai.subut.kurjun.repo.util;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vafer.jdeb.debian.BinaryPackageControlFile;
import org.vafer.jdeb.debian.ControlFile;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.index.PackageIndexFieldsParser;
import ai.subut.kurjun.metadata.common.apt.DefaultIndexPackageMetaData;
import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.model.index.IndexPackageMetaData;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.apt.Dependency;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;
import ai.subut.kurjun.repo.service.PackageFilenameBuilder;
import ai.subut.kurjun.repo.service.PackagesIndexBuilder;


class PackagesIndexBuilderImpl implements PackagesIndexBuilder
{

    private static final Logger LOGGER = LoggerFactory.getLogger( PackagesIndexBuilderImpl.class );

    @Inject
    PackageFilenameBuilder filenameBuilder;

    @Inject
    Gson gson;


    @Override
    public void buildIndex( PackagesProvider provider, OutputStream out, CompressionType compressionType ) throws IOException
    {
        List<SerializableMetadata> items = provider.getPackages();
        try ( OutputStream os = wrapStream( out, compressionType ) )
        {
            for ( SerializableMetadata item : items )
            {
                DefaultPackageMetadata meta = deserializeMetadata( item );
                String s = formatPackageMetadata( meta );
                writeString( s, os );
            }
        }
    }


    private DefaultPackageMetadata deserializeMetadata( SerializableMetadata meta )
    {
        DefaultPackageMetadata res = gson.fromJson( meta.serialize(), DefaultPackageMetadata.class );
        try
        {
            DefaultIndexPackageMetaData ipm = gson.fromJson( meta.serialize(), DefaultIndexPackageMetaData.class );
            if ( ipm.getSHA1() != null )
            {
                res.getExtra().put( IndexPackageMetaData.SHA1_FIELD, Hex.encodeHexString( ipm.getSHA1() ) );
            }
            if ( ipm.getSHA256() != null )
            {
                res.getExtra().put( IndexPackageMetaData.SHA256_FIELD, Hex.encodeHexString( ipm.getSHA256() ) );
            }
            if ( ipm.getSize() > 0 )
            {
                res.getExtra().put( IndexPackageMetaData.SIZE_FIELD, Long.toString( ipm.getSize() ) );
            }
        }
        catch ( JsonSyntaxException ex )
        {
            LOGGER.error( "Meta data is not index package metadata. Using it as plain package metadata.", ex );
        }
        return res;
    }


    private void writeString( String s, OutputStream sink ) throws IOException
    {
        Charset utf8 = StandardCharsets.UTF_8;
        sink.write( s.getBytes( utf8 ) );
        sink.write( System.lineSeparator().getBytes( utf8 ) );
    }


    private OutputStream wrapStream( OutputStream os, CompressionType compressionType ) throws IOException
    {
        OutputStream wrapped = null;
        switch ( compressionType )
        {
            case NONE:
                wrapped = os;
                break;
            case GZIP:
                wrapped = new GzipCompressorOutputStream( os );
                break;
            case BZIP2:
                wrapped = new BZip2CompressorOutputStream( os );
                break;
            case XZ:
                wrapped = new XZCompressorOutputStream( os );
                break;
            case LZMA:
                // TODO: no lzma output stream impl in common-compress
                wrapped = os;
                break;
            default:
                throw new AssertionError( compressionType.name() );
        }
        return wrapped;
    }


    private String formatPackageMetadata( DefaultPackageMetadata meta ) throws IOException
    {

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
        if ( meta.getSource() != null )
        {
            cf.set( PackageMetadata.SOURCE_FIELD, meta.getSource() );
        }
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
        cf.set( IndexPackageMetaData.FILENAME_FIELD, filenameBuilder.makeFilename( meta ) );
        cf.set( IndexPackageMetaData.MD5SUM_FIELD, meta.getMd5Sum() );
        includeExtraField( cf, meta, IndexPackageMetaData.SHA1_FIELD );
        includeExtraField( cf, meta, IndexPackageMetaData.SHA256_FIELD );
        includeExtraField( cf, meta, IndexPackageMetaData.SIZE_FIELD );
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


    private void includeExtraField( ControlFile cf, DefaultPackageMetadata meta, String field )
    {
        if ( meta.getExtra().containsKey( field ) )
        {
            cf.set( field, meta.getExtra().get( field ) );
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

            if ( dep instanceof Dependency )
            {
                sb.append( dumpDependency( ( Dependency ) dep ) );
            }
            else
            {
                sb.append( dep );
            }
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
        Objects.requireNonNull( dependency.getPackage(), "dependency name" );

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


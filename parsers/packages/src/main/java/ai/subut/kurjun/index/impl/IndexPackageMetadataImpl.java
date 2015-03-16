package ai.subut.kurjun.index.impl;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vafer.jdeb.debian.ControlFile;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import ai.subut.kurjun.cfparser.impl.DependencyParser;
import ai.subut.kurjun.model.index.IndexPackageMetaData;
import ai.subut.kurjun.model.index.TagItem;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.Dependency;
import ai.subut.kurjun.model.metadata.Priority;


public class IndexPackageMetadataImpl implements IndexPackageMetaData
{

    private static final Logger LOGGER = LoggerFactory.getLogger( IndexPackageMetadataImpl.class );

    private ControlFile controlFile;
    private DependencyParser depParser = new DependencyParser();


    public IndexPackageMetadataImpl( ControlFile controlFile )
    {
        this.controlFile = controlFile;
    }


    @Override
    public byte[] getSHA1()
    {
        String sha1 = controlFile.get( IndexPackageMetaData.SHA1_FIELD );
        if ( sha1 != null )
        {
            try
            {
                return Hex.decodeHex( sha1.toCharArray() );
            }
            catch ( DecoderException ex )
            {
                LOGGER.error( "Invalid SHA1 checksum", ex );
            }
        }
        return null;
    }


    @Override
    public byte[] getSHA256()
    {
        String sha256 = controlFile.get( IndexPackageMetaData.SHA256_FIELD );
        if ( sha256 != null )
        {
            try
            {
                return Hex.decodeHex( sha256.toCharArray() );
            }
            catch ( DecoderException ex )
            {
                LOGGER.error( "Invalid SHA256 checksum", ex );
            }
        }
        return null;
    }


    @Override
    public byte[] getMd5Sum()
    {
        String md5 = controlFile.get( IndexPackageMetaData.MD5SUM_FIELD );
        if ( md5 != null )
        {
            try
            {
                return Hex.decodeHex( md5.toCharArray() );
            }
            catch ( DecoderException ex )
            {
                LOGGER.error( "Invalid MD5 checksum", ex );
            }
        }
        return null;
    }


    @Override
    public long getSize()
    {
        String size = controlFile.get( IndexPackageMetaData.SIZE_FIELD );
        return size != null && !size.isEmpty() ? Long.parseLong( size ) : 0;
    }


    @Override
    public String getFilename()
    {
        return controlFile.get( IndexPackageMetaData.FILENAME_FIELD );
    }


    @Override
    public byte[] getDescriptionMd5()
    {
        String md5 = controlFile.get( IndexPackageMetaData.DESCRIPTION_MD5_FIELD );
        if ( md5 != null )
        {
            try
            {
                return Hex.decodeHex( md5.toCharArray() );
            }
            catch ( DecoderException ex )
            {
                LOGGER.error( "Invalid MD5 checksum for description", ex );
            }
        }
        return null;
    }


    @Override
    public List<TagItem> getTag()
    {
        // TODO:
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public String getPackage()
    {
        return controlFile.get( IndexPackageMetaData.PACKAGE_FIELD );
    }


    @Override
    public String getVersion()
    {
        return controlFile.get( IndexPackageMetaData.VERSION_FIELD );
    }


    @Override
    public String getMaintainer()
    {
        return controlFile.get( IndexPackageMetaData.MAINTAINER_FIELD );
    }


    @Override
    public Architecture getArchitecture()
    {
        String arch = controlFile.get( IndexPackageMetaData.ARCHITECTURE_FIELD );
        return Architecture.valueOf( arch );
    }


    @Override
    public int getInstalledSize()
    {
        String size = controlFile.get( IndexPackageMetaData.INSTALLED_SIZE_FIELD );
        return size != null && !size.isEmpty() ? Integer.parseInt( size ) : 0;
    }


    @Override
    public List<Dependency> getDependencies()
    {
        String s = controlFile.get( IndexPackageMetaData.DEPENDS_FIELD );
        return depParser.getDependencies( s );
    }


    @Override
    public List<Dependency> getRecommends()
    {
        String s = controlFile.get( IndexPackageMetaData.RECOMMENDS_FIELD );
        return depParser.getDependencies( s );
    }


    @Override
    public List<Dependency> getSuggests()
    {
        String s = controlFile.get( IndexPackageMetaData.SUGGESTS_FIELD );
        return depParser.getDependencies( s );
    }


    @Override
    public List<Dependency> getEnhances()
    {
        String s = controlFile.get( IndexPackageMetaData.ENHANCES_FIELD );
        return depParser.getDependencies( s );
    }


    @Override
    public List<Dependency> getPreDepends()
    {
        String s = controlFile.get( IndexPackageMetaData.PRE_DEPENDS_FIELD );
        return depParser.getDependencies( s );
    }


    @Override
    public List<Dependency> getConflicts()
    {
        String s = controlFile.get( IndexPackageMetaData.CONFLICTS_FIELD );
        return depParser.getDependencies( s );
    }


    @Override
    public List<Dependency> getBreaks()
    {
        String s = controlFile.get( IndexPackageMetaData.BREAKS_FIELD );
        return depParser.getDependencies( s );
    }


    @Override
    public List<Dependency> getReplaces()
    {
        String s = controlFile.get( IndexPackageMetaData.REPLACES_FIELD );
        return depParser.getDependencies( s );
    }


    @Override
    public List<String> getProvides()
    {
        String s = controlFile.get( IndexPackageMetaData.PROVIDES_FIELD );
        return s != null ? Arrays.asList( s.split( "," ) ) : null;
    }


    @Override
    public String getSection()
    {
        return controlFile.get( IndexPackageMetaData.SECTION_FIELD );
    }


    @Override
    public Priority getPriority()
    {
        String p = controlFile.get( IndexPackageMetaData.PRIORITY_FIELD );
        return Priority.valueOf( p );
    }


    @Override
    public URL getHomepage()
    {
        String home = controlFile.get( IndexPackageMetaData.HOMEPAGE_FIELD );
        if ( home != null )
        {
            try
            {
                return new URL( home );
            }
            catch ( MalformedURLException ex )
            {
                LOGGER.error( "Invalid homepage URL", ex );
            }
        }
        return null;
    }


    @Override
    public String getDescription()
    {
        return controlFile.get( IndexPackageMetaData.DESCRIPTION_FIELD );
    }


    @Override
    public String toString()
    {
        return controlFile.toString( PackageIndexFieldsParser.FIELDS );
    }
}


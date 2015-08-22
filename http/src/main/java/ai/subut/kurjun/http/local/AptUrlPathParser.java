package ai.subut.kurjun.http.local;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.model.metadata.Architecture;


class AptUrlPathParser
{

    private static final Pattern packageIndexFilePattern;
    private static final Pattern componentReleaseFilePattern;
    private static final Pattern releaseIndexFilePattern;


    static
    {
        // pattern groups:
        // (1) release name like "truty"
        // (2) component name like "main"
        // (3) architecture like "amd64"
        // (4) packages index extension, usually correspond to compression type like "gz", "bz2"
        packageIndexFilePattern = Pattern.compile( "/dists/(\\w+)/(\\w+)/binary-(\\w+)/Packages(?:\\.\\w+)?" );

        // this pattern is similar to the above one except it refers to Release file instead of Packages file
        componentReleaseFilePattern = Pattern.compile( "/dists/(\\w+)/(\\w+)/binary-(\\w+)/Release" );

        // (1) release name like "trusty"
        // (2) "In" prefix like in "InRelease". if this group is not null then clear signed release index file is referred
        // (3) ".gpg" extension meaning that release file signature is referred
        releaseIndexFilePattern = Pattern.compile( "/dists/(\\w+)/(In)?Release(\\.gpg)?" );
    }

    private String release;
    private String component;
    private Architecture architecture;
    private CompressionType compressionType = CompressionType.NONE;

    private boolean packagesIndexFile;
    private boolean releaseIndexFile;
    private boolean clearSignedReleaseIndexFile;
    private boolean releaseIndexFileSignature;


    public AptUrlPathParser( String path )
    {
        parse( path );
    }


    public String getRelease()
    {
        return release;
    }


    public String getComponent()
    {
        return component;
    }


    public Architecture getArchitecture()
    {
        return architecture;
    }


    public CompressionType getCompressionType()
    {
        return compressionType;
    }


    public boolean isPackagesIndexFile()
    {
        return packagesIndexFile;
    }


    public boolean isReleaseIndexFile()
    {
        return releaseIndexFile;
    }


    public boolean isClearSignedReleaseIndexFile()
    {
        return clearSignedReleaseIndexFile;
    }


    public boolean isReleaseIndexFileSignature()
    {
        return releaseIndexFileSignature;
    }


    private void parse( String path )
    {
        parsePackagesIndexFilePath( path );
    }


    private void parsePackagesIndexFilePath( String path )
    {
        Matcher matcher = packageIndexFilePattern.matcher( path );
        if ( matcher.matches() )
        {
            packagesIndexFile = true;
            release = matcher.group( 1 );
            component = matcher.group( 2 );
            architecture = Architecture.getByValue( matcher.group( 3 ) );
            String ext = matcher.group( 4 );
            if ( ext != null )
            {
                compressionType = CompressionType.getByExtension( ext );
            }
        }
        else
        {
            parseComponentReleaseFilePath( path );
        }
    }


    private void parseComponentReleaseFilePath( String path )
    {
        Matcher matcher = componentReleaseFilePattern.matcher( path );
        if ( matcher.matches() )
        {
            release = matcher.group( 1 );
            component = matcher.group( 2 );
            architecture = Architecture.getByValue( matcher.group( 3 ) );
        }
        else
        {
            parseReleaseIndexFilePath( path );
        }
    }


    private void parseReleaseIndexFilePath( String path )
    {
        Matcher matcher = releaseIndexFilePattern.matcher( path );
        if ( matcher.matches() )
        {
            release = matcher.group( 1 );
            if ( matcher.group( 2 ) != null )
            {
                clearSignedReleaseIndexFile = true;
            }
            else if ( matcher.group( 3 ) != null )
            {
                releaseIndexFileSignature = true;
            }
            else
            {
                releaseIndexFile = true;
            }
        }
    }


}


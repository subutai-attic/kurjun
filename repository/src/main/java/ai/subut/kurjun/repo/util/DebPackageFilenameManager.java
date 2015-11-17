package ai.subut.kurjun.repo.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;
import ai.subut.kurjun.repo.service.PackageFilenameBuilder;
import ai.subut.kurjun.repo.service.PackageFilenameParser;


/**
 * Builder and parser of Filename field of a Debian package meta data.
 *
 */
public class DebPackageFilenameManager implements PackageFilenameBuilder, PackageFilenameParser
{

    /**
     * Filename pattern groups: (1) component name, (2) package or source, (3) package file name.
     * <p>
     * Third group is a package file name which consists of package name, version, and architecture delimited by an
     * underscore.
     */
    private static final Pattern FILENAME_FIELD_PATTERN
            = Pattern.compile( "/?pool/(\\w+)/[a-z]/([a-z0-9+-\\.]+)/(.+)" );


    @Override
    public String makeFilename( PackageMetadata metadata )
    {
        // start by making path
        StringBuilder sb = new StringBuilder( "pool/" );
        sb.append( metadata.getComponent() ).append( "/" );

        String s = metadata.getSource() != null ? metadata.getSource() : metadata.getPackage();
        sb.append( s.substring( 0, 1 ) ).append( "/" ).append( s ).append( "/" );
        // finally append package file name
        sb.append( makePackageFilename( metadata ) );
        return sb.toString();
    }


    @Override
    public String makePackageFilename( PackageMetadata metadata )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( metadata.getPackage() ).append( "_" );
        sb.append( metadata.getVersion() ).append( "_" );
        sb.append( metadata.getArchitecture().toString() );
        sb.append( ".deb" );
        return sb.toString();
    }


    @Override
    public String getComponent( String filename )
    {
        Matcher matcher = FILENAME_FIELD_PATTERN.matcher( filename );
        if ( matcher.matches() )
        {
            return matcher.group( 1 );
        }
        return null;
    }


    @Override
    public String getPackageFromFilename( String filename )
    {
        return getPartOfPackageFile( 0, filename );
    }


    @Override
    public String getVersionFromFilename( String filename )
    {
        return getPartOfPackageFile( 1, filename );
    }


    @Override
    public Architecture getArchFromFilename( String filename )
    {
        String arch = getPartOfPackageFile( 2, filename );
        return Architecture.getByValue( arch );
    }


    private String getPartOfPackageFile( int partIndex, String filename )
    {
        assert partIndex < 3;

        Matcher matcher = FILENAME_FIELD_PATTERN.matcher( filename );
        if ( matcher.matches() )
        {
            String file = matcher.group( 3 );
            String[] arr = file.split( "_" );
            if ( arr.length == 3 )
            {
                return arr[partIndex];
            }
        }
        return null;
    }


}


package ai.subut.kurjun.common.utils;



import java.util.regex.Pattern;

import ai.subut.kurjun.model.metadata.Metadata;


/**
 * Utility methods for snap packages.
 *
 */
public class SnapUtils
{

    private static final Pattern namePattern = Pattern.compile( "[a-z0-9][a-z0-9+-]+" );
    private static final Pattern versionPattern = Pattern.compile( "[a-zA-Z0-9\\.+~-]+" );


    private SnapUtils()
    {
    }


    /**
     * Checks if a given name is a valid snap package name.
     *
     * @param name
     * @return
     */
    public static boolean isValidName( String name )
    {
        return namePattern.matcher( name ).matches();
    }


    /**
     * Checks if a given version string is a valid snap version.
     *
     * @param version
     * @return
     */
    public static boolean isValidVersion( String version )
    {
        return versionPattern.matcher( version ).matches();
    }


    /**
     * Makes a meaningful file name for supplied snap metadata. Usually it contains snap package name and version.
     *
     * @param metadata snap metadata to make name for
     * @return file name for metadata
     */
    public static String makeFileName( Metadata metadata )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( metadata.getName() ).append( "-" ).append( metadata.getVersion() );
        sb.append( ".snap" );
        return sb.toString();
    }
}


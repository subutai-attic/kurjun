package ai.subut.kurjun.model.metadata.snap;


import java.util.regex.Pattern;


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
}


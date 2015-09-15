package ai.subut.kurjun.common.utils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Debian policy related utility methods.
 *
 */
public class DebUtils
{

    public static final Pattern DEB_VERSION_PATTERN;


    static
    {
        /**
         * Simplified pattern for Debian package versions as defined in
         * https://www.debian.org/doc/debian-policy/ch-controlfields.html#s-f-Version
         *
         * The format is: ([epoch:])(upstream_version[-debian_revision])
         */
        DEB_VERSION_PATTERN = Pattern.compile( "([0-9]+:)?([0-9][A-Za-z0-9\\.+-:~]+)" );
    }


    private DebUtils()
    {
        // not to be constructed
    }


    /**
     * Checks if supplied version string conforms to Debian version policy.
     *
     * @param version version string to check
     * @return
     */
    public static boolean isValidVersion( String version )
    {
        return DEB_VERSION_PATTERN.matcher( version ).matches();
    }


    /**
     * Gets epoch component of the supplied version. If version string does not contain epoch component then zero is
     * returned as per Debian version policy at
     * https://www.debian.org/doc/debian-policy/ch-controlfields.html#s-f-Version.
     *
     * @param version version string to extract epoch component from
     * @return epoch component if found; {@code "0"} otherwise
     */
    public static String getVersionEpoch( String version )
    {
        Matcher matcher = DEB_VERSION_PATTERN.matcher( version );
        if ( !matcher.matches() )
        {
            throw new IllegalArgumentException( "Invalid version argument" );
        }
        String epoch = matcher.group( 1 );
        if ( epoch != null )
        {
            // trim trailing colon
            return epoch.substring( 0, epoch.length() - 1 );
        }
        return String.valueOf( 0 );
    }


    /**
     * Gets version without epoch component. So the returned string is contains upstream_version and debian_version
     * components.
     *
     * @param version
     * @return
     */
    public static String getVersionWithoutEpoch( String version )
    {
        Matcher matcher = DEB_VERSION_PATTERN.matcher( version );
        if ( !matcher.matches() )
        {
            throw new IllegalArgumentException( "Invalid version argument" );
        }
        return matcher.group( 2 );
    }

}


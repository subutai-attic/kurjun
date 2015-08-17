package ai.subut.kurjun.http;


/**
 * Utility methods to work with servlets.
 *
 */
public class ServletUtils
{

    private ServletUtils()
    {
        // no need to construct utility class
    }


    /**
     * Prepends a slash to the beginning of the given path.
     *
     * @param path
     * @return
     */
    public static String ensureLeadingSlash( String path )
    {
        if ( path.startsWith( "/" ) )
        {
            return path;
        }
        return "/" + path;
    }


    /**
     * Removes that trailing slash from the given path.
     *
     * @param path
     * @return
     */
    public static String removeTrailingSlash( String path )
    {
        if ( path.endsWith( "/" ) )
        {
            return path.substring( 0, path.length() - 1 );
        }
        return path;
    }
}


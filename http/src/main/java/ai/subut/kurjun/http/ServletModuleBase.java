package ai.subut.kurjun.http;


import com.google.inject.servlet.ServletModule;


/**
 * Base servlet module for Guice.
 *
 */
public abstract class ServletModuleBase extends ServletModule
{

    private String servletPath = "";


    /**
     * Gets servlet path that will be served. Defaults to empty string.
     *
     * @return
     */
    public String getServletPath()
    {
        return servletPath;
    }


    /**
     * Sets servlet path that will be served.
     *
     * @param servletPath
     */
    public void setServletPath( String servletPath )
    {
        servletPath = ServletUtils.ensureLeadingSlash( servletPath );
        servletPath = ServletUtils.removeTrailingSlash( servletPath );

        this.servletPath = servletPath;
    }

}


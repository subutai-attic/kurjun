package ai.subut.kurjun.http.local;


import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.servlet.ServletModule;

import ai.subut.kurjun.http.ServletUtils;


/**
 * Guice servlet module that makes requests be handled by {@link DefaultServlet}. This is a good choice to serve
 * contents of some defined directory like from a base directory of a non-virtual local apt repository. To make use of
 * that default servlet, a base resource should be set to a base directory of a repository. This is usually done be
 * {@link ServletContextHandler#setBaseResource(org.eclipse.jetty.util.resource.Resource)} method.
 * <p>
 * One unhandy side of this is that we can not set base resource from within the servlets or from this module. This
 * makes this module unusable.
 * <p>
 */
public class LocalAptRepoServletModuleDefault extends ServletModule
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


    @Override
    protected void configureServlets()
    {

        // servlet bindings are required to be annotated with @Singleton.
        // if can not annotate classes directly, do it here
        bind( DefaultServlet.class ).asEagerSingleton();

        serve( servletPath + "/*" ).with( DefaultServlet.class );

    }

}


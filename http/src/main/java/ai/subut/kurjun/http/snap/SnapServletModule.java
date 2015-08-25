package ai.subut.kurjun.http.snap;


import ai.subut.kurjun.http.ServletModuleBase;


/**
 * Guice servlet module for snap repository.
 *
 */
public class SnapServletModule extends ServletModuleBase
{

    @Override
    protected void configureServlets()
    {
        serve( getServletPath() + "/upload" ).with( SnapUploadServlet.class );
        serve( getServletPath() + "*" ).with( SnapServlet.class );
    }

}


package ai.subut.kurjun.http.apt;


import ai.subut.kurjun.http.ServletModuleBase;


/**
 * Guice servlet module for local Kurjun apt repository.
 *
 */
public class AptRepoServletModule extends ServletModuleBase
{

    @Override
    protected void configureServlets()
    {

        serve( getServletPath() + "/uni/*" ).with( AptUniServlet.class );

        serve( getServletPath() + "/upload" ).with( AptRepoUploadServlet.class );
        serve( getServletPath() + "/pool/*" ).with( AptPoolServlet.class );
        serve( getServletPath() + "/*" ).with( AptRepoServlet.class );

    }


}


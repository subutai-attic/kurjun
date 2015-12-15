package ai.subut.kurjun.http.apt;


import ai.subut.kurjun.http.ServletModuleBase;


/**
 * Guice servlet module for local non-virtual apt repository.
 *
 */
public class LocalAptRepoServletModule extends ServletModuleBase
{

    @Override
    protected void configureServlets()
    {

        serve( getServletPath() + "/*" ).with( LocalAptRepoServlet.class );

    }


}


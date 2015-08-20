package ai.subut.kurjun.http.local;


import ai.subut.kurjun.http.ServletModuleBase;


/**
 * Guice servlet module for local Kurjun apt repository.
 *
 */
public class KurjunAptRepoServletModule extends ServletModuleBase
{

    @Override
    protected void configureServlets()
    {

        serve( getServletPath() + "/pool/*" ).with( KurjunAptPoolServlet.class );
        serve( getServletPath() + "/*" ).with( KurjunAptRepoServlet.class );

    }


}


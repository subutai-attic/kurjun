package ai.subut.kurjun.http.subutai;


import ai.subut.kurjun.http.ServletModuleBase;


/**
 * Guice servlet module for Subutai templates repository.
 *
 */
public class TemplateServletModule extends ServletModuleBase
{

    @Override
    protected void configureServlets()
    {
        serve( getServletPath() + "/upload/*" ).with( TemplateUploadServlet.class );
        serve( getServletPath() + "/*" ).with( TemplateServlet.class );
    }


}


package ai.subut.kurjun.http.subutai;


import java.util.Iterator;
import java.util.Set;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.http.HttpServer;
import ai.subut.kurjun.http.HttpServletBase;


/**
 * Abstract base class for template servlets. This class has methods to get contexts for templates type specified in
 * request path.
 *
 */
abstract class TemplateServletBase extends HttpServletBase
{
    private String currentTemplateType;


    @Override
    protected KurjunContext getContext()
    {
        return getContextForType( currentTemplateType );
    }


    public void setCurrentTemplateType( String currentTemplateType )
    {
        this.currentTemplateType = currentTemplateType;
    }


    protected KurjunContext getContextForType( String type )
    {
        Set<KurjunContext> set = HttpServer.TEMPLATE_CONTEXTS;
        for ( Iterator<KurjunContext> it = set.iterator(); it.hasNext(); )
        {
            KurjunContext c = it.next();
            if ( c.getName().equals( type ) )
            {
                return c;
            }
        }
        return null;
    }

}


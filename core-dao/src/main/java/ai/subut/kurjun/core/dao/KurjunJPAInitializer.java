package ai.subut.kurjun.core.dao;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.PersistService;


/**
 *
 */
@Singleton
public class KurjunJPAInitializer
{
    @Inject
    public KurjunJPAInitializer( final PersistService service )
    {
        service.start();
    }

}

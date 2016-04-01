package ai.subut.kurjun.core.dao.api;




import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;


/**
 * Guice module to initialize Kurjun DAO bindings.
 *
 */
public class KurjunDAOModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        //****************
        install( new JpaPersistModule( "PU-KURJUN" ) );
        //****************
    }

}


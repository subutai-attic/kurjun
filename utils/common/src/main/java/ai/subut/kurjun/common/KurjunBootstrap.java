package ai.subut.kurjun.common;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;



/**
 * Class to bootstrap Kurjun modules in Guice context.
 *
 */
public final class KurjunBootstrap
{

    private final List<Module> modules = new ArrayList<>();
    private Injector injector;


    public KurjunBootstrap()
    {
        modules.add( new KurjunPropertiesModule() );
    }


    /**
     * Gets Guice injector if it is created. Use {@link KurjunBootstrap#boot()} to boot up.
     *
     * @return Guice injector
     */
    public Injector getInjector()
    {
        return injector;
    }


    public void addModule( Module... modules )
    {
        if ( modules != null )
        {
            this.modules.addAll( Arrays.asList( modules ) );
        }
    }


    /**
     * Bootstrap Guice with provided modules.
     *
     */
    public void boot()
    {
        injector = Guice.createInjector( modules );
    }
}


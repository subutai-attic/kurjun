package ai.subut.kurjun.common;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Module;

import sun.security.pkcs11.Secmod;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class KurjunBootstrapTest
{
    private KurjunBootstrap kurjunBootstrap;

    @Mock
    Module module;


    @Before
    public void setUp() throws Exception
    {
        kurjunBootstrap = new KurjunBootstrap();
    }


    @Test
    public void getInjector() throws Exception
    {
        kurjunBootstrap.getInjector();
    }


    @Test
    public void addModule() throws Exception
    {
        kurjunBootstrap.addModule( module );
    }


    @Test
    public void boot() throws Exception
    {
        kurjunBootstrap.boot();
    }
}
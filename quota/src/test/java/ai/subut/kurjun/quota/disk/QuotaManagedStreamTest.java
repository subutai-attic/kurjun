package ai.subut.kurjun.quota.disk;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.omg.CORBA.portable.InputStream;

import ai.subut.kurjun.quota.QuotaException;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class QuotaManagedStreamTest
{
    private QuotaManagedStream quotaManagedStream;

    @Mock
    InputStream inputStream;


    @Before
    public void setUp() throws Exception
    {
        quotaManagedStream = new QuotaManagedStream( inputStream, 5 );
    }


    @Test
    public void read() throws Exception
    {
        quotaManagedStream.read();
    }


    @Test( expected = IOException.class )
    public void readException() throws Exception
    {
        quotaManagedStream = new QuotaManagedStream( inputStream, 0 );
        quotaManagedStream.read();
    }
}
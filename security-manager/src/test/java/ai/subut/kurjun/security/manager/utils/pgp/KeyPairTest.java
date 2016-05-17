package ai.subut.kurjun.security.manager.utils.pgp;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class KeyPairTest
{
    private KeyPair keyPair;


    @Before
    public void setUp() throws Exception
    {
        byte[] bytes = { 0, 1, 2, 3, 4, 5 };

        keyPair = new KeyPair();

        keyPair.setPrimaryKeyFingerprint( "primaryKeyFingerprint" );
        keyPair.setPrimaryKeyId( "primaryKeyId" );
        keyPair.setPubKeyring( bytes );
        keyPair.setSecKeyring( bytes );
        keyPair.setSubKeyFingerprint( "subKeyFingerprint" );
        keyPair.setSubKeyId( "subKeyId" );
    }


    @Test
    public void getPrimaryKeyId() throws Exception
    {
        assertNotNull( keyPair.getPrimaryKeyId() );
    }


    @Test
    public void getPrimaryKeyFingerprint() throws Exception
    {
        assertNotNull( keyPair.getPrimaryKeyFingerprint() );
    }


    @Test
    public void getSubKeyId() throws Exception
    {
        assertNotNull( keyPair.getSubKeyId() );
    }


    @Test
    public void getSubKeyFingerprint() throws Exception
    {
        assertNotNull( keyPair.getSubKeyFingerprint() );
    }


    @Test
    public void getPubKeyring() throws Exception
    {
        assertNotNull( keyPair.getPubKeyring() );
    }


    @Test
    public void getSecKeyring() throws Exception
    {
        assertNotNull( keyPair.getSecKeyring() );
    }
}
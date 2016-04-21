package ai.subut.kurjun.security.manager.utils.pgp;


import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class ContentAndSignaturesTest
{
    private ContentAndSignatures contentAndSignatures;

    @Mock
    PGPOnePassSignatureList pgpOnePassSignatures;

    @Mock
    PGPSignatureList pgpSignatures;


    @Before
    public void setUp() throws Exception
    {
        byte[] dectyptedContent = { 0, 1, 2, 3, 4, 5 };

        contentAndSignatures = new ContentAndSignatures( dectyptedContent, pgpOnePassSignatures, pgpSignatures );
    }


    @Test
    public void getDecryptedContent() throws Exception
    {
        assertNotNull( contentAndSignatures.getDecryptedContent() );
    }


    @Test
    public void getOnePassSignatureList() throws Exception
    {
        assertNotNull( contentAndSignatures.getOnePassSignatureList() );
    }


    @Test
    public void getSignatureList() throws Exception
    {
        assertNotNull( contentAndSignatures.getSignatureList() );
    }
}
package ai.subut.kurjun.metadata.common.subutai;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class TemplateIdTest
{
    private TemplateId templateId;


    @Before
    public void setUp() throws Exception
    {
        templateId = new TemplateId( "FCCF494471A9E89AB05C6BCED48E74E18333EBA3", "md5" );

        templateId.setCertified( true );
    }


    @Test
    public void get() throws Exception
    {
        assertNotNull( templateId.get() );
    }


    @Test
    public void getMd5() throws Exception
    {
        assertNotNull( templateId.getMd5() );
    }


    @Test
    public void getOwnerFprint() throws Exception
    {
        assertNotNull( templateId.getOwnerFprint() );
    }


    @Test
    public void isCertified() throws Exception
    {
        assertTrue( templateId.isCertified() );
    }


    @Test
    public void equals() throws Exception
    {
        templateId.equals( new Object() );
        templateId.equals( templateId );
        templateId.hashCode();
    }
}
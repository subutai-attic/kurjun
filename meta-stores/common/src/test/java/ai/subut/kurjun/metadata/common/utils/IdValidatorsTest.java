package ai.subut.kurjun.metadata.common.utils;


import org.junit.Before;
import org.junit.Test;

import ai.subut.kurjun.metadata.common.apt.DefaultIndexPackageMetaDataTest;

import static org.junit.Assert.*;


public class IdValidatorsTest
{
    private IdValidators validators;
    private IdValidators.Template template;


    @Before
    public void setUp() throws Exception
    {
        validators = new IdValidators();
        template = new IdValidators.Template();
    }


    @Test
    public void testValidate()
    {
        assertNotNull( template.validate( "owner", DefaultIndexPackageMetaDataTest.md5() ) );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testValidateWithEmptyMD5()
    {
        byte[] test = new byte[0];

        template.validate( "owner", test );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testValidateWithCombinedMD5Exception()
    {
        template.validate( DefaultIndexPackageMetaDataTest.md5().toString() );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testValidateWhenCombinedMD5IsNull()
    {
        assertNotNull( template.validate( null ) );
    }
}
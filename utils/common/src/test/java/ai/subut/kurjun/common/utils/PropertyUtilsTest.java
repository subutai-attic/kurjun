package ai.subut.kurjun.common.utils;


import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ai.subut.kurjun.common.ErrorCode;
import ai.subut.kurjun.model.metadata.Metadata;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class PropertyUtilsTest
{
    @Mock
    Properties properties;

    @Mock
    Metadata metadata;


    @Test
    public void makeKurjunProperties() throws Exception
    {
        assertNotNull( PropertyUtils.makeKurjunProperties( properties ) );
        assertNotNull( SnapUtils.makeFileName( metadata ) );

        ErrorCode.AccessPermissionError.toString();
        assertNotNull( ErrorCode.AccessPermissionError.getId() );
        assertNotNull( ErrorCode.AccessPermissionError.getName() );
    }
}
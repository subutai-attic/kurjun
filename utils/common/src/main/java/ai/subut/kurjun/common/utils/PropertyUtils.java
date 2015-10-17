package ai.subut.kurjun.common.utils;


import java.util.Properties;

import ai.subut.kurjun.common.KurjunPropertiesModule;
import ai.subut.kurjun.common.service.KurjunProperties;


/**
 * Utility class related to property handling.
 *
 */
public class PropertyUtils
{

    private PropertyUtils()
    {
        // not to be constructed
    }


    public static KurjunProperties makeKurjunProperties( Properties properties )
    {
        return KurjunPropertiesModule.create( properties );
    }

}


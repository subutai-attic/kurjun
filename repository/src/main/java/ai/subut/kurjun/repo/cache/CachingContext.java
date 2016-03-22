package ai.subut.kurjun.repo.cache;


import java.nio.file.Paths;
import java.util.Properties;

import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.storage.factory.FileStoreFactory;


/**
 * A special context to be used for package caching.
 *
 */
class CachingContext extends KurjunContext
{

    public static final String NAME = "__caching_context__";


    public CachingContext()
    {
        super( NAME );
    }


    /**
     * Sets appropriate property values for caching context. Basically, a file system backed file store in system
     * temporary directory is setup.
     *
     * @param properties properties to set property values
     */
    public static void setProperties( Properties properties )
    {
        properties.setProperty( FileStoreFactory.TYPE, FileStoreFactory.FILE_SYSTEM );
        properties.setProperty( KurjunConstants.FILE_STORE_FS_DIR_PATH,
                                Paths.get( System.getProperty( "java.io.tmpdir" ), "kurjun/cache" ).toString() );
    }

}


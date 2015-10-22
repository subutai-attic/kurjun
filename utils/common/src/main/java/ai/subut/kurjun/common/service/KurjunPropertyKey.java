package ai.subut.kurjun.common.service;


/**
 * This class consists of property keys to be used in Kurjun properties.
 *
 */
public final class KurjunPropertyKey
{

    private KurjunPropertyKey()
    {
        // not to be constructed
    }


    /**
     * Parent directory for file system backed file store implementation.
     */
    public static final String FILE_SYSTEM_PARENT_DIR = "file.store.fs.parent.dir";

    /**
     * Path to file where security related data is stored.
     */
    public static final String SECURITY_FILEDB_PATH = "security.filedb.path";

    /**
     * Keyserver URL to fetch public keys from.
     */
    public static final String SECURITY_KEYSERVER_URL = "security.hkp.url";
}


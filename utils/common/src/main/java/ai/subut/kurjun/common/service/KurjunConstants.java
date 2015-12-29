package ai.subut.kurjun.common.service;


/**
 * This class consists of constant values like property keys used in Kurjun properties or other constant values used
 * throughout the whole project.
 *
 */
public final class KurjunConstants
{

    private KurjunConstants()
    {
        // not to be constructed
    }

    /**
     * Packaging type of the repository. Values for this property are expected to be one of constants in
     * {@code PackageType}.
     */
    public static final String REPO_PACKAGE_TYPE = "repo.package.type";

    /**
     * Property key for root directory of the file system backed file store.
     */
    public static final String FILE_STORE_FS_ROOT_DIR = "file.store.fs.root.dir";

    /**
     * Property key to explicitly specify directory for file system backed file store. This property can only be used in
     * context properties.
     */
    public static final String FILE_STORE_FS_DIR_PATH = "file.store.fs.path";

    /**
     * Path to file where security related data is stored.
     */
    public static final String SECURITY_FILEDB_PATH = "security.filedb.path";

    /**
     * Keyserver URL to fetch public keys from.
     */
    public static final String SECURITY_KEYSERVER_URL = "security.hkp.url";

    /**
     * HTTP header for key fingerprints used for identifying purposes.
     */
    public static final String HTTP_HEADER_FINGERPRINT = "X-Subutai-Fingerprint";

    /**
     * HTTP parameter name for fingerprints.
     */
    public static final String HTTP_PARAM_FINGERPRINT = "fingerprint";

    /**
     * Path to file where quota related data is stored.
     */
    public static final String QUOTA_FILEDB_PATH = "quota.filedb.path";

    /**
     * Boolean value that indicates if quota info should be stored in memory. Defaults to 'false' in which case file
     * based db is used to store quotas info.
     */
    public static final String QUOTA_IN_MEMORY = "quota.storage.inmemory";


}


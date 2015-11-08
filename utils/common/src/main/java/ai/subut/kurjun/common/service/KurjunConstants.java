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

    /**
     * HTTP header for key fingerprints used for identifying purposes.
     */
    public static final String HTTP_HEADER_FINGERPRINT = "X-Subutai-Fingerprint";
}


package ai.subut.kurjun.repo.service;


import ai.subut.kurjun.model.metadata.apt.PackageMetadata;


/**
 * Builder of filename field of a Debian package.
 *
 */
public interface PackageFilenameBuilder
{
    /**
     * Makes a filename field value for the supplied meta data. Filename field indicates the path to package file
     * relative to repositories base directory. Usually it formatted in the following way:
     * <p>
     * {@code pool/{component}/{first-letter-of-package}/{package}/{package-file} } where {@code {package-file}} has
     * format {@code {package}_{version}_{arch}.deb}
     * <p>
     * This method conforms to that convention.
     *
     * @param metadata meta data for which to make a filename field
     * @return filename field value
     */
    String makeFilename( PackageMetadata metadata );


    /**
     * Makes package file name for the supplied meta data. Refer to
     * {@link PackageFilenameBuilder#makeFilename(ai.subut.kurjun.model.metadata.PackageMetadata)} docs for details.
     *
     * @param metadata meta data for which to make a package file name
     * @return package file name
     */
    String makePackageFilename( PackageMetadata metadata );
}


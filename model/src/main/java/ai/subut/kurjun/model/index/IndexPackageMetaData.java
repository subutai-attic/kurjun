package ai.subut.kurjun.model.index;


import java.util.List;

import ai.subut.kurjun.model.metadata.apt.PackageMetadata;


/**
 * Index files contain additional Package meta data fields.
 */
public interface IndexPackageMetaData extends PackageMetadata
{
    /**
     * The field holding the MD5 digest of the package's description.
     */
    String DESCRIPTION_MD5_FIELD = "Description-md5";

    /**
     * @todo have no idea what this is used for Example values are: Tag: game::strategy, implemented-in::c++,
     * interface::x11, role::program, uitoolkit::sdl, uitoolkit::wxwidgets, use::gameplaying, x11::application Also note
     * that this is a multi-line field.
     */
    String TAG_FIELD = "Tag";

    /**
     * The field holding the relative path from the repository URL to the Debian archive file.
     */
    String FILENAME_FIELD = "Filename";

    /**
     * The field holding the size in bytes of the Debian archive file in the repository.
     */
    String SIZE_FIELD = "Size";

    /**
     * The field holding the MD5 checksum for the Debian archive file in the repository.
     */
    String MD5SUM_FIELD = "MD5sum";

    /**
     * The field holding the SHA1 hash for the Debian archive file in the repository.
     */
    String SHA1_FIELD = "SHA1";

    /**
     * The field holding the SHA256 hash for the Debian archive file in the repository.
     */
    String SHA256_FIELD = "SHA256";


    /**
     * Gets the SHA1 hash for the Debian Package file in the repository.
     *
     * @return the SHA1 hash for the package
     */
    byte[] getSHA1();


    /**
     * Gets the SHA256 hash for the Debian Package file in the repository.
     *
     * @return the SHA256 hash for the package file
     */
    byte[] getSHA256();


    /**
     * Gets the MD5 checksum for the Debian Package file in the repository.
     *
     * @return the MD5 checksum for the package file
     */
    @Override
    byte[] getMd5Sum();


    /**
     * Gets the size in bytes of the Debian Package file in the repository.
     *
     * @return the size in bytes of the package file
     */
    long getSize();


    /**
     * Gets the repository URL relative path of the Debian Package file in the repository.
     */
    @Override
    String getFilename();


    /**
     * Gets the Description-md5 value.
     */
    byte[] getDescriptionMd5();


    /**
     * Gets the Tags for the package.
     */
    List<TagItem> getTag();

}


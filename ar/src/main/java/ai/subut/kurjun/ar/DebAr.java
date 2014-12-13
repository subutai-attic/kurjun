package ai.subut.kurjun.ar;


import java.io.File;


/**
 * A utility class to access a Debian archive file.
 */
public interface DebAr
{
    /**
     * Gets access to an extracted file of the control file. The first
     * call may extract the file to a temporary location. Subsequent
     * calls do not extract.
     *
     * @return the extracted control file
     */
    File getControlFile();

    /**
     * Gets access to an extracted file of the md5sums file. The first
     * call may extract the file to a temporary location. Subsequent
     * calls do not extract.
     *
     * @return the extracted md5sums file
     */
    File getMd5Sums();

    /**
     * Checks to see if this DebAr is a version 2.0 archive file.
     *
     * @return true if version 2, false otherwise
     */
    boolean isVersion2();
}

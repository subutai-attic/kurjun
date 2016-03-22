package ai.subut.kurjun.ar;


import java.io.File;
import java.io.IOException;


/**
 * Interface for working with Tar files.
 */
public interface Tar
{
    /**
     * Gets the Tar archive file.
     *
     * @return the Tar archive file
     */
    File getFile();

    /**
     * Extracts the Tar archive file to the specified directory path,
     * creating it if it does not exist.
     *
     * @param extractTo the directory to extract the Tar file to
     */
    void extract( File extractTo ) throws IOException;
}

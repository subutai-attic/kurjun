package ai.subut.kurjun.model.repository;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import ai.subut.kurjun.model.metadata.apt.PackageMetadata;


/**
 * A non-virtual Repository that is locally hosted on this Kurjun server.
 */
public interface LocalRepository extends Repository
{

    /**
     * Gets base directory of the repository.
     *
     * @return path to base directory
     */
    Path getBaseDirectory();


    PackageMetadata put( InputStream is ) throws IOException;
}


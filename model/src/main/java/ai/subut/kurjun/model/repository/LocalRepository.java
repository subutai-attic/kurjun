package ai.subut.kurjun.model.repository;


import java.io.IOException;
import java.io.InputStream;

import ai.subut.kurjun.model.metadata.Metadata;


/**
 * A non-virtual Repository that is locally hosted on this Kurjun server.
 */
public interface LocalRepository extends Repository
{

    /**
     * Puts supplied package stream into the repository. Package data obtained from stream should be parsed and, if
     * valid data is found, should be saved accordingly.
     *
     * @param is stream to read package data from
     * @return package meta data
     * @throws IOException
     */
    Metadata put( InputStream is ) throws IOException;
}


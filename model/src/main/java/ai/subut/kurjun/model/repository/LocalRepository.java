package ai.subut.kurjun.model.repository;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.model.metadata.Metadata;


/**
 * A non-virtual Repository that is locally hosted on this Kurjun server.
 */
public interface LocalRepository extends Repository
{

    /**
     * Puts supplied package stream into the repository. Shortcut for {@link LocalRepository#put(java.io.InputStream,
     * ai.subut.kurjun.ar.CompressionType)} to be used for uncompressed streams.
     *
     * @param is stream to read package data from
     *
     * @return package meta data
     */
    Metadata put( InputStream is ) throws IOException;


    /**
     * Puts supplied package stream into the repository. Package data obtained from stream should be parsed and, if
     * valid data is found, be saved accordingly. Optional compression type can be supplied to indicate compression type
     * of the stream.
     *
     * @param is stream to read package data from
     * @param compressionType compression type of the stream
     *
     * @return package meta data
     *
     * @see LocalRepository#put(java.io.InputStream)
     */
    Metadata put( InputStream is, CompressionType compressionType ) throws IOException;

    Metadata put( InputStream is, CompressionType compressionType, String owner ) throws IOException;

    Metadata put( File file, CompressionType compressionType, String owner ) throws IOException;

    /**
     * Deletes package from the repository. Package should be specified by its md5 checksum.
     *
     * @param md5 md5 checksum of the package to delete
     *
     * @return {@code true} if package was found and successfully deleted; {@code false} otherwise. Failure to delete
     * may be caused by various reasons, for example when package for supplied md5 could not be found, or if package
     * deletion is not permitted.
     */
    boolean delete( byte[] md5 ) throws IOException;


    boolean delete( Object id, byte[] md5 ) throws IOException;

    Object getContext();

    byte[] md5();
}


package ai.subut.kurjun.index.service;


import java.io.IOException;
import java.io.OutputStream;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.storage.FileStore;


/**
 * Packages index file builder.
 *
 */
public interface PackagesIndexBuilder
{

    /**
     * Sets file store to use when building packages index files. TODO: better injection mechanism
     *
     * @param fileStore
     */
    void setFileStore( FileStore fileStore );


    /**
     * Builds packages index file for the supplied component and architecture. Output will be plain stream without
     * compression.
     *
     * @param component component name to build packages index
     * @param arch architecture of packages to be included in the index
     * @param os sink to output packages index stream
     * @throws IOException
     */
    void buildIndex( String component, Architecture arch, OutputStream os ) throws IOException;


    /**
     * Builds packages index file for the supplied component and architecture. Output will be compressed by specified
     * compression type.
     *
     * @param component component name to build packages index
     * @param arch architecture of packages to be included in the index
     * @param os sink to output packages index stream
     * @param compressionType compression type to compress output stream
     * @throws IOException
     */
    void buildIndex( String component, Architecture arch, OutputStream os, CompressionType compressionType ) throws IOException;


}


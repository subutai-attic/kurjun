package ai.subut.kurjun.index.service;


import java.io.IOException;
import java.io.OutputStream;

import ai.subut.kurjun.ar.CompressionType;


/**
 * Packages index file builder.
 *
 */
public interface PackagesIndexBuilder
{
    /**
     * Builds packages index file for the supplied component. Output will be plain stream without compression.
     *
     * @param component component name to build packages index
     * @param os sink to output packages index stream
     * @throws IOException
     */
    void buildIndex( String component, OutputStream os ) throws IOException;


    /**
     * Builds packages index file for the supplied component. Output will be compressed by specified compression type.
     *
     * @param component component name to build packages index
     * @param os sink to output packages index stream
     * @param compressionType compression type to compress output stream
     * @throws IOException
     */
    void buildIndex( String component, OutputStream os, CompressionType compressionType ) throws IOException;
}


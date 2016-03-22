package ai.subut.kurjun.snap.service;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.model.metadata.snap.SnapMetadata;


public interface SnapMetadataParser
{

    /**
     * Parses metadata out of a given snap package.
     *
     * @param packageFile the package file
     * @return
     * @throws IOException if file reading or parsing errors occur
     */
    SnapMetadata parse( File packageFile ) throws IOException;


    /**
     * Parses metadata out of a given snap package stream.
     *
     * @param packageStream the package stream
     * @return
     * @throws IOException if stream reading or parsing errors occur
     */
    SnapMetadata parse( InputStream packageStream ) throws IOException;


    /**
     * Parses metadata out of a given compressed snap package stream.
     *
     * @param packageStream package stream
     * @param compressionType compression type
     * @return
     * @throws IOException
     */
    SnapMetadata parse( InputStream packageStream, CompressionType compressionType ) throws IOException;


    /**
     * Parses metadata from a given metadata file, usually named "packages.yaml"
     *
     * @param metadataFile metadata file of a snap package
     * @return
     * @throws IOException if file reading or parsing errors occur
     */
    SnapMetadata parseMetadata( File metadataFile ) throws IOException;


    /**
     * Parses metadata from a given metadata stream.
     *
     * @param metadataFileStream
     * @return
     * @throws IOException if stream reading or parsing errors occur
     */
    SnapMetadata parseMetadata( InputStream metadataFileStream ) throws IOException;
}


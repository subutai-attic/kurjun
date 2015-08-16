package ai.subut.kurjun.snap.service;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import ai.subut.kurjun.model.metadata.snap.SnapMetadata;


public interface SnappyMetadataParser
{

    /**
     * Parses metadata out of a given snap package.
     *
     * @param packageFile the package file
     * @return
     */
    SnapMetadata parse( File packageFile ) throws IOException;


    /**
     * Parses metadata out of a given snap package stream.
     *
     * @param packageStream the package stream
     * @return
     */
    SnapMetadata parse( InputStream packageStream ) throws IOException;


    /**
     * Parses metadata from a given metadata file, usually named "packages.yaml"
     *
     * @param metadataFile metadata file of a snap package
     * @return
     */
    SnapMetadata parseMetadata( File metadataFile ) throws IOException;


    /**
     * Parses metadata from a given metadata stream.
     *
     * @param metadataFileStream
     * @return
     */
    SnapMetadata parseMetadata( InputStream metadataFileStream ) throws IOException;
}


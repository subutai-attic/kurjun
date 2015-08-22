package ai.subut.kurjun.index.service;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.model.index.IndexPackageMetaData;


/**
 * Interface to parse package index files. Usually named as "Packages", "Packages.gz", etc.
 *
 */
public interface PackagesIndexParser
{

    /**
     * Parses specified packages index file and returns package metadata items. Specified file may a plain file or
     * compressed one with either of algorithms specified in {@link CompressionType} enum.
     *
     * @param indexFile packages index file
     * @param component the component supplied packages index belongs to
     * @throws IOException
     * @return list of packages meta data
     */
    List<IndexPackageMetaData> parse( File indexFile, String component ) throws IOException;


    /**
     * Parses given stream of packages index according specified compression type.
     *
     * @param is input stream to read index contents from
     * @param compressionType compression type of the contents
     * @param component the component supplied packages index belongs to
     * @throws IOException
     * @return list of packages meta data
     */
    List<IndexPackageMetaData> parse( InputStream is, CompressionType compressionType, String component ) throws IOException;

}


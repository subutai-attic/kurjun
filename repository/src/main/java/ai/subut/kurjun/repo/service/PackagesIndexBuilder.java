package ai.subut.kurjun.repo.service;


import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.repo.util.PackagesProviderFactory;


/**
 * Packages index file builder.
 *
 */
public interface PackagesIndexBuilder
{


    /**
     * Builds packages index file for packages provided by {@link PackagesProvider}. Output will be compresses by
     * specified compression type. To produce plain output without compression use {@link CompressionType#NONE}.
     *
     * @param provider packages provider whose packages will be used to build packages index
     * @param os sink to output packages index contents
     * @param compressionType compression type to compress output stream
     * @throws IOException
     */
    void buildIndex( PackagesProvider provider, OutputStream os, CompressionType compressionType ) throws IOException;


    /**
     * Packages provider for packages index builder. Packages index is usually built for some certain component and
     * architecture. Those parameters are expected to be present when creating instances of this class.
     *
     * @see PackagesProviderFactory
     */
    interface PackagesProvider
    {
        List<SerializableMetadata> getPackages();
    }


}


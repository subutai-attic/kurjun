package ai.subut.kurjun.riparser;


import java.io.IOException;
import java.io.InputStream;

import ai.subut.kurjun.model.index.ReleaseFile;


public interface ReleaseIndexParser
{
    /**
     * Parses a release file. Usually named as `Release'.
     *
     * @param is data input stream
     * @return release file instance if successfully parsed
     * @throws IOException
     */
    ReleaseFile parse( InputStream is ) throws IOException;


    /**
     * Parses a clear-signed release file. Usually named as `InRelease'. Clear signed files are verified by provided
     * public key first.
     *
     * @param is clear-signed data stream
     * @param keyStream public key stream
     * @return release file instance if successfully verified and parsed, {@code null} otherwise
     * @throws IOException
     */
    ReleaseFile parseClearSigned( InputStream is, InputStream keyStream ) throws IOException;


    /**
     * Parses a release file with detached signature provided. Data is verified against signature first.
     *
     * @param is data input stream
     * @param signStream detached signature stream
     * @param keyStream public key stream
     * @return release file instance if successfully verified and parsed, {@code null} otherwise
     * @throws IOException
     */
    ReleaseFile parseWithSignature( InputStream is, InputStream signStream, InputStream keyStream ) throws IOException;

}


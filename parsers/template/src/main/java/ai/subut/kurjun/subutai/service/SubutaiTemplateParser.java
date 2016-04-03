package ai.subut.kurjun.subutai.service;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;


/**
 * Subutai metadata configuration parser. Subutai metadata is tar archive which contains "config" file that contains
 * metadata information. More info can be found at https://confluence.subutai.io/x/AgKUAQ.
 *
 */
public interface SubutaiTemplateParser
{

    /**
     * Parses supplied metadata file. File should be a tar archive file.
     *
     * @param file metadata file
     * @return metadata meta data
     * @throws IOException
     */
    SubutaiTemplateMetadata parseTemplate( File file ) throws IOException;


    /**
     * Parses metadata config file from supplied stream. Please note that this method receives stream of a config file
     * and so can not calculate md5 sum of the package itself.
     *
     * @param stream stream to read metadata config file contents from
     * @return metadata meta data without md5 sum value
     * @throws IOException
     */
    SubutaiTemplateMetadata parseTemplateConfigFile( InputStream stream ) throws IOException;
}


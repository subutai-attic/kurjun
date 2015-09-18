package ai.subut.kurjun.subutai.service;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import ai.subut.kurjun.model.metadata.template.TemplateMetadata;


/**
 * Subutai template configuration parser. Subutai template is tar archive which contains "config" file that contains
 * template information. More info can be found at https://confluence.subutai.io/x/AgKUAQ.
 *
 */
public interface SubutaiTemplateParser
{

    /**
     * Parses supplied template file. File should be a tar archive file.
     *
     * @param file template file
     * @return template meta data
     * @throws IOException
     */
    TemplateMetadata parseTemplate( File file ) throws IOException;


    /**
     * Parses template config file from supplied stream. Please note that this method receives stream of a config file
     * and so can not calculate md5 sum of the package itself.
     *
     * @param stream stream to read template config file contents from
     * @return template meta data without md5 sum value
     * @throws IOException
     */
    TemplateMetadata parseTemplateConfigFile( InputStream stream ) throws IOException;
}


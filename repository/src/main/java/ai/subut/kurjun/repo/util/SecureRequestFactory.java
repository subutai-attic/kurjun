package ai.subut.kurjun.repo.util;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.cxf.jaxrs.client.WebClient;

import ai.subut.kurjun.model.repository.Repository;
import io.subutai.common.settings.SecuritySettings;
import io.subutai.common.util.RestUtil;


/**
 * This class prepares web-client instances that can make requests to other repositories in a secure way.
 *
 */
public class SecureRequestFactory
{

    private Repository remoteRepository;


    /**
     * Constructs a secure request factory for supplied repository.
     *
     * @param remoteRepository repo to make requests to
     */
    public SecureRequestFactory( Repository remoteRepository )
    {
        this.remoteRepository = remoteRepository;
    }


    /**
     * Prepares a web client that can do secure requests to the repository at specified path with supplied query
     * parameters.
     *
     * @param path path to make a request
     * @param queryParams optional query parameters with map keys as parameter names and values as parameter values
     * @return
     */
    public WebClient makeClient( String path, Map<String, String> queryParams )
    {
        // merge repository path and supplied path
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append( remoteRepository.getPath() );
        if ( !remoteRepository.getPath().endsWith( "/" ) )
        {
            pathBuilder.append( "/" );
        }
        pathBuilder.append( path );

        // prepare query part of URL
        StringBuilder queryBuilder = new StringBuilder();
        if ( queryParams != null )
        {
            for ( Map.Entry< String, String> e : queryParams.entrySet() )
            {
                queryBuilder.append( e.getKey() ).append( "=" ).append( e.getValue() );
            }
        }

        try
        {
            URI uri = new URI( remoteRepository.getProtocol().toString(), null,
                               remoteRepository.getHostname(),
                               remoteRepository.getPort(),
                               pathBuilder.toString(),
                               queryBuilder.toString(),
                               null );

            // refer to design page at https://confluence.subutai.io/x/HQS3AQ
            String alias = SecuritySettings.KEYSTORE_PX2_ROOT_ALIAS;
            return RestUtil.createTrustedWebClientWithAuth( uri.toURL().toString(), alias );
        }
        catch ( URISyntaxException | MalformedURLException ex )
        {
            throw new IllegalArgumentException( "Failed to build URL", ex );
        }
    }
}


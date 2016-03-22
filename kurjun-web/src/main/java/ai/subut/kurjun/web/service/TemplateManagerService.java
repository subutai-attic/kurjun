package ai.subut.kurjun.web.service;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.metadata.common.subutai.TemplateId;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ninja.Renderable;


public interface TemplateManagerService extends BaseService
{
    /**
     * Gets template info.
     *
     * @param md5 md5 checksum of the package to retrieve info
     *
     * @return JSON encoded meta data
     */
    SerializableMetadata getTemplate( byte[] md5 ) throws IOException;

    /**
     * Gets the list of remote repo urls
     *
     * @return Set of urls
     */
    List<Map<String, Object>> getRemoteRepoUrls();


    /**
     * Gets template stream.
     *
     * @param repository repository
     * @param md5 md5 checksum of the package to retrieve
     * @param isKurjunClient where the client is Kurjun or not
     *
     * @return input stream to read package data
     */
    InputStream getTemplateData( String repository, byte[] md5, boolean isKurjunClient ) throws IOException;


    /**
     * Lists packages in supplied repository.
     *
     * @param repository repository
     * @param isKurjunClient where the client is Kurjun or not
     *
     * @return list of JSON encoded meta data
     */
    List<SerializableMetadata> list( String repository, boolean isKurjunClient ) throws IOException;


    List<Map<String, Object>> getSharedTemplateInfos( byte[] md5, String templateOwner ) throws IOException;


    List<Map<String, Object>> listAsSimple( String repository ) throws IOException;


    /**
     * Lists packages in public repository. The request treated as not kurjun client.
     *
     * @return list of JSON encoded meta data
     */
    List<SerializableMetadata> list();


    /**
     * Uploads package data from supplied input stream to the repository defined by supplied repository.
     *
     * @param repository repository
     * @param inputStream input stream to read package data
     *
     * @return template id of uploaded package upload succeeds; {@code null} otherwise
     */
    String upload( String repository, InputStream inputStream ) throws IOException;

    /**
     * Uploads package data from supplied input stream to the repository defined by supplied repository.
     *
     * @param repository repository
     * @param file input stream to read package data
     *
     * @return template id of uploaded package upload succeeds; {@code null} otherwise
     */
    String upload( String repository, File file ) throws IOException;

    /**
     * Deletes package from the repository defined by supplied repository.
     *
     * @param templateId checksum of the package to delete
     *
     * @return {@code true} if package successfully deleted; {@code false} otherwise
     */
    boolean delete( TemplateId templateId ) throws IOException;


    /**
     * Adds remote repository located at supplied URL. Repositories added with this method will be used to fulfill
     * requests in case the local repository can not handle requests.
     *
     * @param url URL of the remote repository
     * @param token access token to be used for the given remote repo url
     */
    void addRemoteRepository( URL url, String token );


    /**
     * Removes remote repository located at supplied URL.
     *
     * @param url URL of the remote repository
     */
    void removeRemoteRepository( URL url );


    /**
     * Gets the set of repositories
     */
    Set<String> getRepositories();


    /**
     * Create repository for the user with the given user name
     */
    LocalRepository createUserRepository( KurjunContext userName );


    /**
     * Shares given template to given target user by current active user
     *
     * @param templateId template id
     * @param targetUserName target username
     */
    void shareTemplate( String templateId, String targetUserName );


    /**
     * Deletes the share for given template to given target user by current active user
     *
     * @param templateId template id
     * @param targetUserName target username
     */
    void unshareTemplate( String templateId, String targetUserName );

    /*
    *
    * */
    Renderable renderableTemplate( String repository, String md5, boolean isKurjunClient ) throws IOException;

    String md5();

    DefaultTemplate getTemplate( TemplateId templateId, String md5, String name, String version );

    boolean downloadTemplates();
}

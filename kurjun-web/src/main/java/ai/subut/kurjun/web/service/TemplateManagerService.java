package ai.subut.kurjun.web.service;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.metadata.common.subutai.TemplateId;
import ai.subut.kurjun.model.identity.UserSession;
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
    SerializableMetadata getTemplate(UserSession userSession, String md5 ) throws IOException;

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
    InputStream getTemplateData(UserSession userSession, String repository, String md5, boolean isKurjunClient ) throws IOException;


    /**
     * Lists packages in supplied repository.
     *
     * @param repository repository
     * @param isKurjunClient where the client is Kurjun or not
     *
     * @return list of JSON encoded meta data
     */
    List<SerializableMetadata> list(UserSession userSession, String repository, boolean isKurjunClient ) throws IOException;


    List<Map<String, Object>> getSharedTemplateInfos( String md5, String templateOwner ) throws IOException;


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
    String upload(UserSession userSession, String repository, InputStream inputStream ) throws IOException;

    /**
     * Uploads package data from supplied input stream to the repository defined by supplied repository.
     *
     * @param repository repository
     * @param file input stream to read package data
     *
     * @return template id of uploaded package upload succeeds; {@code null} otherwise
     */
    String upload(UserSession userSession, String repository, File file ) throws IOException;

    /**
     * Deletes package from the repository defined by supplied repository.
     *
     * @param templateId checksum of the package to delete
     *
     * @return {@code true} if package successfully deleted; {@code false} otherwise
     */
    int delete(UserSession userSession, TemplateId templateId ) throws IOException;


    /**
     * Gets the set of repositories
     */
    Set<String> getRepositories();


    /**
     * Create repository for the user with the given user name
     */
    LocalRepository createUserRepository( KurjunContext userName );


    Renderable renderableTemplate(UserSession userSession, String repository, String md5, boolean isKurjunClient ) throws IOException;


    String md5();


    DefaultTemplate getTemplate(UserSession userSession, TemplateId templateId, String md5, String name, String version );


    int downloadTemplates();
}

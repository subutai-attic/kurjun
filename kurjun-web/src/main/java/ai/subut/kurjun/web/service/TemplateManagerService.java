package ai.subut.kurjun.web.service;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.template.TemplateData;
import ai.subut.kurjun.model.repository.LocalRepository;
import ninja.Renderable;


public interface TemplateManagerService extends BaseService
{


    List<SerializableMetadata> list( UserSession userSession, String repository, String search,
                                     boolean isKurjunClient ) throws IOException;

    /**
     * Uploads package data from supplied input stream to the repository defined by supplied repository.
     *
     * @param repository repository
     * @param inputStream input stream to read package data
     *
     * @return metadata id of uploaded package upload succeeds; {@code null} otherwise
     */
    String upload(UserSession userSession, String repository, InputStream inputStream ) throws IOException;



    /**
     * Deletes package from the repository defined by supplied repository.
     *
     *
     * @return {@code true} if package successfully deleted; {@code false} otherwise
     */
    int delete(UserSession userSession, String repository , String md5) throws IOException;


    /**
     * Create repository for the user with the given user name
     */
    LocalRepository createUserRepository( KurjunContext userName );


    Renderable renderableTemplate(UserSession userSession, String repository, String md5, boolean isKurjunClient ) throws IOException;


    String md5();


    TemplateData getTemplate(UserSession userSession, String repository, String md5, String name, String version );


}

package ai.subut.kurjun.core.dao.service.metadata;


import java.util.List;

import ai.subut.kurjun.model.metadata.SerializableMetadata;


/**
 *
 */
public interface TemplateDataService
{
    //*****************************
    void persist( SerializableMetadata template );

    //*****************************
    SerializableMetadata merge( SerializableMetadata template );

    //*****************************
    SerializableMetadata find( String id );

    //*****************************
    List<SerializableMetadata> findAll();

    //*****************************
    boolean delete( SerializableMetadata metadata );
}

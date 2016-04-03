package ai.subut.kurjun.core.dao.service.metadata;


import java.util.List;

import ai.subut.kurjun.model.metadata.apt.PackageMetadata;


/**
 *
 */
public interface AptDataService
{
    //*****************************
    void persist( PackageMetadata packageMetadata );

    //*****************************
    PackageMetadata merge( PackageMetadata packageMetadata );

    //*****************************
    PackageMetadata find( String id );

    //*****************************
    List<PackageMetadata> findAll();

    //*****************************
    boolean delete( PackageMetadata packageMetadata );
}

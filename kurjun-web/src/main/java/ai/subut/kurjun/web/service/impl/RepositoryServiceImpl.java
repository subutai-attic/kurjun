package ai.subut.kurjun.web.service.impl;


import java.io.File;
import java.util.*;

import ai.subut.kurjun.model.identity.RelationObjectType;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.web.service.RelationManagerService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.metadata.storage.file.DbFilePackageMetadataStoreModule;
import ai.subut.kurjun.web.service.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Singleton
public class RepositoryServiceImpl implements RepositoryService
{


    @Inject KurjunProperties kurjunProperties;


    private static final Logger LOGGER = LoggerFactory.getLogger( RepositoryService.class );

    @Override
    public synchronized List<String> getRepositories()
    {
        String fileDbDirectory = kurjunProperties.get( DbFilePackageMetadataStoreModule.DB_FILE_LOCATION_NAME );

        File fileDirectory = new File( fileDbDirectory );
        File[] files = fileDirectory.listFiles();

        List<String> results = new ArrayList<>();

        for ( File file : files )
        {
            if ( file.isDirectory() )
            {
                results.add( file.getName() );
            }
        }

        results.remove( AptManagerServiceImpl.REPO_NAME );
        results.remove( RawManagerServiceImpl.DEFAULT_RAW_REPO_NAME );

        return results;
    }

    @Override
    public List<String> getRepositoryList()
    {
        List<String> repoList = new ArrayList<>();

        return repoList;
    }



}

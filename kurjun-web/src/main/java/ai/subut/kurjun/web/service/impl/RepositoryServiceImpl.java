package ai.subut.kurjun.web.service.impl;


import java.util.*;

import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.repo.service.RepositoryManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.web.service.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Singleton
public class RepositoryServiceImpl implements RepositoryService
{

    //@Inject KurjunProperties kurjunProperties;

    @Inject
    RepositoryManager repositoryManager;



    private static final Logger LOGGER = LoggerFactory.getLogger( RepositoryService.class );


    @Override
    public synchronized List<RepositoryData> getRepositoryList( int repoType )
    {
        return repositoryManager.getRepositoryList(repoType);
    }


    @Override
    public synchronized List<String> getRepositoryContextList( int repoType )
    {
        List<RepositoryData> repoDataList = repositoryManager.getRepositoryList(repoType);

        if(repoDataList.isEmpty())
        {
            return Collections.emptyList();
        }
        else
        {
            List<String> contexList = new ArrayList<>();

            for(RepositoryData repodata:repoDataList)
            {
                contexList.add( repodata.getContext() );
            }

            return contexList;
        }
    }

}

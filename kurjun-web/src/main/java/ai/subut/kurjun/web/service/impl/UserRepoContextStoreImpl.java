package ai.subut.kurjun.web.service.impl;


import java.io.IOException;
import java.util.Map;
import java.util.Set;

import ai.subut.kurjun.model.identity.UserSession;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.web.model.UserContext;
import ai.subut.kurjun.web.service.UserRepoContextStore;


@Singleton
public class UserRepoContextStoreImpl implements UserRepoContextStore
{
    private static final String MAP_NAME_USER_REPO = "user_repo_contexts";

    private static final String DB_FILE_LOCATION_NAME = "user.context.path";

    private String repoFile;

    private UserSession userSession;


    @Inject
    public UserRepoContextStoreImpl( KurjunProperties kurjunProperties )
    {
        String fileDbDirectory = kurjunProperties.get( DB_FILE_LOCATION_NAME );
        this.repoFile = fileDbDirectory;
    }


    public UserRepoContextStoreImpl( String appDataBaseUrl )
    {
        String path = appDataBaseUrl == null ? "" : appDataBaseUrl + "/";
        repoFile = path + "kurjun/misc/user_repositories";
    }


    public void addUserRepoContext( UserContext userRepoContext ) throws IOException
    {
        FileDb fileDb = null;

        try
        {
            fileDb = new FileDb( repoFile );
            fileDb.put( MAP_NAME_USER_REPO, makeKey( userRepoContext ), userRepoContext );
        }
        finally
        {
            if(fileDb != null)
                fileDb.close();
        }

    }


    public UserContext removeUserRepoContext( UserContext userRepoContext ) throws IOException
    {
        UserContext removed;
        FileDb fileDb = null;

        try
        {
            fileDb = new FileDb( repoFile );
            removed = fileDb.remove( MAP_NAME_USER_REPO, makeKey( userRepoContext ) );
        }
        finally
        {
            if(fileDb != null)
                fileDb.close();
        }

        return removed;
    }


    public Set<UserContext> getUserRepoContexts() throws IOException
    {
        FileDb fileDb = null;

        try
        {
            fileDb = new FileDb( repoFile );
            Map<String, UserContext> map = fileDb.get( MAP_NAME_USER_REPO );

            return Sets.newConcurrentHashSet( map.values() );
        }
        finally
        {
            if(fileDb != null)
                fileDb.close();
        }
    }


    private String makeKey( UserContext userRepoContext )
    {
        return userRepoContext.getFingerprint();
    }

    @Override
    public void setUserSession( UserSession userSession ) {
        this.userSession = userSession;
    }

}

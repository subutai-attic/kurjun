package ai.subut.kurjun.web.service;


import java.io.IOException;
import java.util.Set;

import com.google.inject.Singleton;

import ai.subut.kurjun.model.user.UserContext;

@Singleton
public interface UserRepoContextStore
{
    void addUserRepoContext( UserContext userRepoContext ) throws IOException;

    UserContext removeUserRepoContext( UserContext userRepoContext ) throws IOException;

    Set<UserContext> getUserRepoContexts() throws IOException;
}

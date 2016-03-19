package ai.subut.kurjun.web.service;


import java.io.IOException;
import java.util.Set;

import ai.subut.kurjun.web.model.UserContext;


public interface UserRepoContextStore extends BaseService
{
    void addUserRepoContext( UserContext userRepoContext ) throws IOException;

    UserContext removeUserRepoContext( UserContext userRepoContext ) throws IOException;

    Set<UserContext> getUserRepoContexts() throws IOException;
}

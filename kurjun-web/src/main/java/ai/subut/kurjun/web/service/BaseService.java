package ai.subut.kurjun.web.service;


import ai.subut.kurjun.model.identity.UserSession;

public interface BaseService
{
    void setUserSession( UserSession userSession );

    UserSession getUserSession();
}

package ai.subut.kurjun.model.identity;


import java.util.Date;


/**
 *
 */
public interface UserSession
{
    User getUser();

    void setUser( User user );

    UserToken getUserToken();

    void setUserToken( UserToken userToken );

    int getStatus();

    void setStatus( int status );

    Date getStartDate();

    void setStartDate( Date startDate );

    Date getEndDate();

    void setEndDate( Date endDate );
}

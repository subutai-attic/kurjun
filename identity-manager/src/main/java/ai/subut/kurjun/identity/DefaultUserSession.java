package ai.subut.kurjun.identity;


import java.io.Serializable;
import java.util.Date;

import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.identity.UserToken;


/**
 *
 */
public class DefaultUserSession implements UserSession , Serializable
{
    private User user;
    private UserToken userToken;
    private int status = 1;
    private Date startDate = new Date( System.currentTimeMillis() );
    private Date endDate = new Date( System.currentTimeMillis() );


    @Override
    public User getUser()
    {
        return user;
    }


    @Override
    public void setUser( final User user )
    {
        this.user = user;
    }


    @Override
    public UserToken getUserToken()
    {
        return userToken;
    }


    @Override
    public void setUserToken( final UserToken userToken )
    {
        this.userToken = userToken;
    }


    @Override
    public int getStatus()
    {
        return status;
    }


    @Override
    public void setStatus( final int status )
    {
        this.status = status;
    }


    @Override
    public Date getStartDate()
    {
        return startDate;
    }


    @Override
    public void setStartDate( final Date startDate )
    {
        this.startDate = startDate;
    }


    @Override
    public Date getEndDate()
    {
        return endDate;
    }

    @Override
    public void setEndDate( final Date endDate )
    {
        this.endDate = endDate;
    }
}

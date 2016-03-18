package ai.subut.kurjun.model.identity;


import java.util.Date;


/**
 *
 */
public interface User
{
    String getKeyId();

    String getKeyFingerprint();

    Date getDate();

    String getEmailAddress();

    String getSignature();

    void setSignature( String signature );

    //*************************
    String getKeyData();

    //*************************
    void setKeyData( String keyData );

    //*************************
    UserToken getUserToken();

    //*************************
    void setUserToken( UserToken userToken );
}

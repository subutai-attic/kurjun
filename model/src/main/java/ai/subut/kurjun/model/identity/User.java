package ai.subut.kurjun.model.identity;


import java.util.Date;


/**
 *
 */
public interface User
{
    //*************************
    void setKeyId( String keyId );

    //*************************
    String getKeyId();


    //*************************
    String getKeyFingerprint();


    //*************************
    void setKeyFingerprint( String keyFingerprint );

    //*************************
    Date getDate();


    //*************************
    String getEmailAddress();


    //*************************
    String getSignature();


    //*************************
    void setSignature( String signature );


    //*************************
    String getKeyData();


    //*************************
    void setKeyData( String keyData );


    //*************************
    UserToken getUserToken();


    //*************************
    void setUserToken( UserToken userToken );

    //*************************
    int getType();

    //*************************
    void setType( int type );
}

package ai.subut.kurjun.model.identity;


import java.util.Date;


/**
 *
 */
public interface UserToken
{
    //***********************************
    String getHeader();

    //***********************************
    String getClaims();

    //***********************************
    String getFullToken();

    String getToken();

    void setToken( String token );

    String getSecret();

    void setSecret( String secret );

    String getHashAlgorithm();

    void setHashAlgorithm( String hashAlgorithm );

    String getIssuer();

    void setIssuer( String issuer );

    Date getValidDate();

    void setValidDate( Date validDate );
}

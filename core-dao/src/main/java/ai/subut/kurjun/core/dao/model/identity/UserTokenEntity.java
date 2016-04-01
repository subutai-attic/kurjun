package ai.subut.kurjun.core.dao.model.identity;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;

import ai.subut.kurjun.model.identity.UserToken;
import ai.subut.kurjun.security.manager.utils.token.TokenUtils;


/**
 *
 */
@Entity
@Table( name = UserTokenEntity.TABLE_NAME )
@Access( AccessType.FIELD )
public class UserTokenEntity implements UserToken, Serializable
{

    //*********************
    public static final String TABLE_NAME = "user_token";
    //*********************

    private String token;

    private String secret;

    private String hashAlgorithm;

    private String issuer;

    private Date validDate = null;


    //***********************************
    @Override
    public String getHeader()
    {
        String str = "";

        str += "{\"typ\":\"JWT\",";
        str += "\"alg\":\"" + hashAlgorithm + "\"}";

        return str;
    }


    //***********************************
    @Override
    public String getClaims()
    {
        String str = "";

        str += "{\"iss\":\"" + issuer + "\",";

        if(validDate == null)
            str += "\"exp\":0,";
        else
            str += "\"exp\":" + validDate.getTime() + ",";

        str += "\"sub\":\"" + token + "\"}";

        return str;
    }


    //***********************************
    @Override
    public String getFullToken()
    {
        return TokenUtils.createToken( getHeader(), getClaims(), secret );
    }
    //***********************************


    @Override
    public String getToken()
    {
        return token;
    }


    @Override
    public void setToken( final String token )
    {
        this.token = token;
    }


    @Override
    public String getSecret()
    {
        return secret;
    }


    @Override
    public void setSecret( final String secret )
    {
        this.secret = secret;
    }


    @Override
    public String getHashAlgorithm()
    {
        return hashAlgorithm;
    }


    @Override
    public void setHashAlgorithm( final String hashAlgorithm )
    {
        this.hashAlgorithm = hashAlgorithm;
    }


    @Override
    public String getIssuer()
    {
        return issuer;
    }


    @Override
    public void setIssuer( final String issuer )
    {
        this.issuer = issuer;
    }


    @Override
    public Date getValidDate()
    {
        return validDate;
    }


    @Override
    public void setValidDate( final Date validDate )
    {
        this.validDate = validDate;
    }

}

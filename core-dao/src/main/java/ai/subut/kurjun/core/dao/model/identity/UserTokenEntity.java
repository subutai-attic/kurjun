package ai.subut.kurjun.core.dao.model.identity;


import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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

    @Id
    @Column( name = "token" )
    private String token;

    @Column( name = "secret" )
    private String secret;

    @Column( name = "hash_algo" )
    private String hashAlgorithm;

    @Column( name = "issuer" )
    private String issuer;

    @Column( name = "valid_date" )
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


    //*************************
    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof UserTokenEntity )
        {
            UserTokenEntity other = ( UserTokenEntity ) obj;
            return Objects.equals( this.token , other.token );
        }
        return false;
    }


    //*************************
    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode( this.token);
        return hash;
    }


}

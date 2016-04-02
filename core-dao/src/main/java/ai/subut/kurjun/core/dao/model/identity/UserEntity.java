package ai.subut.kurjun.core.dao.model.identity;


import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserToken;


/**
 *
 */
@Entity
@Table( name = UserEntity.TABLE_NAME )
@Access( AccessType.FIELD )
public class UserEntity implements User, Serializable
{

    //*********************
    public static final String TABLE_NAME = "userl";
    //*********************

    @Id
    @Column( name = "fingerprint" )
    private String keyFingerprint = "";

    @Column( name = "username" ,unique = true)
    private String userName = "";

    @Column( name = "register_date" )
    private Date date = new Date(System.currentTimeMillis());

    @Column( name = "email")
    private String emailAddress = "";

    @Column( name = "signature")
    private String signature = "";

    @Lob
    @Column( name = "key_data" )
    private byte[] keyData;

    @Column( name = "type")
    private int type = 2;

    @Column( name = "trust_level")
    private int trustLevel = 3;


    @Column( name = "user_token" )
    @OneToOne( cascade = CascadeType.ALL, fetch = FetchType.EAGER,targetEntity = UserTokenEntity.class)
    private UserToken userToken = null;



    //*************************
    public UserEntity()
    {
    }


    //*************************
    @Override
    public String getUserName()
    {
        return userName;
    }


    //*************************
    @Override
    public void setUserName( final String userName )
    {
        this.userName = userName;
    }


    //*************************
    @Override
    public void setDate( final Date date )
    {
        this.date = date;
    }


    //*************************
    @Override
    public void setEmailAddress( final String emailAddress )
    {
        this.emailAddress = emailAddress;
    }


    //*************************
    @Override
    public String getKeyFingerprint()
    {
        return keyFingerprint;
    }


    //*************************
    @Override
    public void setKeyFingerprint(String keyFingerprint)
    {
        this.keyFingerprint = keyFingerprint.toLowerCase();
    }


    //*************************
    @Override
    public Date getDate()
    {
        return date;
    }


    //*************************
    @Override
    public String getEmailAddress()
    {
        return emailAddress;
    }


    //*************************
    @Override
    public String getSignature()
    {
        return signature;
    }


    //*************************
    @Override
    public void setSignature( final String signature )
    {
        this.signature = signature;
    }


    //*************************
    @Override
    public byte[] getKeyData()
    {
        return keyData;
    }


    //*************************
    @Override
    public void setKeyData( final byte[] keyData )
    {
        this.keyData = keyData;
    }


    //*************************
    @Override
    public UserToken getUserToken()
    {
        return userToken;
    }


    //*************************
    @Override
    public void setUserToken( final UserToken userToken )
    {
        this.userToken = userToken;
    }


    //*************************
    @Override
    public int getType()
    {
        return type;
    }


    //*************************
    @Override
    public void setType( final int type )
    {
        this.type = type;
    }


    //*************************
    @Override
    public int getTrustLevel()
    {
        return trustLevel;
    }


    //*************************
    @Override
    public void setTrustLevel( final int trustLevel )
    {
        this.trustLevel = trustLevel;
    }


    //*************************
    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode( this.keyFingerprint );
        return hash;
    }

}


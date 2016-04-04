package ai.subut.kurjun.core.dao.model.metadata;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import ai.subut.kurjun.model.metadata.RepositoryData;


/**
 *
 */
@Entity
@Table( name = RepositoryDataEntity.TABLE_NAME )
@Access( AccessType.FIELD )
public class RepositoryDataEntity implements RepositoryData
{
    public static final String TABLE_NAME = "repository";


    @EmbeddedId
    private RepositoryDataId id;


    @Column( name = "fingerprint" )
    private String ownerFingerpint;


    public RepositoryDataEntity()
    {
    }

    public RepositoryDataEntity( String context, int type )
    {
        this.id = new RepositoryDataId(context, type);
    }


    public RepositoryDataId getId()
    {
        return id;
    }


    @Override
    public String getContext()
    {
        return (this.id != null)?this.id.getContext():"";
    }


    @Override
    public int getType()
    {
        return (this.id != null)?this.id.getType():0;
    }


    @Override
    public String getOwnerFingerpint()
    {
        return ownerFingerpint;
    }


    @Override
    public void setOwnerFingerpint( final String ownerFingerpint )
    {
        this.ownerFingerpint = ownerFingerpint;
    }

}

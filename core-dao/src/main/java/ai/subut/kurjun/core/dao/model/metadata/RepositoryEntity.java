package ai.subut.kurjun.core.dao.model.metadata;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 *
 */
@Entity
@Table( name = AptEntity.TABLE_NAME )
@Access( AccessType.FIELD )
public class RepositoryEntity
{
    public static final String TABLE_NAME = "debs";

    @Id
    @Column( name = "context" )
    private String context;

    @Column( name = "fingerprint" )
    private String ownerFingerpint;

    @Column( name = "type" )
    private int type;


    public String getContext()
    {
        return context;
    }


    public void setContext( final String context )
    {
        this.context = context;
    }


    public String getOwnerFingerpint()
    {
        return ownerFingerpint;
    }


    public void setOwnerFingerpint( final String ownerFingerpint )
    {
        this.ownerFingerpint = ownerFingerpint;
    }


    public int getType()
    {
        return type;
    }


    public void setType( final int type )
    {
        this.type = type;
    }
}

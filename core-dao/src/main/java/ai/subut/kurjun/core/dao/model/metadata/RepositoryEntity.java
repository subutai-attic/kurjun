package ai.subut.kurjun.core.dao.model.metadata;


import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.Protocol;
import ai.subut.kurjun.model.repository.Repository;


/* ************************* */
@Embeddable
class RepositoryId
{
    @Column( name = "context" )
    String context;

    @Column( name = "type" )
    int type;
}


//************************* */

/**
 *
 */
@Entity
@Table( name = AptEntity.TABLE_NAME )
@Access( AccessType.FIELD )
public class RepositoryEntity implements Repository
{
    public static final String TABLE_NAME = "repository";


    @EmbeddedId
    private RepositoryId id;

    @Column( name = "fingerprint" )
    private String ownerFingerpint;


    public RepositoryId getId()
    {
        return id;
    }


    public void setId( final RepositoryId id )
    {
        this.id = id;
    }


    public String getOwnerFingerpint()
    {
        return ownerFingerpint;
    }


    public void setOwnerFingerpint( final String ownerFingerpint )
    {
        this.ownerFingerpint = ownerFingerpint;
    }


    @Override
    public UUID getIdentifier()
    {
        return null;
    }


    @Override
    public URL getUrl()
    {
        return null;
    }


    @Override
    public String getPath()
    {
        return null;
    }


    @Override
    public String getHostname()
    {
        return null;
    }


    @Override
    public int getPort()
    {
        return 0;
    }


    @Override
    public boolean isSecure()
    {
        return false;
    }


    @Override
    public Protocol getProtocol()
    {
        return null;
    }


    @Override
    public boolean isKurjun()
    {
        return false;
    }


    @Override
    public Set<ReleaseFile> getDistributions()
    {
        return null;
    }


    @Override
    public SerializableMetadata getPackageInfo( final Metadata metadata )
    {
        return null;
    }


    @Override
    public InputStream getPackageStream( final Metadata metadata )
    {
        return null;
    }


    @Override
    public List<SerializableMetadata> listPackages()
    {
        return null;
    }
}

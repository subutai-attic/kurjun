package ai.subut.kurjun.model.repository;


/**
 *
 */
public interface ArtifactId
{
    String getName();

    void setName( String name );

    String getContext();

    void setContext( String context );

    int getType();

    void setType( int type );

    String getOwner();

    void setOwner( String owner );

    String getMd5Sum();

    void setMd5Sum( String md5Sum );
}

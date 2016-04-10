package ai.subut.kurjun.model.repository;


/**
 *
 */
public interface ArtifactId
{

    String getSearch();

    void setSearch( String search );

    String getVersion();

    void setVersion( String version );

    String getContext();

    void setContext( String context );

    int getType();

    void setType( int type );

    String getMd5Sum();

    void setMd5Sum( String md5Sum );

    String getArtifactName();

    void setArtifactName( String artifactName );

    String getUniqueId();
}

package ai.subut.kurjun.model.metadata;



/**
 *
 */
public interface RepositoryData
{

    String getContext();

    int getType();

    String getOwner();

    void setOwner( String owner );

}

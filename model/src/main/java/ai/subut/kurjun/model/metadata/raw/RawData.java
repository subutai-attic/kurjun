package ai.subut.kurjun.model.metadata.raw;


import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.ArtifactId;


/**
 *
 */
public interface RawData extends Metadata, SerializableMetadata
{

    void setId( ArtifactId id );

    String getUniqId();

    String getContext();

    int getType();

    long getUploadDate();

    void setUploadDate( long uploadDate );

    void setVersion( String version );

    long getSize();

    void setSize( long size );

    void setName( String name );

    void setOwner( String owner );
}

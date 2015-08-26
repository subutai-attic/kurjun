package ai.subut.kurjun.snap.metadata.store;


import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import ai.subut.kurjun.model.metadata.snap.SnapMetadataStore;


/**
 * Guice module to initialize snap package store bindings.
 *
 */
public class SnapMetadataStoreModule extends AbstractModule
{
    public static final String DB_FILE_PATH = "snap.metadata.store.filedb";

    private String fileDbPath;


    /**
     * Sets a file path where metadata is stored.
     *
     * @param fileDbPath file path to store metadata
     */
    public void setFileDbPath( String fileDbPath )
    {
        this.fileDbPath = fileDbPath;
    }


    @Override
    protected void configure()
    {
        if ( fileDbPath != null )
        {
            bind( String.class ).annotatedWith( Names.named( DB_FILE_PATH ) ).toInstance( fileDbPath );
        }

        bind( SnapMetadataStore.class ).to( SnapMetadataStoreImpl.class );
    }

}


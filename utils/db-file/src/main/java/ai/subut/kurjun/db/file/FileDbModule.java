package ai.subut.kurjun.db.file;


import com.google.inject.AbstractModule;


/**
 * Guice module to initialize db file bindings.
 *
 */
public class FileDbModule extends AbstractModule
{

    public static final String DB_FILE_PATH = "db.file.path";


    @Override
    protected void configure()
    {
        bind( FileDb.class );
        bind( FileDbReadOnly.class );
    }

}


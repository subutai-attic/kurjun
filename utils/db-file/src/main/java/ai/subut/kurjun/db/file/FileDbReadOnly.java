package ai.subut.kurjun.db.file;


import java.io.IOException;

import com.google.inject.Inject;
import com.google.inject.name.Named;


/**
 * Read only version of {@link FileDb}.
 *
 */
public class FileDbReadOnly extends FileDb
{

    @Inject
    public FileDbReadOnly( @Named( FileDbModule.DB_FILE_PATH ) String dbFile ) throws IOException
    {
        super( dbFile, true );
    }

}


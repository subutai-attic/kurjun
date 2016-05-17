package ai.subut.kurjun.db.file.tests;


import java.util.concurrent.Callable;

import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


/**
 * Does random writes to DB.
 */
public class DbWriter implements Callable<Result>
{
    private FileDb fileDb;


    public DbWriter( FileDb fileDb )
    {
        this.fileDb = fileDb;
    }


    @Override
    public Result call() throws Exception {
        Result result = new Result();
        long time = System.nanoTime();

        try
        {
            MetadataEnum type = MetadataEnum.getRandom();
            SerializableMetadata serializableMetadata = type.getSupplier().get();
            result.setMapName( type.getMapName() );
            fileDb.put( type.getMapName(), serializableMetadata.getId(), serializableMetadata );
        }
        catch ( Throwable t )
        {
            result.setFailure( t );
        }
        result.setTime( ( System.nanoTime() - time ) / 1000 );

        return result;
    }
}

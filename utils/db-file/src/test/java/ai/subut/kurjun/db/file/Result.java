package ai.subut.kurjun.db.file;


/**
 * Document me!
 */
public class Result
{
    private long time;
    private Throwable failure;
    private String mapName;


    public long getTime() {
        return time;
    }


    public void setTime( final long time ) {
        this.time = time;
    }


    public Throwable getFailure() {
        return failure;
    }


    public void setFailure( final Throwable failure ) {
        this.failure = failure;
    }


    public boolean isSuccess()
    {
        return this.failure == null;
    }


    public String getMapName() {
        return mapName;
    }


    public void setMapName( final String mapName ) {
        this.mapName = mapName;
    }


    public String toString()
    {
        if ( isSuccess() )
        {
            return "[SUCCESS][ " + mapName + "]: " + time;
        }

        return "[FAILURE][ " + mapName + "][" + failure.getClass().getCanonicalName() + "]: " + time;
    }
}

package ai.subut.kurjun.common.context;


import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Singleton;

import ai.subut.kurjun.model.context.ArtifactContext;
import ai.subut.kurjun.model.user.UserContext;


@Singleton
public class GlobalArtifactContext implements ArtifactContext
{

    private Map<String, UserContext> map;


    public GlobalArtifactContext()
    {
        this.map = new ConcurrentHashMap<>();
    }


    @Override
    public UserContext getRepository( final String md5 )
    {
        return map.get( md5 );
    }


    @Override
    public void store( final byte[] md5, final UserContext repository )
    {
        String hash = new BigInteger( 1, Arrays.copyOf( md5, md5.length ) ).toString( 16 );
        map.put( hash, repository );
    }


    @Override
    public void remove( final byte[] md5 )
    {

        String hash = new BigInteger( 1, Arrays.copyOf( md5, md5.length ) ).toString( 16 );
        map.remove( hash );
    }


    /*
  * The Double checked pattern is used to avoid obtaining the lock every time the code is executed,
  * if the call are not happening together then the first condition will fail and the code execution will not execute
  * the locking thus saving resources.
  * */
    //private static GlobalArtifactContext globalArtifactContext = null;
    //    public static GlobalArtifactContext getInstance()
    //    {
    //        if ( globalArtifactContext == null )
    //        {
    //            synchronized ( GlobalArtifactContext.class )
    //            {
    //                if ( globalArtifactContext == null )
    //                {
    //                    globalArtifactContext = new GlobalArtifactContext();
    //                }
    //            }
    //        }
    //        return globalArtifactContext;
    //    }
}

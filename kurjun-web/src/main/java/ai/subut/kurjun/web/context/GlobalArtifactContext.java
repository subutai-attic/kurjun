package ai.subut.kurjun.web.context;


import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.web.utils.Utils;


@Singleton
public class GlobalArtifactContext implements ArtifactContext
{

    private Map<String, KurjunContext> map;
    private Map<String, String> cacheMap;


    public GlobalArtifactContext()
    {
        this.cacheMap = new ConcurrentHashMap<>();
        this.map = new ConcurrentHashMap<>();
    }


    @Override
    public KurjunContext getRepository( final String md5 )
    {
        return map.get( md5 );
    }


    @Override
    public void store( final byte[] md5, final KurjunContext repository )
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


    @Override
    public String getMd5( final String repository )
    {
        return cacheMap.get( repository );
    }


    @Override
    public void store( final String repository, final byte[] md5 )
    {
        cacheMap.put( repository, Utils.MD5.toString( md5 ) );
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

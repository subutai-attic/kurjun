package ai.subut.kurjun.web.utils;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.io.ByteStreams;

import ninja.Context;
import ninja.Renderable;
import ninja.Result;
import ninja.utils.ResponseStreams;


public class RenderableImpl implements Renderable
{
    private Context context;
    private Result result;
    private InputStream inputStream;


    public RenderableImpl( final Context context, final Result result, final InputStream inputStream )
    {
        this.context = context;
        this.result = result;
        this.inputStream = inputStream;
    }


    @Override
    public void render( final Context context, final Result result )
    {
        ResponseStreams responseStreams = context.finalizeHeaders( result );

        try ( OutputStream outputStream = responseStreams.getOutputStream() )
        {
            ByteStreams.copy( inputStream, outputStream );
        }
        catch ( IOException e )
        {
            //throw new IOException( e );
        }
    }
}

package ai.subut.kurjun.web.controllers.torrent;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.web.service.TorrentService;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.PathParam;


@Singleton
public class TorrentController
{

    private static final Logger LOGGER = LoggerFactory.getLogger( TorrentController.class );

    @Inject
    TorrentService torrentService;


    public Result get( Context context, @PathParam( "id" ) String id )
    {


        return Results.ok();
    }


    public Result list( Context context )
    {
        return Results.ok();
    }


    public Result info( Context context, @PathParam( "id" ) String id )
    {
        return Results.ok();
    }


    public Result delete( Context context, @PathParam( "id" ) String id )
    {
        return Results.ok();
    }
}

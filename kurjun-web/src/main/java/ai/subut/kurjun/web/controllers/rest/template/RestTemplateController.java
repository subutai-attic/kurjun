package ai.subut.kurjun.web.controllers.rest.template;


import ninja.Context;
import ninja.Result;
import ninja.params.Param;
import ninja.uploads.DiskFileItemProvider;
import ninja.uploads.FileItem;
import ninja.uploads.FileProvider;


/**
 * REST Controller for Template Management
 */


public class RestTemplateController
{


    @FileProvider( DiskFileItemProvider.class )
    public Result uploadFinish( Context context, @Param( "upfile" ) FileItem upfile ) throws Exception
    {

        upfile.getInputStream();


        return null;
    }
}

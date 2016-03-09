package ai.subut.kurjun.web.controllers.rest.template;


import java.io.InputStream;

import com.google.inject.Inject;

import ai.subut.kurjun.web.service.TemplateManagerService;
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

    @Inject
    TemplateManagerService templateManagerService;


    @FileProvider( DiskFileItemProvider.class )
    public Result upload( Context context, @Param( "upfile" ) FileItem upfile ) throws Exception
    {
        byte[] buffer = new byte[8192];

        InputStream inputStream = upfile.getInputStream();

        byte bytesRead;

        return null;
    }
}

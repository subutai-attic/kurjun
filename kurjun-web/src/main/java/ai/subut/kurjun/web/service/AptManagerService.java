package ai.subut.kurjun.web.service;


import java.io.InputStream;
import java.net.URI;
import java.util.List;

import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ninja.Renderable;


public interface AptManagerService extends BaseService
{

    String md5();


    String getRelease( String release, String component, String arch );


    Renderable getPackage( UserSession userSession, String repository, String md5 );


    URI upload( UserSession userSession, String repository, InputStream is );



    List<SerializableMetadata> list( UserSession userSession, String repository, String search );


    boolean delete( UserSession userSession, String repository, String md5 );


    boolean isCompressionTypeSupported( String packagesIndex );


    String getSerializedPackageInfoByFilename( String filename ) throws IllegalArgumentException;


    String getSerializedPackageInfoByMd5( String md5 ) throws IllegalArgumentException;


    Renderable getPackagesIndex( String release, String component, String arch, String packagesIndex )

            throws IllegalArgumentException;

    Renderable  getPackageByFilename( String filename ) throws IllegalArgumentException;



    String getPackageInfo( UserSession userSession, String repository, String md5, String name, String version );

    List<String> getRepoList();

}

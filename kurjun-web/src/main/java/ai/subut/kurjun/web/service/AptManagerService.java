package ai.subut.kurjun.web.service;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ninja.Renderable;


public interface AptManagerService extends BaseService
{

    String md5();

    String getRelease( String release, String component, String arch );

    String getPackageInfo( byte[] md5, String name, String version );

    URI upload( InputStream is );

    List<SerializableMetadata> list();

    boolean delete( byte[] md5 ) throws IOException;

    boolean isCompressionTypeSupported( String packagesIndex );

    String getSerializedPackageInfo( String filename ) throws IllegalArgumentException;

    String getSerializedPackageInfo( byte[] md5 ) throws IllegalArgumentException;

    Renderable getPackagesIndex( String release, String component, String arch, String packagesIndex )
            throws IllegalArgumentException;

    Renderable  getPackageByFilename( String filename ) throws IllegalArgumentException;

    Renderable getPackage( byte[] md5 );
}

package ai.subut.kurjun.repo.http;


import ai.subut.kurjun.model.index.ChecksummedResource;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;


/**
 * Utility class to build paths for apt repository items like release index files, package index files, and artifact
 * files. Note that the result path of this class does not have slash ("/") in the beginning because the paths returned
 * are not absolute paths.
 *
 */
public class PathBuilder
{

    private boolean releaseIndexFile = false;
    private boolean releaseIndexFileSigned = false;

    private String release;
    private ChecksummedResource resource;
    private PackageMetadata packageMetaData;


    PathBuilder()
    {
    }


    public static PathBuilder instance()
    {
        return new PathBuilder();
    }


    /**
     * Indicates the path is for release index file, usually named as "Release".
     *
     * @return
     */
    public PathBuilder forReleaseIndexFile()
    {
        this.releaseIndexFile = true;
        return this;
    }


    /**
     * Indicates the path is for signed release index file, usually named as "InRelease".
     *
     * @return
     */
    public PathBuilder forReleaseIndexFileSigned()
    {
        this.releaseIndexFile = true;
        this.releaseIndexFileSigned = true;
        return this;
    }


    /**
     * Indicated the path is for release index file. A boolean argument indicates if the signed or unsigned file is
     * requested.
     *
     * @param releaseIndexFileSigned
     * @return
     */
    public PathBuilder setReleaseIndexFileSigned( boolean releaseIndexFileSigned )
    {
        return releaseIndexFileSigned ? forReleaseIndexFileSigned() : forReleaseIndexFile();
    }


    /**
     * Sets a release to build path for.
     *
     * @param releaseFile
     * @return
     */
    public PathBuilder setRelease( ReleaseFile releaseFile )
    {
        this.release = releaseFile.getCodename();
        return this;
    }


    public PathBuilder setRelease( String release )
    {
        this.release = release;
        return this;
    }


    /**
     * Sets a resource to build path for. Setting a resource throws an exception when building path for release index
     * files.
     *
     * @param resource
     * @return
     */
    public PathBuilder setResource( ChecksummedResource resource )
    {
        if ( releaseIndexFile )
        {
            throw new IllegalStateException( "Release distribution path does not need resource data" );
        }
        this.resource = resource;
        return this;
    }


    /**
     * Sets a package metadata to build path for. Setting a package metadata throws an exception when building path for
     * release index files.
     *
     * @param packageMetaData
     * @return
     */
    public PathBuilder setPackageMetaData( PackageMetadata packageMetaData )
    {
        if ( releaseIndexFile )
        {
            throw new IllegalStateException( "Release distribution path does not need package metadata" );
        }
        this.packageMetaData = packageMetaData;
        return this;
    }


    /**
     * Builds a path according to set release, package metadata, and resource items. This methods throws an exception if
     * there are no enough data.
     *
     * <p>
     * Note that the resultant path does not start with a slash ("/") because returned paths are not absolute paths
     * </p>
     *
     * @return a path to apt repository item corresponding to provided data by setters
     */
    public String build()
    {
        StringBuilder sb = new StringBuilder();
        if ( packageMetaData != null )
        {
            sb.append( packageMetaData.getFilename() );
            return sb.toString();
        }

        if ( release != null )
        {
            sb.append( "dists/" ).append( release ).append( "/" );
        }
        else
        {
            throw new IllegalStateException( "Can not build path without release info" );
        }

        if ( releaseIndexFile )
        {
            if ( releaseIndexFileSigned )
            {
                sb.append( "InRelease" );
            }
            else
            {
                sb.append( "Release" );
            }
        }
        else
        {
            if ( resource != null )
            {
                sb.append( resource.getRelativePath() );
            }
            else
            {
                throw new IllegalStateException( "No resource specified" );
            }
        }
        return sb.toString();
    }


}


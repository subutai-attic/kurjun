package ai.subut.kurjun.cfparser;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vafer.jdeb.debian.BinaryPackageControlFile;

import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.apt.Dependency;
import ai.subut.kurjun.model.metadata.apt.Priority;


/**
 * A wrapper around a BinaryPackageControlFile that implements the PkgMeta interface.
 */
class BinaryPackageMetadata extends AbstractPackageMetadata
{
    private static final Logger LOG = LoggerFactory.getLogger( BinaryPackageMetadata.class );


    BinaryPackageMetadata( String md5, String filename, BinaryPackageControlFile controlFile )
    {
        super( md5, filename, controlFile );
    }


    @Override
    public String getComponent()
    {
        String section = getSection();
        if ( section != null && section.contains( "/" ) )
        {
            return section.substring( 0, section.indexOf( "/" ) );
        }
        return "main";
    }


    @Override
    public String getPackage()
    {
        return controlFile.get( PACKAGE_FIELD );
    }


    @Override
    public String getOwner()
    {
        return null;
    }


    @Override
    public String getName()
    {
        return getPackage();
    }


    @Override
    public String getVersion()
    {
        return controlFile.get( VERSION_FIELD );
    }


    @Override
    public String getSource()
    {
        return controlFile.get( SOURCE_FIELD );
    }


    @Override
    public String getMaintainer()
    {
        return controlFile.get( MAINTAINER_FIELD );
    }


    @Override
    public Architecture getArchitecture()
    {
        return Architecture.getByValue( controlFile.get( ARCHITECTURE_FIELD ) );
    }


    @Override
    public int getInstalledSize()
    {
        return Integer.parseInt( controlFile.get( INSTALLED_SIZE_FIELD ) );
    }


    @Override
    public String getSection()
    {
        return controlFile.get( SECTION_FIELD );
    }


    @Override
    public Priority getPriority()
    {
        return Priority.valueOf( controlFile.get( PRIORITY_FIELD ) );
    }


    @Override
    public URL getHomepage()
    {
        String field = controlFile.get( HOMEPAGE_FIELD );

        if ( field == null )
        {
            return null;
        }

        try
        {
            return new URL( field );
        }
        catch ( MalformedURLException e )
        {
            LOG.error( "The URL for the homepage was malformed: {}", field );
            return null;
        }
    }


    @Override
    public String getDescription()
    {
        return ( ( BinaryPackageControlFile ) controlFile ).getShortDescription();
    }


    // ------------------------------------------------------------------------
    // Fields for Package Interrelationships
    // ------------------------------------------------------------------------
    @Override
    public List<Dependency> getDependencies()
    {
        return getCached( DEPENDS_FIELD );
    }


    // obviously source package specific
    // Build-Depends
    public List<Dependency> getBuildDependencies()
    {
        return getCached( BUILD_DEPENDS_FIELD );
    }


    // obviously source package specific
    // Build-Depends-Indep
    public List<Dependency> getIndependentBuildDependencies()
    {
        return getCached( BUILD_DEPENDS_INDEP_FIELD );
    }


    public List<Dependency> getPreDependencies()
    {
        return getCached( PRE_DEPENDS_FIELD );
    }


    @Override
    public List<Dependency> getRecommends()
    {
        return getCached( RECOMMENDS_FIELD );
    }


    @Override
    public List<Dependency> getSuggests()
    {
        return getCached( SUGGESTS_FIELD );
    }


    @Override
    public List<Dependency> getBreaks()
    {
        return getCached( BREAKS_FIELD );
    }


    @Override
    public List<Dependency> getConflicts()
    {
        return getCached( CONFLICTS_FIELD );
    }


    // only this field MUST NOT provide version information
    @Override
    public List<String> getProvides()
    {
        String provides = controlFile.get( PROVIDES_FIELD );
        if ( provides != null )
        {
            String[] parts = provides.split( "," );
            return Arrays.asList( parts );
        }
        return null;
    }


    @Override
    public List<Dependency> getReplaces()
    {
        return getCached( REPLACES_FIELD );
    }


    @Override
    public List<Dependency> getEnhances()
    {
        return getCached( ENHANCES_FIELD );
    }


    @Override
    public List<Dependency> getPreDepends()
    {
        return getCached( PRE_DEPENDS_FIELD );
    }
}


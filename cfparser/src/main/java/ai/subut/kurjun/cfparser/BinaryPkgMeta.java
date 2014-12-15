package ai.subut.kurjun.cfparser;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vafer.jdeb.debian.BinaryPackageControlFile;

import ai.subut.kurjun.model.Arch;
import ai.subut.kurjun.model.Dependency;
import ai.subut.kurjun.model.PkgMeta;
import ai.subut.kurjun.model.Priority;


/**
 * A wrapper around a BinaryPackageControlFile that implements the PkgMeta interface.
 */
class BinaryPkgMeta implements PkgMeta
{
    private static final Logger LOG = LoggerFactory.getLogger( BinaryPkgMeta.class );
    private BinaryPackageControlFile controlFile;


    BinaryPkgMeta( BinaryPackageControlFile controlFile )
    {
        this.controlFile = controlFile;
    }


    @Override
    public String getPackage()
    {
        return controlFile.get( "Package" );
    }


    @Override
    public String getVersion()
    {
        return controlFile.get( "Version" );
    }


    @Override
    public String getMaintainer()
    {
        return controlFile.get( "Maintainer" );
    }


    @Override
    public Arch getArchitecture()
    {
        return Arch.valueOf( controlFile.get( "Architecture" ) );
    }


    @Override
    public int getInstalledSize()
    {
        return Integer.parseInt( controlFile.get( "Installed-Size" ) );
    }


    @Override
    public String getSection()
    {
        return controlFile.get( "Section" );
    }


    @Override
    public Priority getPriority()
    {
        return Priority.valueOf( controlFile.get( "Priority" ) );
    }


    @Override
    public URL getHomepage()
    {
        String field = controlFile.get( "Homepage" );

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
        return controlFile.getShortDescription();
    }


    // ------------------------------------------------------------------------
    // Fields for Package Interrelationships
    // ------------------------------------------------------------------------


    @Override
    public List<Dependency> getDependencies()
    {
        return null;
    }


    // obviously source package specific
    // Build-Depends
    public List<Dependency> getBuildDependencies()
    {
        return null;
    }


    // obviously source package specific
    // Build-Depends-Indep
    public List<Dependency> getIndependentBuildDependencies()
    {
        return null;
    }


    public List<Dependency> getPreDependencies()
    {
        return null;
    }


    public List<Dependency> getRecommends()
    {
        return null;
    }


    @Override
    public List<Dependency> getSuggests()
    {
        return null;
    }


    @Override
    public List<Dependency> getBreaks()
    {
        return null;
    }


    @Override
    public List<Dependency> getConflicts()
    {
        return null;
    }


    // only this field MUST NOT provide version information
    @Override
    public List<String> getProvides()
    {
        return null;
    }


    @Override
    public List<Dependency> getReplaces()
    {
        return null;
    }


    public List<Dependency> getEnhances()
    {
        return null;
    }
}

package ai.subut.kurjun.metadata.common.subutai;


import java.util.Locale;
import java.util.Objects;


/**
 * Wrapper class of composite template id for convenience
 */
public class TemplateId
{
    private String md5;
    private String ownerFprint;
    private boolean certified;

    private static final String SEPARATOR = ".";


    public TemplateId( String ownerFprint, String md5 )
    {
        this.md5 = md5;
        this.ownerFprint = ownerFprint.toLowerCase( Locale.US );
        this.certified = false;
    }


    public String get()
    {
        return ownerFprint + SEPARATOR + md5;
    }


    public String getMd5()
    {
        return md5;
    }


    public String getOwnerFprint()
    {
        return ownerFprint;
    }


    public boolean isCertified()
    {
        return certified;
    }


    public void setCertified( boolean certified )
    {
        this.certified = certified;
    }


    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode( this.md5 ) + Objects.hashCode( this.ownerFprint );
        return hash;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof TemplateId )
        {
            final TemplateId other = ( TemplateId ) obj;

            if ( Objects.equals( this.md5, other.md5 )
                    && Objects.equals( this.ownerFprint, other.ownerFprint ) )
            {
                return true;
            }
        }
        return false;
    }
}

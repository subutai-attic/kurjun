package ai.subut.kurjun.core.dao.service.metadata;


import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.metadata.AptDAO;
import ai.subut.kurjun.core.dao.api.metadata.TemplateDAO;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;


@Singleton
public class AptDataService
{
    @Inject
    AptDAO aptDAO;

    @Inject
    public AptDataService()
    {
    }

    //*****************************
    public void persist( PackageMetadata packageMetadata )
    {
        try
        {
            if(packageMetadata != null)
                aptDAO.persist( packageMetadata );
        }
        catch ( DAOException e )
        {

        }
    }


    //*****************************
    public PackageMetadata merge( PackageMetadata packageMetadata )
    {
        try
        {
            if( packageMetadata != null )
                return aptDAO.merge( packageMetadata );
        }
        catch ( DAOException e )
        {
        }

        return null;
    }


    //*****************************
    public PackageMetadata find( String id )
    {
        try
        {
            if( id != null )
                return aptDAO.find( id );
            else
                return null;
        }
        catch ( Exception e )
        {
            return null;
        }
    }


    //*****************************
    public List<PackageMetadata> findAll()
    {
        try
        {
            return aptDAO.findAll( "AptEntity");
        }
        catch ( DAOException e )
        {
            return new ArrayList<>(  );
        }
    }


    //*****************************
    public boolean delete( PackageMetadata packageMetadata )
    {
        try
        {
            aptDAO.remove( packageMetadata );
            return true;
        }
        catch ( DAOException e )
        {
            e.printStackTrace();
            return false;
        }
    }



}

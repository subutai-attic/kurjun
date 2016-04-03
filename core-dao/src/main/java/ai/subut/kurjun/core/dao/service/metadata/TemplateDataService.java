package ai.subut.kurjun.core.dao.service.metadata;


import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.metadata.TemplateDAO;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


@Singleton
public class TemplateDataService
{
    @Inject
    TemplateDAO templateDAO;

    @Inject
    public TemplateDataService()
    {
    }

    //*****************************
    public void persist( SerializableMetadata template )
    {
        try
        {
            if(template != null)
                templateDAO.persist( template );
        }
        catch ( DAOException e )
        {

        }
    }


    //*****************************
    public SerializableMetadata merge( SerializableMetadata template )
    {
        try
        {
            if(template != null)
                return templateDAO.merge( template );
        }
        catch ( DAOException e )
        {
        }

        return null;
    }


    //*****************************
    public SerializableMetadata find( String id )
    {
        try
        {
            if( id != null )
                return templateDAO.find( id );
            else
                return null;
        }
        catch ( Exception e )
        {
            return null;
        }
    }


    //*****************************
    public List<SerializableMetadata> findAll()
    {
        try
        {
            return templateDAO.findAll( "TemplateEntity");
        }
        catch ( DAOException e )
        {
            return new ArrayList<>(  );
        }
    }


    //*****************************
    public boolean delete( SerializableMetadata metadata )
    {
        try
        {
            templateDAO.remove( metadata );
            return true;
        }
        catch ( DAOException e )
        {
            e.printStackTrace();
            return false;
        }
    }



}

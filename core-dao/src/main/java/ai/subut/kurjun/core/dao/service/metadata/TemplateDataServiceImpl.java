package ai.subut.kurjun.core.dao.service.metadata;


import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.metadata.TemplateDAO;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


@Singleton
public class TemplateDataServiceImpl implements TemplateDataService
{
    @Inject
    TemplateDAO templateDAO;

    @Inject
    public TemplateDataServiceImpl()
    {
    }

    //*****************************
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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

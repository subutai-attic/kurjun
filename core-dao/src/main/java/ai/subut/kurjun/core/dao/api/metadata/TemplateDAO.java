package ai.subut.kurjun.core.dao.api.metadata;


import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.GenericDAOImpl;
import ai.subut.kurjun.core.dao.model.metadata.TemplateDataEntity;
import ai.subut.kurjun.model.metadata.template.TemplateData;
import ai.subut.kurjun.model.repository.ArtifactId;


public class TemplateDAO extends GenericDAOImpl<TemplateData>
{
    private static final Logger LOGGER = LoggerFactory.getLogger( TemplateDAO.class );

    public TemplateDAO()
    {
        super();
    }

    public TemplateDAO(EntityManagerFactory emf)
    {
        super(emf);
    }

    //***********************************************
    public TemplateData find( ArtifactId id ) throws DAOException
    {
        try
        {
            EntityManager em = getEntityManager();
            return em.find( TemplateDataEntity.class, id );
        }
        catch ( Exception e )
        {
            LOGGER.error( "****** Error in TemplateDAO find :" + e.getMessage(), e );
            throw new DAOException( e );
        }
    }



    //***********************************************
    public List<TemplateData> findByRepository( String repoContext) throws DAOException
    {
        try
        {
            Query qr = getEntityManager().createQuery(
                    " select e from TemplateDataEntity e where e.id.context=:repoContext ",
                    TemplateDataEntity.class );
            qr.setParameter( "repoContext" ,repoContext );

            List<TemplateData>  templates = qr.getResultList();

            if(!templates.isEmpty())
                return templates;
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }

        return Collections.emptyList();

    }




    //***********************************************
    public TemplateData findByDetails( ArtifactId id ) throws DAOException
    {
        try
        {
            String querySTR = "select e from TemplateDataEntity e where (e.id.md5Sum is not null) ";

            if( !Strings.isNullOrEmpty( id.getContext() ))
                querySTR += " and e.id.context=:repoContext ";
            if( !Strings.isNullOrEmpty(id.getMd5Sum() ))
                querySTR += " and e.id.md5Sum=:md5Sum ";
            if( !Strings.isNullOrEmpty(id.getArtifactName() ))
                querySTR += " and e.name=:name ";
            if( !Strings.isNullOrEmpty(id.getVersion() ))
                querySTR += " and e.version=:version ";

            querySTR += " order by e.version ";
            Query qr = getEntityManager().createQuery(querySTR,TemplateDataEntity.class );

            if( !Strings.isNullOrEmpty(id.getContext()))
                qr.setParameter( "repoContext" ,id.getContext() );
            if( !Strings.isNullOrEmpty(id.getMd5Sum() ))
                qr.setParameter( "md5Sum" ,id.getMd5Sum() );
            if( !Strings.isNullOrEmpty(id.getArtifactName() ))
                qr.setParameter( "name" ,id.getArtifactName() );
            if( !Strings.isNullOrEmpty(id.getVersion() ))
                qr.setParameter( "version" ,id.getVersion() );

            List<TemplateData>  items = qr.getResultList();

            if(!items.isEmpty())
            {
                return items.get( 0 );
            }
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }

        return null;

    }


}

package ai.subut.kurjun.core.dao.api.metadata;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.subut.kurjun.core.dao.api.GenericDAOImpl;
import ai.subut.kurjun.model.metadata.RepositoryData;


/**
 *
 */
public class RepositoryDAO  extends GenericDAOImpl<RepositoryData>
{
    private static final Logger LOGGER = LoggerFactory.getLogger( RawDAO.class );


    public RepositoryDAO()
    {
        super();
    }
}

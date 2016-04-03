package ai.subut.kurjun.core.dao.api.raw;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.subut.kurjun.core.dao.api.GenericDAOImpl;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


public class RawDAO extends GenericDAOImpl<SerializableMetadata>
{
    private static final Logger LOGGER = LoggerFactory.getLogger( RawDAO.class );


    public RawDAO()
    {
        super();
    }
}

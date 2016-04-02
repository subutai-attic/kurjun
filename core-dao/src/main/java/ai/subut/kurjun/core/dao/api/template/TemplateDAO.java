package ai.subut.kurjun.core.dao.api.template;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.subut.kurjun.core.dao.api.GenericDAOImpl;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;


public class TemplateDAO  extends GenericDAOImpl<SubutaiTemplateMetadata>
{
    private static final Logger LOGGER = LoggerFactory.getLogger( TemplateDAO.class );

    public TemplateDAO()
    {
        super();
    }

}

package ai.subut.kurjun.core.dao.api.identity;


import javax.persistence.EntityManagerFactory;

import com.google.inject.Inject;


/**
 *
 */
public class RelationDAO
{
    private EntityManagerFactory entityManagerFactory;


    @Inject
    public RelationDAO(EntityManagerFactory entityManagerFactory)
    {
        this.entityManagerFactory = entityManagerFactory;
    }
}

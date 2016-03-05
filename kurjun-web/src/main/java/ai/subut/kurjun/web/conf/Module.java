package ai.subut.kurjun.web.conf;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.repo.RepositoryFactoryImpl;
import ai.subut.kurjun.web.service.TemplateManagerService;
import ai.subut.kurjun.web.service.impl.TemplateManagerServiceImpl;


public class Module extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( RepositoryFactory.class).to( RepositoryFactoryImpl.class );
        bind( TemplateManagerService.class ).to( TemplateManagerServiceImpl.class );

    }
}

package ai.subut.kurjun.repo;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.model.repository.LocalRepository;


public class ConfigModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( LocalRepository.class ).to( LocalRepositoryImpl.class );
    }

}


package ai.subut.kurjun.repo;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.model.repository.LocalRepository;

/**
 * Guice module to initialize repository type bindings.
 *
 */
public class RepositoryModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( LocalRepository.class ).to( LocalRepositoryImpl.class );
    }

}


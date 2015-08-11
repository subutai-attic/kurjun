package ai.subut.kurjun.repo;


import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import ai.subut.kurjun.model.repository.NonLocalRepository;
import ai.subut.kurjun.riparser.service.ReleaseIndexParser;


class NonLocalRepositoryImpl extends RepositoryBase implements NonLocalRepository
{
    private static final Logger LOGGER = LoggerFactory.getLogger( NonLocalRepositoryImpl.class );

    private ReleaseIndexParser releaseIndexParser;


    @Inject
    public NonLocalRepositoryImpl( ReleaseIndexParser releaseIndexParser )
    {
        this.releaseIndexParser = releaseIndexParser;
    }


    @Override
    public void init( URL url )
    {
        this.url = url;
    }


    @Override
    protected Logger getLogger()
    {
        return LOGGER;
    }


    @Override
    protected ReleaseIndexParser getReleaseIndexParser()
    {
        return releaseIndexParser;
    }


}


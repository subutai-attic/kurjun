package ai.subut.kurjun.repo;


import java.io.IOException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ai.subut.kurjun.model.repository.Repository;
import ai.subut.kurjun.model.repository.UnifiedRepository;


public class RepositoryHelpersTest
{

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();


    @Test
    public void testIsAptRepository() throws IOException
    {
        Repository r1 = new LocalAptRepository( null, null, null, null, null );
        Assert.assertTrue( RepositoryHelpers.isAptRepository( r1 ) );

        Repository r2 = new LocalAptRepositoryWrapper( null, temporaryFolder.newFolder().toString() );
        Assert.assertTrue( RepositoryHelpers.isAptRepository( r2 ) );

        //Repository r3 = new RemoteAptRepository( new URL( "http://localhost:8080/vapt" ), null );
        //Assert.assertTrue( RepositoryHelpers.isAptRepository( r3 ) );

        Repository r4 = new LocalSnapRepository( null, null, null, null );
        Assert.assertFalse( RepositoryHelpers.isAptRepository( r4 ) );

        Repository r5 = new LocalTemplateRepository( null, null, null, null );
        Assert.assertFalse( RepositoryHelpers.isAptRepository( r5 ) );

        // unified
        UnifiedRepository uni = new UnifiedRepositoryImpl();
        uni.getRepositories().add( r1 );
        uni.getRepositories().add( r2 );
        //uni.getRepositories().add( r3 );
        Assert.assertTrue( RepositoryHelpers.isAptRepository( uni ) );

        uni.getRepositories().add( r4 );
        Assert.assertFalse( RepositoryHelpers.isAptRepository( uni ) );
    }
}


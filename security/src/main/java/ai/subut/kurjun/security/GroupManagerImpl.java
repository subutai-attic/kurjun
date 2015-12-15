package ai.subut.kurjun.security;


import java.io.IOException;

import com.google.inject.Inject;

import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.model.security.Group;
import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.security.service.FileDbProvider;
import ai.subut.kurjun.security.service.GroupManager;


class GroupManagerImpl implements GroupManager
{
    private static final String MAP_NAME = "groups";

    private FileDbProvider fileDbProvider;


    @Inject
    public GroupManagerImpl( FileDbProvider fileDbProvider )
    {
        this.fileDbProvider = fileDbProvider;
    }


    @Override
    public Group getGroup( String name ) throws IOException
    {
        try ( FileDb fileDb = fileDbProvider.get() )
        {
            return fileDb.get( MAP_NAME, name, DefaultGroup.class );
        }
    }


    @Override
    public void addGroup( Group group ) throws IOException
    {
        try ( FileDb fileDb = fileDbProvider.get() )
        {
            fileDb.put( MAP_NAME, group.getName(), group );
        }
    }


    @Override
    public boolean removeGroup( Group group ) throws IOException
    {
        try ( FileDb fileDb = fileDbProvider.get() )
        {
            return fileDb.remove( MAP_NAME, group.getName() ) != null;
        }
    }


    @Override
    public Group addIdentity( Identity identity, String groupName ) throws IOException
    {
        try ( FileDb fileDb = fileDbProvider.get() )
        {
            DefaultGroup group = fileDb.get( MAP_NAME, groupName, DefaultGroup.class );
            if ( group == null )
            {
                throw new IOException( "Group not found: " + groupName );
            }
            if ( group.getByFingerprint( identity.getKeyFingerprint() ) == null )
            {
                group.addIdentity( identity );
                fileDb.put( MAP_NAME, group.getName(), group );
            }
            return group;
        }
    }


    @Override
    public DefaultGroup removeIdentity( Identity identity, String groupName ) throws IOException
    {
        try ( FileDb fileDb = fileDbProvider.get() )
        {
            DefaultGroup group = fileDb.get( MAP_NAME, groupName, DefaultGroup.class );
            if ( group == null )
            {
                throw new IOException( "Group not found: " + groupName );
            }
            if ( group.getByFingerprint( identity.getKeyFingerprint() ) != null )
            {
                group.removeIdentity( identity );
                fileDb.put( MAP_NAME, group.getName(), group );
            }
            return group;
        }
    }

}


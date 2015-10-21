package ai.subut.kurjun.security;


import java.io.IOException;

import com.google.inject.Inject;

import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.model.security.Role;
import ai.subut.kurjun.security.service.FileDbProvider;
import ai.subut.kurjun.security.service.RoleManager;


class RoleManagerImpl implements RoleManager
{
    private static final String MAP_NAME = "roles";

    private FileDbProvider fileDbProvider;


    @Inject
    public RoleManagerImpl( FileDbProvider fileDbProvider )
    {
        this.fileDbProvider = fileDbProvider;
    }


    @Override
    public Role getRole( String name ) throws IOException
    {
        try ( FileDb fileDb = fileDbProvider.get() )
        {
            return fileDb.get( MAP_NAME, name, RoleImpl.class );
        }
    }


    @Override
    public void addRole( Role role ) throws IOException
    {
        try ( FileDb fileDb = fileDbProvider.get() )
        {
            fileDb.put( MAP_NAME, role.getName(), role );
        }
    }


    @Override
    public boolean removeRole( Role role ) throws IOException
    {
        try ( FileDb fileDb = fileDbProvider.get() )
        {
            return fileDb.remove( MAP_NAME, role.getName() ) != null;
        }
    }

}


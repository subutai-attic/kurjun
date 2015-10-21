package ai.subut.kurjun.security;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.model.security.Group;
import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.model.security.Role;
import ai.subut.kurjun.security.service.FileDbProvider;
import ai.subut.kurjun.security.service.GroupManager;
import ai.subut.kurjun.security.service.IdentityManager;
import ai.subut.kurjun.security.service.PgpKeyFetcher;
import ai.subut.kurjun.security.service.RoleManager;
import ai.subut.kurjun.security.utils.PGPUtils;


public class IdentityManagerImpl implements IdentityManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger( IdentityManagerImpl.class );

    private static final String MAP_NAME = "identities";
    private static final String GROUPS_MAP_NAME = "identity-groups";
    private static final String ROLES_MAP_NAME = "identity-roles";

    private FileDbProvider fileDbProvider;
    private PgpKeyFetcher keyFetcher;
    private GroupManager groupManager;
    private RoleManager roleManager;


    @Inject
    public IdentityManagerImpl( FileDbProvider fileDbProvider,
                                PgpKeyFetcher keyFetcher,
                                GroupManager groupManager,
                                RoleManager roleManager )
    {
        this.fileDbProvider = fileDbProvider;
        this.keyFetcher = keyFetcher;
        this.groupManager = groupManager;
        this.roleManager = roleManager;
    }


    @Override
    public Identity getIdentity( String fingerprint ) throws IOException
    {
        try ( FileDb fileDb = fileDbProvider.get() )
        {
            return fileDb.get( MAP_NAME, fingerprint, DefaultIdentity.class );
        }
    }


    @Override
    public Identity addIdentity( String fingerprint, String signedFingerprint ) throws IOException
    {
        PGPPublicKey key = keyFetcher.get( fingerprint );
        if ( key == null )
        {
            LOGGER.info( "Key not found for fingerprint: {}", fingerprint );
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream signed = new ByteArrayInputStream( signedFingerprint.getBytes() );
        InputStream keyStream = new ByteArrayInputStream( key.getEncoded() );
        if ( PGPUtils.verifyData( signed, keyStream, out ) && fingerprint.equalsIgnoreCase( out.toString().trim() ) )
        {
            Identity id = new DefaultIdentity( key );
            try ( FileDb fileDb = fileDbProvider.get() )
            {
                fileDb.put( MAP_NAME, fingerprint, id );
            }
            return id;
        }
        else
        {
            LOGGER.info( "Signed data not verified" );
            return null;
        }
    }


    @Override
    public Set<Group> getGroups( Identity identity ) throws IOException
    {
        try ( FileDb fileDb = fileDbProvider.get() )
        {
            // get group names the identity belongs to
            Set items = fileDb.get( GROUPS_MAP_NAME, identity.getKeyFingerprint(), Set.class );
            if ( items == null )
            {
                return Collections.emptySet();
            }

            Set<Group> groups = new HashSet<>();
            for ( Object item : items )
            {
                Group g = groupManager.getGroup( item.toString() );
                if ( g != null )
                {
                    groups.add( g );
                }
            }
            return groups;
        }
    }


    @Override
    public Set<Role> getRoles( Identity identity ) throws IOException
    {
        try ( FileDb fileDb = fileDbProvider.get() )
        {
            // get role names the identity belongs to
            Set items = fileDb.get( ROLES_MAP_NAME, identity.getKeyFingerprint(), Set.class );
            if ( items == null )
            {
                return Collections.emptySet();
            }

            Set<Role> roles = new HashSet<>();
            for ( Object item : items )
            {
                Role r = roleManager.getRole( item.toString() );
                if ( r != null )
                {
                    roles.add( r );
                }
            }
            return roles;
        }
    }


    @Override
    public void addRole( Role role, Identity identity ) throws IOException
    {
        try ( FileDb fileDb = fileDbProvider.get() )
        {
            // get role names the identity belongs to
            Set items = fileDb.get( ROLES_MAP_NAME, identity.getKeyFingerprint(), Set.class );
            if ( items == null )
            {
                items = new HashSet();
            }
            if ( items.add( role.getName() ) )
            {
                fileDb.put( ROLES_MAP_NAME, identity.getKeyFingerprint(), items );
            }
        }
    }


    @Override
    public void removeRole( Role role, Identity identity ) throws IOException
    {
        try ( FileDb fileDb = fileDbProvider.get() )
        {
            // get role names the identity belongs to
            Set items = fileDb.get( ROLES_MAP_NAME, identity.getKeyFingerprint(), Set.class );
            if ( items != null && items.remove( role.getName() ) )
            {
                fileDb.put( ROLES_MAP_NAME, identity.getKeyFingerprint(), items );
            }
        }
    }

}


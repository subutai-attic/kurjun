package ai.subut.kurjun.security;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.model.security.Group;
import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.model.security.Permission;
import ai.subut.kurjun.security.service.FileDbProvider;
import ai.subut.kurjun.security.service.GroupManager;
import ai.subut.kurjun.security.service.IdentityManager;
import ai.subut.kurjun.security.service.PgpKeyFetcher;
import ai.subut.kurjun.security.service.RoleManager;
import ai.subut.kurjun.security.utils.PGPUtils;
import com.google.common.collect.Sets;


public class IdentityManagerImpl implements IdentityManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger( IdentityManagerImpl.class );

    private static final String MAP_NAME = "identities";
    private static final String GROUPS_MAP_NAME = "identity-groups";
    private static final String PERMISSIONS_MAP_NAME = "identity-permissions";

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
            return fileDb.get( MAP_NAME, fingerprint.toLowerCase(), DefaultIdentity.class );
        }
    }


    @Override
    public Identity addIdentity( String fingerprint, boolean checkKeyExistence ) throws IOException
    {
        Identity id;

        if ( checkKeyExistence )
        {
            PGPPublicKey key = keyFetcher.get( fingerprint );
            if ( key == null )
            {
                LOGGER.info( "Key not found for fingerprint: {}", fingerprint );
                return null;
            }
            id = new DefaultIdentity( key );
        }
        else
        {
            id = new DefaultIdentity( fingerprint );
        }

        try ( FileDb fileDb = fileDbProvider.get() )
        {
            fileDb.put( MAP_NAME, id.getKeyFingerprint().toLowerCase(), id );
        }
        return id;
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
                fileDb.put( MAP_NAME, id.getKeyFingerprint(), id );
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
    public Set<Permission> getPermissions( Identity identity, String resource ) throws IOException
    {
        try ( FileDb fileDb = fileDbProvider.get() )
        {
            Set<ResourceControl> controls = fileDb.get( PERMISSIONS_MAP_NAME, identity.getKeyFingerprint(), Set.class );
            if ( controls == null )
            {
                return Collections.emptySet();
            }

            ResourceControl control = findResourceControl( resource, controls );
            if ( control == null )
            {
                return Collections.emptySet();
            }

            return Sets.immutableEnumSet( control.permissions );
        }
    }


    @Override
    public void addResourcePermission( Permission permission, Identity identity, String resource ) throws IOException
    {
        try ( FileDb fileDb = fileDbProvider.get() )
        {
            Set<ResourceControl> controls = fileDb.get( PERMISSIONS_MAP_NAME, identity.getKeyFingerprint(), Set.class );
            if ( controls == null )
            {
                controls = new HashSet();
            }

            ResourceControl control = findResourceControl( resource, controls );
            if ( control == null )
            {
                control = new ResourceControl( resource );
                controls.add( control );
            }
            control.permissions.add( permission );
            fileDb.put( PERMISSIONS_MAP_NAME, identity.getKeyFingerprint(), controls );
        }
    }


    @Override
    public void removeResourcePermission( Permission permission, Identity identity, String resource ) throws IOException
    {
        try ( FileDb fileDb = fileDbProvider.get() )
        {
            Set<ResourceControl> controls = fileDb.get( PERMISSIONS_MAP_NAME, identity.getKeyFingerprint(), Set.class );
            if ( controls != null )
            {
                ResourceControl control = findResourceControl( resource, controls );
                if ( control != null && control.permissions.remove( permission ) )
                {
                    fileDb.put( PERMISSIONS_MAP_NAME, identity.getKeyFingerprint(), controls );
                }
            }
        }
    }


    private ResourceControl findResourceControl( String resource, Set<ResourceControl> controls )
    {
        for ( ResourceControl control : controls )
        {
            if ( control.resource.equals( resource  ) )
            {
                return control;
            }
        }
        return null;
    }


    private static class ResourceControl implements Serializable
    {
        private String resource;
        private Set<Permission> permissions;


        public ResourceControl( String resource )
        {
            this.resource = resource;
            this.permissions = new HashSet<>();
        }


        @Override
        public int hashCode()
        {
            int hash = 7;
            hash = 29 * hash + Objects.hashCode( this.resource );
            return hash;
        }


        @Override
        public boolean equals( Object obj )
        {
            if ( obj instanceof ResourceControl )
            {
                return Objects.equals( this.resource, ( ( ResourceControl ) obj ).resource );
            }
            return false;
        }
    }


}


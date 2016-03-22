package ai.subut.kurjun.metadata.common.utils;


import java.util.Locale;

import org.apache.commons.codec.binary.Hex;

import ai.subut.kurjun.metadata.common.subutai.TemplateId;


/**
 * Custom package identifier validators
 */
public class IdValidators
{

    public static class Template
    {

        public static final String SEPARATOR = "\\.";


        public static TemplateId validate( String owner, byte[] md5 ) throws IllegalArgumentException
        {
            return validate( owner, Hex.encodeHexString( md5 ) );
        }


        public static TemplateId validate( String ownerFprint, String md5 ) throws IllegalArgumentException
        {
            String owner = ownerFprint.toLowerCase( Locale.US );
            
            if ( owner == null || owner.trim().isEmpty() )
            {
                throw new IllegalArgumentException( "Invalid template id. Owner fingerprint is empty" );
            }

            if ( md5 == null || md5.trim().isEmpty() )
            {
                throw new IllegalArgumentException( "Invalid template id. Md5 checksum is empty" );
            }

            return new TemplateId( owner, md5 );
        }


        public static TemplateId validate( String ownerMd5Combined ) throws IllegalArgumentException
        {
            if ( ownerMd5Combined == null || ownerMd5Combined.trim().isEmpty() )
            {
                throw new IllegalArgumentException( "Invalid empty template id" );
            }

            String[] arr = ownerMd5Combined.trim().split( SEPARATOR );

            if ( arr.length != 2 )
            {
                throw new IllegalArgumentException( "Invalid template id: " + ownerMd5Combined );
            }

            return validate( arr[0], arr[1] );
        }
    }

}

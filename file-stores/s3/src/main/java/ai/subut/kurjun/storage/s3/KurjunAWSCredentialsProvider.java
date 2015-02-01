/*
 * Copyright 2015 azilet.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.subut.kurjun.storage.s3;


import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;


public class KurjunAWSCredentialsProvider implements AWSCredentialsProvider
{
    private static final Logger LOGGER = LoggerFactory.getLogger( KurjunAWSCredentialsProvider.class );


    @Override
    public AWSCredentials getCredentials()
    {
        try
        {
            InputStream is = getClass().getClassLoader().getResourceAsStream( "kurjun-aws.properties" );
            return new PropertiesCredentials( is );
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to read properties file", ex );
        }
        catch ( IllegalArgumentException ex )
        {
            LOGGER.error( "Invalid properties file", ex );
        }
        return null;
    }


    @Override
    public void refresh()
    {
        // no-op. static credentials are used
    }

}


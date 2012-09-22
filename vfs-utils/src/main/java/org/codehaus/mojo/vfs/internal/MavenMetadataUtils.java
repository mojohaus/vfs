package org.codehaus.mojo.vfs.internal;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Writer;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class MavenMetadataUtils
{

    private static final String MD5 = "md5";

    private static final String SHA1 = "sha1";

    /**
     * Merge toFile into fromeFile and write back to toFile
     * 
     * @param fromFile
     * @param toFile
     * @throws IOException
     * @throws XmlPullParserException markit public internally so that we can write more test.
     * 
     *             Note: expose internally for testing purpose
     */
    public void merge( File fromFile, File toFile )
        throws IOException, XmlPullParserException
    {

        Metadata fromMetadata = readMetadata( fromFile );
        Metadata toMetadata = readMetadata( toFile );

        // Merge and write back to staged metadata to replace the remote one
        fromMetadata.merge( toMetadata );

        writeMetadata( toFile, fromMetadata );

        try
        {
            File newMd5 = new File( toFile.getAbsolutePath() + ".md5" );
            FileUtils.fileWrite( newMd5, checksum( toFile, MD5 ) );

            File newSha1 = new File( toFile.getAbsolutePath() + ".sha1" );
            FileUtils.fileWrite( newSha1, checksum( toFile, SHA1 ) );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new RuntimeException( e );
        }

    }

    private void writeMetadata( File toFile, Metadata metadata )
        throws IOException, XmlPullParserException
    {

        MetadataXpp3Writer xppWriter = new MetadataXpp3Writer();
        Writer writer = null;
        try
        {
            writer = new FileWriter( toFile );
            xppWriter.write( writer, metadata );
        }
        finally
        {
            IOUtil.close( writer );
        }
    }

    private Metadata readMetadata( File metadataFile )
        throws IOException, XmlPullParserException
    {

        MetadataXpp3Reader xppReader = new MetadataXpp3Reader();

        // Existing Metadata in target stage
        Metadata metadata = new Metadata();
        if ( metadataFile.exists() )
        {
            Reader fromMetadataReader = null;

            try
            {
                fromMetadataReader = new FileReader( metadataFile );
                metadata = xppReader.read( fromMetadataReader );
            }
            finally
            {
                IOUtil.close( fromMetadataReader );
            }
        }

        return metadata;
    }

    private String checksum( File file, String type )
        throws IOException, NoSuchAlgorithmException
    {

        MessageDigest digest = MessageDigest.getInstance( type );

        InputStream is = new FileInputStream( file );

        byte[] buf = new byte[8192];

        int i;

        while ( ( i = is.read( buf ) ) > 0 )
        {
            digest.update( buf, 0, i );
        }

        IOUtil.close( is );

        return encode( digest.digest() );
    }

    private String encode( byte[] binaryData )
    {

        if ( binaryData.length != 16 && binaryData.length != 20 )
        {
            int bitLength = binaryData.length * 8;
            throw new IllegalArgumentException( "Unrecognized length for binary data: " + bitLength + " bits" );
        }

        String retValue = "";

        for ( int i = 0; i < binaryData.length; i++ )
        {
            String t = Integer.toHexString( binaryData[i] & 0xff );

            if ( t.length() == 1 )
            {
                retValue += ( "0" + t );
            }
            else
            {
                retValue += t;
            }
        }

        return retValue.trim();
    }
}

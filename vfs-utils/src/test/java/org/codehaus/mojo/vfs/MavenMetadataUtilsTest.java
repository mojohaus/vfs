package org.codehaus.mojo.vfs;

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
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.codehaus.mojo.vfs.internal.MavenMetadataUtils;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MavenMetadataUtilsTest
    extends AbstractVfsTestCase
{
    private MavenMetadataUtils mavenMetadata = new MavenMetadataUtils();

    private File workingDir = new File( builddir, "test-metadata" );

    @Before
    public void beforeTest()
    {
        workingDir.mkdirs();
    }

    @Test
    public void testMergeNewArtifact()
        throws Exception
    {
        File srcFile = new File( basedir, "src/test/data/metadata/maven-metadata-1.xml" );
        File finalFile = new File( workingDir, "maven-metadata.xml" );
        finalFile.delete();

        mavenMetadata.merge( srcFile, finalFile );

        Metadata expectedMetadata = readMetadata( new File( basedir, "src/test/data/metadata/maven-metadata-1.xml" ) );
        Metadata resultMetadata = readMetadata( finalFile );
        Assert.assertEquals( expectedMetadata.getVersioning().getVersions(), resultMetadata.getVersioning()
            .getVersions() );
        Assert.assertEquals( expectedMetadata.getArtifactId(), resultMetadata.getArtifactId() );

        Assert.assertTrue( new File( finalFile.getAbsolutePath() + ".md5" ).exists() );
        Assert.assertTrue( new File( finalFile.getAbsolutePath() + ".sha1" ).exists() );
    }

    @Test
    public void testMerge2ExistingMetadataFiles()
        throws Exception
    {
        File finalFile = new File( workingDir, "maven-metadata.xml" );
        finalFile.delete();
        FileUtils.copyFile( new File( basedir, "src/test/data/metadata/maven-metadata-3.xml" ), finalFile );

        mavenMetadata.merge( new File( basedir, "src/test/data/metadata/maven-metadata-1-2.xml" ), finalFile );

        Metadata expectedMetadata = readMetadata( new File( basedir, "src/test/data/metadata/maven-metadata-1-2-3.xml" ) );
        Metadata resultMetadata = readMetadata( finalFile );
        Assert.assertEquals( expectedMetadata.getVersioning().getVersions(), resultMetadata.getVersioning()
            .getVersions() );

        FileUtils.copyFile( new File( basedir, "src/test/data/metadata/maven-metadata-1-2.xml" ), finalFile );

        mavenMetadata.merge( new File( basedir, "src/test/data/metadata/maven-metadata-3.xml" ), finalFile );

        expectedMetadata = readMetadata( new File( basedir, "src/test/data/metadata/maven-metadata-1-2-3.xml" ) );
        resultMetadata = readMetadata( finalFile );
        // order matter
        Assert.assertFalse( expectedMetadata.getVersioning().getVersions()
            .equals( resultMetadata.getVersioning().getVersions() ) );

        Assert.assertTrue( new File( finalFile.getAbsolutePath() + ".md5" ).exists() );
        Assert.assertTrue( new File( finalFile.getAbsolutePath() + ".sha1" ).exists() );

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

}

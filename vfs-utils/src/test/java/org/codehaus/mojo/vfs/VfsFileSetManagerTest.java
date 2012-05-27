package org.codehaus.mojo.vfs;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.codehaus.mojo.vfs.internal.DefaultVfsFileSetManager;
import org.junit.Before;
import org.junit.Test;

public class VfsFileSetManagerTest
    extends AbstractVfsTestCase
{

    private VfsFileSetManager fileSetManager = new DefaultVfsFileSetManager();

    @Before
    public void beforeTest()
    {
    }

    @Test
    public void testLocalFileListWithIncludes()
        throws Exception
    {
        String url = "file://" + basedir.getAbsolutePath();

        FileSystemManager fsManager = VFS.getManager();
        FileObject startDirectory = fsManager.resolveFile( url );

        VfsFileSet fileSet = new VfsFileSet();
        fileSet.setSource( startDirectory );
        String[] includes = { "**/pom.xml" };
        fileSet.setIncludes( includes );

        fileSetManager = new DefaultVfsFileSetManager();
        List<FileObject> fos = fileSetManager.list( fileSet );
        Assert.assertTrue( fos.size() == 1 );

        fileSet.setExcludes( includes );
        fos = fileSetManager.list( fileSet );
        Assert.assertTrue( fos.size() == 0 );
        fileSet.setExcludes( null );

        includes[0] = "pom.xml";
        fos = fileSetManager.list( fileSet );
        Assert.assertEquals( 1, fos.size() );

        includes[0] = "**/V*FileSetManager.j*va";
        fos = fileSetManager.list( fileSet );
        Assert.assertEquals( 1, fos.size() );

        includes[0] = "V*UtilsTest.j*va";
        fos = fileSetManager.list( fileSet );
        Assert.assertTrue( fos.size() == 0 );

    }

    @Test
    public void testLocalFileListWithExcludes()
        throws Exception
    {
        String url = "file://" + basedir.getAbsolutePath();

        FileSystemManager fsManager = VFS.getManager();
        FileObject startDirectory = fsManager.resolveFile( url );

        VfsFileSet fileSet = new VfsFileSet();
        fileSet.setSource( startDirectory );
        String[] excludes = { "**/target/", "**/src/" };
        fileSet.setExcludes( excludes );

        List<FileObject> fos = fileSetManager.list( fileSet );
        Assert.assertTrue( fos.size() > 0 );
        for ( FileObject fo : fos )
        {
            Assert.assertFalse( fo.getName().getPath().contains( "/target/" ) );
            Assert.assertFalse( fo.getName().getPath().contains( "/src/" ) );
        }

    }

    @Test
    public void testCopyDelete()
        throws Exception
    {
        File expectedFile = new File( builddir, "test-copy-delete/pom.xml" );
        expectedFile.delete();
        Assert.assertFalse( "Expected copied file found after delete. ", expectedFile.exists() );

        FileSystemManager fsManager = VFS.getManager();

        FileObject fromDir = fsManager.resolveFile( "file://" + basedir.getAbsolutePath() );
        FileObject toDir = fsManager.resolveFile( "file://" + builddir.getAbsolutePath() + "/test-copy-delete" );

        VfsFileSet fileSet = new VfsFileSet();
        fileSet.setSource( fromDir );
        fileSet.setDestination( toDir );
        String[] includes = { "pom.xml" };
        fileSet.setIncludes( includes );

        fileSetManager.copy( fileSet );

        Assert.assertTrue( "Expected copied file not found. ", expectedFile.exists() );

        fileSet.setSource( toDir );
        fileSetManager.delete( fileSet );
        Assert.assertFalse( "Expected copied file found after delete. ", expectedFile.exists() );

    }

}

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

import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.Selectors;
import org.codehaus.mojo.vfs.VfsFileSet;
import org.codehaus.mojo.vfs.VfsFileSetManager;
import org.codehaus.mojo.vfs.VfsUtils;

public class DefaultVfsFileSetManager
    implements VfsFileSetManager
{

    public List<FileObject> list( VfsFileSet fileSet )
        throws FileSystemException
    {
        VfsDirectoryScanner scanner = createScanner( fileSet );
        return scanner.scan();
    }

    public void delete( VfsFileSet fileSet )
        throws FileSystemException
    {
        VfsDirectoryScanner scanner = createScanner( fileSet );
        List<FileObject> fos = scanner.scan();
        for ( FileObject fo : fos )
        {
            // TODO check for return error
            fo.delete();
        }

    }

    public void move( VfsFileSet fileSet )
        throws FileSystemException
    {
        VfsDirectoryScanner scanner = createScanner( fileSet );
        List<FileObject> fos = scanner.scan();

        // better to use FileObject.move()??
        copy( fileSet.getSource(), fileSet.getDestination(), fos, fileSet.isOverwrite() );

        for ( FileObject fo : fos )
        {
            fo.delete();
        }

    }

    public void copy( VfsFileSet fileSet )
        throws FileSystemException
    {
        if ( copySingle( fileSet ) )
        {
            return;
        }

        VfsDirectoryScanner scanner = createScanner( fileSet );
        List<FileObject> fos = scanner.scan();

        copy( fileSet.getSource(), fileSet.getDestination(), fos, fileSet.isOverwrite() );
    }

    /**
     * Workaround to support single copy for those vfs protocol not support directory listing
     * 
     * @param fileset
     * @return
     * @throws FileSystemException
     */
    private boolean copySingle( VfsFileSet fileset )
        throws FileSystemException
    {

        if ( fileset.getExcludes() != null && fileset.getExcludes().length != 0 )
        {
            return false;
        }

        for ( String include : fileset.getIncludes() )
        {
            if ( include.contains( "*" ) || include.contains( "?" ) )
            {
                return false;
            }
        }

        for ( String include : fileset.getIncludes() )
        {
            if ( !include.contains( "*" ) && !include.contains( "?" ) )
            {
                FileObject fo = fileset.getDestination().resolveFile( include );
                FileObject fromFile = fileset.getSource().resolveFile( include );

                copyFile( fromFile, fo, fileset.isOverwrite() );
            }
        }

        return true;
    }

    private void copy( FileObject fromDir, FileObject toDir, List<FileObject> fromFiles, boolean overwrite )
        throws FileSystemException
    {
        for ( FileObject fromFile : fromFiles )
        {
            String relPath = VfsUtils.getRelativePath( fromDir, fromFile );
            FileObject toFile = toDir.resolveFile( relPath );

            copyFile( fromFile, toFile, overwrite );
        }
    }

    private void copyFile( FileObject fromFile, FileObject toFile, boolean overwrite )
        throws FileSystemException
    {
        if ( overwrite || !toFile.exists()
            || fromFile.getContent().getLastModifiedTime() > toFile.getContent().getLastModifiedTime() )
        {
            toFile.copyFrom( fromFile, Selectors.SELECT_ALL );
        }
    }

    private static VfsDirectoryScanner createScanner( VfsFileSet fileSet )
    {

        VfsDirectoryScanner scanner = new DefaultVfsDirectoryScanner();
        scanner.setIncludes( fileSet.getIncludes() );
        scanner.setExcludes( fileSet.getExcludes() );
        scanner.setStartingDirectory( fileSet.getSource() );
        scanner.setCaseSensitive( fileSet.isCaseSensitive() );

        return scanner;
    }

}

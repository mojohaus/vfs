package org.codehaus.mojo.vfs.internal;

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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.codehaus.mojo.vfs.VfsDirectoryScanner;

public class DefaultVfsDirectoryScanner
    implements VfsDirectoryScanner
{

    /////////////////////////////////////////////////////////////////////////////
    //  Configurations
    /////////////////////////////////////////////////////////////////////////////

    /**
     * The starting directory under VFS
     */
    private FileObject startingDirectory;

    /* (non-Javadoc)
     * @see org.codehaus.mojo.vfs.DirectoryScanner#setStartingDirectory(org.apache.commons.vfs2.FileObject)
     */
    public void setStartingDirectory( FileObject directory )
    {
        this.startingDirectory = directory;
    }

    /////////////////////////////////////////////////////////////////////////////

    /**
     * Patterns which should be excluded by default.
     * 
     * @see #addDefaultExcludes()
     */
    private String[] DEFAULTEXCLUDES = {};

    /* (non-Javadoc)
     * @see org.codehaus.mojo.vfs.DirectoryScanner#addDefaultExcludes()
     */
    public void addDefaultExcludes()
    {
        int excludesLength = excludes == null ? 0 : excludes.length;
        String[] newExcludes;
        newExcludes = new String[excludesLength + DEFAULTEXCLUDES.length];
        if ( excludesLength > 0 )
        {
            System.arraycopy( excludes, 0, newExcludes, 0, excludesLength );
        }
        for ( int i = 0; i < DEFAULTEXCLUDES.length; i++ )
        {
            newExcludes[i + excludesLength] = DEFAULTEXCLUDES[i];
        }
        excludes = newExcludes;
    }

    /////////////////////////////////////////////////////////////////////////////

    /** The patterns for the remote files to be included. */
    private String[] includes;

    /* (non-Javadoc)
     * @see org.codehaus.mojo.vfs.DirectoryScanner#setIncludes(java.lang.String[])
     */
    public void setIncludes( String[] includes )
    {
        if ( includes == null )
        {
            this.includes = null;
        }
        else
        {
            this.includes = new String[includes.length];
            for ( int i = 0; i < includes.length; i++ )
            {
                String pattern = includes[i].trim();

                if ( pattern.endsWith( "/" ) )
                {
                    pattern += "**";
                }
                this.includes[i] = pattern;
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////

    /** The patterns for the remote files to be excluded. */
    private String[] excludes;

    /* (non-Javadoc)
     * @see org.codehaus.mojo.vfs.DirectoryScanner#setExcludes(java.lang.String[])
     */
    public void setExcludes( String[] excludes )
    {
        if ( excludes == null )
        {
            this.excludes = null;
        }
        else
        {
            this.excludes = new String[excludes.length];
            for ( int i = 0; i < excludes.length; i++ )
            {
                String pattern = excludes[i].trim();

                if ( pattern.endsWith( "/" ) )
                {
                    pattern += "**";
                }
                this.excludes[i] = pattern;
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////

    /**
     * Whether or not the file system should be treated as a case sensitive one.
     */
    private boolean isCaseSensitive = true;

    /* (non-Javadoc)
     * @see org.codehaus.mojo.vfs.DirectoryScanner#setCaseSensitive(boolean)
     */
    public void setCaseSensitive( boolean isCaseSensitive )
    {
        this.isCaseSensitive = isCaseSensitive;
    }

    /////////////////////////////////////////////////////////////////////////////
    //  Outputs
    /////////////////////////////////////////////////////////////////////////////

    /**
     * The files which matched at least one include and at least one exclude and relative to
     * directory
     */
    private List<FileObject> includedFiles = new ArrayList<FileObject>();

    /* (non-Javadoc)
     * @see org.codehaus.mojo.vfs.DirectoryScanner#getFilesIncluded()
     */
    public List<FileObject> getFilesIncluded()
    {
        return includedFiles;
    }

    /////////////////////////////////////////////////////////////////////////////
    //  public interface
    /////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.codehaus.mojo.vfs.DirectoryScanner#scan()
     */
    public List<FileObject> scan()
        throws FileSystemException
    {
        if ( startingDirectory == null )
        {
            throw new IllegalStateException( "Starting directory is not set" );
        }

        if ( includes == null )
        {
            // No includes supplied, so set it to 'matches all'
            includes = new String[1];
            includes[0] = "**";
        }

        if ( excludes == null )
        {
            excludes = new String[0];
        }

        includedFiles = new ArrayList<FileObject>();

        scanSubDir( startingDirectory, "" );

        return includedFiles;

    }

    /**
     * Scans the given directory for files and directories. Found files are placed in a collection,
     * based on the matching of includes, excludes, and the selectors. When a directory is found, it
     * is scanned recursively.
     * 
     * @throws FileSystemException
     * 
     * @see #includedFiles
     */
    private void scanSubDir( FileObject dir, String relativePath )
        throws FileSystemException
    {

        FileObject[] children = dir.getChildren();

        for ( FileObject child : children )
        {
            String newRelativePath = child.getName().getBaseName();
            if ( !relativePath.isEmpty() )
            {
                newRelativePath = relativePath + "/" + child.getName().getBaseName();
            }

            if ( !this.isDirectory( child ) )
            {

                if ( isIncluded( newRelativePath ) )
                {
                    if ( !isExcluded( newRelativePath ) )
                    {
                        includedFiles.add( child );
                    }
                }
                continue;
            }

            if ( isIncluded( newRelativePath ) )
            {
                if ( !isExcluded( newRelativePath ) )
                {
                    scanSubDir( child, newRelativePath );
                }
                else
                {
                    if ( couldHoldIncluded( newRelativePath ) )
                    {
                        scanSubDir( child, newRelativePath );
                    }
                }
            }
            else
            {
                if ( couldHoldIncluded( newRelativePath ) )
                {
                    scanSubDir( child, newRelativePath );
                }
            }

        }
    }

    private boolean isDirectory( FileObject file )
        throws FileSystemException
    {
        if ( FileType.FOLDER == file.getType() )
        {
            return true;
        }

        if ( FileType.FILE_OR_FOLDER == file.getType() )
        {
            try
            {
                file.getChildren();
            }
            catch ( FileNotFolderException e )
            {
                return false;
            }

            return true;
        }

        return false;
    }

    /////////////////////////////////////////////////////////////////////////////
    //  Helpers
    /////////////////////////////////////////////////////////////////////////////
    /**
     * Tests whether or not a name matches against at least one include pattern.
     * 
     * @param name The name to match. Must not be <code>null</code>.
     * @return <code>true</code> when the name matches against at least one include pattern, or
     *         <code>false</code> otherwise.
     */
    private boolean isIncluded( String name )
    {
        for ( int i = 0; i < includes.length; i++ )
        {
            if ( matchPath( includes[i], name, isCaseSensitive ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests whether or not a name matches against at least one exclude pattern.
     * 
     * @param name The name to match. Must not be <code>null</code>.
     * @return <code>true</code> when the name matches against at least one exclude pattern, or
     *         <code>false</code> otherwise.
     */
    private boolean isExcluded( String name )
    {
        for ( int i = 0; i < excludes.length; i++ )
        {
            if ( matchPath( excludes[i], name, isCaseSensitive ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests whether or not a name matches the start of at least one include pattern.
     * 
     * @param name The name to match. Must not be <code>null</code>.
     * @return <code>true</code> when the name matches against the start of at least one include
     *         pattern, or <code>false</code> otherwise.
     */
    private boolean couldHoldIncluded( String name )
    {
        for ( int i = 0; i < includes.length; i++ )
        {
            if ( matchPatternStart( includes[i], name, isCaseSensitive ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests whether or not a given path matches the start of a given pattern up to the first "**".
     * <p>
     * This is not a general purpose test and should only be used if you can live with false
     * positives. For example, <code>pattern=**\a</code> and <code>str=b</code> will yield
     * <code>true</code>.
     * 
     * @param pattern The pattern to match against. Must not be <code>null</code>.
     * @param str The path to match, as a String. Must not be <code>null</code>.
     * @param isCaseSensitive Whether or not matching should be performed case sensitively.
     * 
     * @return whether or not a given path matches the start of a given pattern up to the first
     *         "**".
     */
    private static boolean matchPatternStart( String pattern, String str, boolean isCaseSensitive )
    {
        return SelectorUtils.matchPatternStart( pattern, str, isCaseSensitive );
    }

    /**
     * Tests whether or not a given path matches a given pattern.
     * 
     * @param pattern The pattern to match against. Must not be <code>null</code>.
     * @param str The path to match, as a String. Must not be <code>null</code>.
     * @param isCaseSensitive Whether or not matching should be performed case sensitively.
     * 
     * @return <code>true</code> if the pattern matches against the string, or <code>false</code>
     *         otherwise.
     */
    private static boolean matchPath( String pattern, String str, boolean isCaseSensitive )
    {
        return SelectorUtils.matchPath( pattern, str, isCaseSensitive );
    }

}

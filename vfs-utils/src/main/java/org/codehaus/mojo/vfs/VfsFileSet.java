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

import org.apache.commons.vfs2.FileObject;

/**
 * VFS configuration to scan for a set of remote files.
 */
public class VfsFileSet
    extends BaseFileSet
{
    /**
     * Path after the url, this is where the scan starts
     */

    private FileObject source;

    /**
     * Relative of a remote URL when it used to copy files between 2 URLs.
     */
    private FileObject destination;

    public FileObject getDestination()
    {
        return destination;
    }

    public void setDestination( FileObject destination )
    {
        this.destination = destination;
    }

    public FileObject getSource()
    {
        return source;
    }

    public void setSource( FileObject source )
    {
        this.source = source;
    }

    /**
     * Retrieves the included and excluded files from this file-set's directory.
     * Specifically, <code>"file-set: <I>[directory]</I> (included:
     * <I>[included files]</I>, excluded: <I>[excluded files]</I>)"</code>
     *
     * @return The included and excluded files from this file-set's directory.
     * Specifically, <code>"file-set: <I>[directory]</I> (included:
     * <I>[included files]</I>, excluded: <I>[excluded files]</I>)"</code>
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "file-set: " + getSource() + " (included: " + getIncludes() + ", excluded: " + getExcludes() + ")";
    }

}

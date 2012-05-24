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

import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

public interface VfsFileSetManager
{
    /**
     * Retrieve a list of virtual files base on criteria set by fileSet
     * @param fileSet
     * @return List of remote file objects
     */
    List<FileObject> list( VfsFileSet fileSet )
        throws FileSystemException;

    /**
     * Delete a set of virtual files
     * @param fileSet
     */
    void delete( VfsFileSet fileSet )
        throws FileSystemException;

    /**
     * Move a set of files between 2 virtual directories with criteria in fileSet
     * @param fileSet
     */
    void move( VfsFileSet fileSet )
        throws FileSystemException;

    /**
     * Copy a set of files between 2 virtual directories with criteria in fileSet
     * @param fileSet
     */
    public void copy( VfsFileSet fileSet )
        throws FileSystemException;

}

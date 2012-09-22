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
import java.io.IOException;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

public interface MergeVfsMavenRepositories
{

    /**
     * Merge Maven repositories
     * @param fromRepo
     * @param toRepo
     * @throws FileSystemException
     */
    void merge( FileObject fromRepo, FileObject toRepo )
        throws FileSystemException, IOException;

    /**
     * Merge Maven repositories with option to do dryRun by not push the merge to target repo
     * @param fromRepo
     * @param toRepo
     * @param stagingDirectory if not given( null ) a temporary one will be created
     * @param dryRun when true, dont push to target repository and leave staging alone to review
     * @throws FileSystemException
     */
    void merge( FileObject fromRepo, FileObject toRepo, File stagingDirectory, boolean dryRun )
        throws FileSystemException, IOException;

}

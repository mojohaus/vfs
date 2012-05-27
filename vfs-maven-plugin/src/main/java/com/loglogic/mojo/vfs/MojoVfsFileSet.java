package com.loglogic.mojo.vfs;

import org.codehaus.mojo.vfs.BaseFileSet;

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

/**
 * VFS configuration to scan for a set of remote files.
 */
public class MojoVfsFileSet
    extends BaseFileSet
{
    /**
     * source URL
     */

    private String source;

    /**
     * maven settings server's source authentication id
     */
    private String sourceId;

    /**
     * Destination URL 
     */
    private String destination;

    public String getSourceId()
    {
        return sourceId;
    }

    public void setSourceId( String sourceId )
    {
        this.sourceId = sourceId;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource( String source )
    {
        this.source = source;
    }

    public String getDestination()
    {
        return destination;
    }

    public void setDestination( String destination )
    {
        this.destination = destination;
    }

    /**
     * maven settings server's destination authentication id
     */
    private String destinationId;

    public String getDestinationId()
    {
        return destinationId;
    }

    public void setDestinationId( String destinationId )
    {
        this.destinationId = destinationId;
    }

    public String toString()
    {
        return "file-set: " + getSource() + " (included: " + getIncludes() + ", excluded: " + getExcludes() + ")";
    }

}

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

public abstract class BaseFileSet
{

    /**
     * Ant's excludes path expression
     */
    private String[] excludes;

    /**
     * Ant's includes path expression
     */
    private String[] includes;

    /**
     * 
     */
    private boolean caseSensitive;

    /**
     * User default exclude sets
     */
    private boolean useDefaultExcludes = true;

    //////////////////////////////////////////////////////////////////////////////////////

    public String[] getExcludes()
    {
        return excludes;
    }

    public void setExcludes( String[] excludes )
    {
        this.excludes = excludes;
    }

    public String[] getIncludes()
    {
        return includes;
    }

    public void setIncludes( String[] includes )
    {
        this.includes = includes;
    }

    public boolean isCaseSensitive()
    {
        return caseSensitive;
    }

    public void setCaseSensitive( boolean caseSensitive )
    {
        this.caseSensitive = caseSensitive;
    }

    public boolean isUseDefaultExcludes()
    {
        return useDefaultExcludes;
    }

    public void setUseDefaultExcludes( boolean useDefaultExcludes )
    {
        this.useDefaultExcludes = useDefaultExcludes;
    }

    public void copyBase( BaseFileSet baseFileSet )
    {
        this.includes = baseFileSet.includes;
        this.excludes = baseFileSet.excludes;
        this.caseSensitive = baseFileSet.caseSensitive;
        this.useDefaultExcludes = baseFileSet.useDefaultExcludes;
    }
}

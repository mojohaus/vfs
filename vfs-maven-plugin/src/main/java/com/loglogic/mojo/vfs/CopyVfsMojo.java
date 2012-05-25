package com.loglogic.mojo.vfs;

import org.apache.maven.plugin.MojoExecutionException;

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
 * Copy files from one VFS to another VFS
 * 
 * @goal copy
 * @requiresProject false
 */
public class CopyVfsMojo
    extends AbstractVfsMojo
{
    /**
     * Directory path relative to source's Wagon
     * @parameter expression="${vfs.fromDir}" default-value=""
     */
    private String fromDir = "";

    /**
     * Comma separated list of Ant's includes to scan for remote files     
     * @parameter expression="${vfs.includes}" default-value="**";
     */
    private String includes;

    /**
     * Comma separated list of Ant's excludes to scan for remote files     
     * @parameter expression="${vfs.excludes}"
     * 
     */
    private String excludes;

    /**
     * Whether to consider remote path case sensitivity during scan
     * @parameter expression="${vfs.caseSensitive}"
     */
    private boolean caseSensitive = true;
    
    /**
     * Remote path relative to target's url to copy files to.
     * 
     * @parameter expression="${vfs.toDir}" default-value="";
     */
    private String toDir = "";
    

    public void execute()
        throws MojoExecutionException
    {
    }    
}
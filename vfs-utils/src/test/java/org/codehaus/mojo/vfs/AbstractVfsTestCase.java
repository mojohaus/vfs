package org.codehaus.mojo.vfs;

import java.io.File;

import org.junit.BeforeClass;

public abstract class AbstractVfsTestCase
{

    protected static File basedir = new File( System.getProperty( "basedir", "." ) );

    protected static File builddir = new File( basedir, "target" );

    /**
     * This allows eclipse and maven to have the same basedir
     */
    @BeforeClass
    public static void beforeBaseClass() {
        System.setProperty( "basedir", System.getProperty( "basedir", "." ) );
    }
    
}

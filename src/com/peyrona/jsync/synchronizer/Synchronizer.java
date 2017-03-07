
package com.peyrona.jsync.synchronizer;

import com.peyrona.jsync.Main;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.logging.Level;

/**
 *
 * @author peyrona
 */
public final class Synchronizer
{
    private final File       fOrigin;
    private final File       fDestin;
    private final FileFilter filter;
    private final Watcher    watcher;

    //----------------------------------------------------------------------------//

    public Synchronizer( File fOrigin, File fDestin, FileFilter filter )
    {
        this.fOrigin = fOrigin;
        this.fDestin = fDestin;
        this.filter  = filter;

        checkOrigin();
        checkDestination();

        // Initialize Watcher Service --------------------------
        Watcher w;

        try
        {
            w = new Watcher( fOrigin, fDestin, filter );
        }
        catch( IOException ioe )
        {
            w = null;
        }

        this.watcher = w;
        // -------------------------------------------------------

        Main.info( "Origin folder: '"+ fOrigin +"'\n"+
                   "Destination folder: '"+ fDestin +"'" );
    }

    //----------------------------------------------------------------------------//

    public void start()
    {
        Main.info( "Updating '"+ fDestin +"' with changes made in '"+ fOrigin +"' since "+ Main.sAPP_NAME +" ran last time." );
        (new FullSync( fOrigin, fDestin, filter )).sync();

        // If watcher can not ran, then, there is nothing else to do.
        if( watcher != null )
        {
            Main.info( "Monitoring changes in '"+ fOrigin +"'." );
            watcher.start();
        }
        else
        {
            Main.log( Level.SEVERE, new IOException( "Can not monitorize changes in '"+ fOrigin +"'. "+ Main.sAPP_NAME +" aborted." ) );
            System.exit( 1 );
        }
    }

    //----------------------------------------------------------------------------//

    private void checkOrigin()
    {
        if( ! fOrigin.exists() )
        {
            Main.log( Level.SEVERE, new IOException( fOrigin +" does not exists: can not continue." ) );
            System.exit( 1 );
        }

        if( ! fOrigin.isDirectory() )
        {
            Main.log( Level.SEVERE, new IOException( fOrigin +" is not a directory: can not continue." ) );
            System.exit( 1 );
        }

        if( ! fOrigin.canRead() )
        {
            Main.log( Level.SEVERE, new IOException( fOrigin +" is can not be read: can not continue." ) );
            System.exit( 1 );
        }
    }

    private void checkDestination()
    {
        if( fDestin.exists() && (! fDestin.isDirectory()) )
        {
            Main.log( Level.SEVERE, new IOException( fDestin +" is not a directory: can not continue." ) );
            System.exit( 1 );
        }

        if( (! fDestin.exists()) && (! fDestin.mkdirs()) )
        {
            Main.log( Level.SEVERE, new IOException( "Unable to create '"+ fDestin +"'. Can not continue." ) );
            System.exit( 1 );
        }

        if( ! fDestin.canRead() )
        {
            Main.log( Level.SEVERE, new IOException( fDestin +" is can not be read: can not continue." ) );
            System.exit( 1 );
        }

        if( ! fDestin.getParentFile().canWrite() )
        {
            Main.log( Level.SEVERE, new IOException( fDestin +" is read-only (can not write): can not continue." ) );
            System.exit( 1 );
        }
    }
}
/*
 * Copyright (C) 2017 Francisco José Morero Peyrona. All Rights Reserved.
 *
 * GNU Classpath is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the free
 * Software Foundation; either version 3, or (at your option) any later version.
 *
 * This app is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this software; see the file COPYING.  If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.peyrona.jsync;

import com.peyrona.jsync.config.Task;
import com.peyrona.jsync.synchronizer.Synchronizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Application entry point.
 *
 * @author peyrona
 */
public class Main
{
    // HOW TO MOUNT BOX.COM IN LINUX
    // http://xmodulo.com/how-to-mount-box-com-cloud-storage-on-linux.html

    public  static final String  sAPP_NAME    = "JSync";
    public  static final String  sCONFIG_FILE = sAPP_NAME +".config.xml";
    public  static final Logger  logger       = initLogger( sAPP_NAME +"-%u.%g.log.txt" );
    private static       boolean bVerbose     = false;         /* By default false */
    private static       boolean bTest        = false;         /* By default false */

    //----------------------------------------------------------------------------//

    public static void main( String[] args )
    {
        File fConfig = null;

        for( int n = 0; n < args.length; n++ )
        {
            switch( args[n].toLowerCase() )
            {
                case "-c":
                case "--config":
                    fConfig = new File( args[n+1] );
                    break;

                case "-v":
                case "--verbose":
                    bVerbose = true;
                    break;

                case "-t":
                case "--test":
                    bTest = true;
                    break;

                case "-h":
                case "--help":
                    showHelp();
                    System.exit( 0 );
                    break;
            }
        }

        if( fConfig == null )   // if not passed, then try with default name
        {
            fConfig = new File( sCONFIG_FILE );
        }

        if( ! fConfig.exists() )
        {
            log( Level.SEVERE, null, new FileNotFoundException( fConfig.toString() ) );
            System.exit( 1 );
        }

        try
        {
            execute( fConfig );
        }
        catch( Exception ex )
        {
            log( Level.SEVERE, sAPP_NAME +" can not continue", ex );
            System.exit( 1 );
        }
    }

    //----------------------------------------------------------------------------//

    public static void info( String msg )
    {
        if( bVerbose )
        {
            log( Level.INFO, msg, null );
        }
    }

    public static void log( Level level, Exception exc )
    {
        log( level, null, exc );
    }

    public static void log( Level level, String msg, Exception exc )
    {
        if( logger != null )
        {
            logger.log( level, msg, exc );
        }
    }

    public static boolean isTesting()
    {
        return bTest;
    }

    //----------------------------------------------------------------------------//

    private static void showHelp()
    {
        System.out.println( "JSync synchronizes files from an Origin folder into a Destination folder" );
        System.out.println( "Syntax:              (all parameters are optional)" );
        System.out.println( "-c, --config  <fileName> : set config file. By default: '"+ sCONFIG_FILE +"'." );
        System.out.println( "-v, --verbose            : extra information is sent to log file. By default it is off" );
        System.out.println( "-t, --test               : operations of copying and deleting are not performed" );
        System.out.println( "-h, --help               : shows this information and quit" );
        System.out.println();
        System.out.println( "Example: java -jar JSync.jar -v -c /home/username/jsync.xml" );
    }

    private static Logger initLogger( String sLogName )
    {
        Logger lgr = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );
               lgr.setLevel( Level.FINEST );

        try
        {
            FileHandler fh = new FileHandler( sLogName, 5*1000*1024, 9, true );
                        fh.setFormatter( new SimpleFormatter() );

            lgr.addHandler( fh );
        }
        catch( IOException ex )
        {
            lgr = null;
            System.err.println( "Error while configuring Logger." );
            ex.printStackTrace( System.err );
        }

        return lgr;
    }

    private static void execute( File fConfig ) throws Exception
    {
        for( Task task : Task.loadTasks( fConfig ) )
        {
            File   fOrigin = new File( task.getOriginFolder() );
            File   fDestin = new File( task.getDestinFolder() );
            Filter filter  = new Filter();
                   filter.setMaxSize( task.getMaxFileSize() );
                   filter.setFileExtensionsToIgnore( task.getIgnoreFileExts() );
                   filter.setFolderNamesToIgnore( task.getFolderNames() );

            info( "------------------------------------------------------------------------------\n"+
                  "Synchronization Started\n"+
                  "Config file: "+ fConfig +"\n"+
                  "Origin     : "+ fOrigin +"\n"+
                  "Destination: "+ fDestin +"\n" );

            (new Synchronizer( fOrigin, fDestin, filter )).start();
        }
    }
}
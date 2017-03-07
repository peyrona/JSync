
package com.peyrona.jsync;

import com.peyrona.jsync.config.Task;
import com.peyrona.jsync.synchronizer.Synchronizer;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author peyrona
 */
public class Main
{
    // COMO MONTAR BOX.COM EN LINUX
    // http://xmodulo.com/how-to-mount-box-com-cloud-storage-on-linux.html
    // http://askubuntu.com/questions/403785/mount-your-box-account-or-sync-a-folder-with-it

    public  static final String  sAPP_NAME    = "JSync";
    public  static final String  sCONFIG_FILE = sAPP_NAME +".config.xml";
    private static       boolean bVerbose     = false;   /* By default false */

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
            showHelp();
            System.exit( 0 );
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
        if( bVerbose == true )
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
        Logger.getLogger( sAPP_NAME ).log( level, msg, exc );
    }

    //----------------------------------------------------------------------------//

    private static void showHelp()
    {
        System.out.println( "JSync syncronizes files from an Origin folder into a Destination folder" );
        System.out.println( "Syntax:              (all parameters are optional)" );
        System.out.println( "-c, --config  <fileName> : set config file. By default: '"+ sCONFIG_FILE +"'." );
        System.out.println( "-v, --verbose            : extra information is sent to log file. By default it is off" );
        System.out.println( "-h, --help               : shows this information and quit" );
        System.out.println();
        System.out.println( "Example: java -jar JSync.jar -v -c /home/username/jsync.xml" );
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

            (new Synchronizer( fOrigin, fDestin, filter )).start();
        }
    }
}
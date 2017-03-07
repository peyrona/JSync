
package com.peyrona.jsync.synchronizer;

import com.peyrona.jsync.Main;
import static com.peyrona.jsync.Main.sAPP_NAME;
import com.peyrona.jsync.Utils;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 *
 * @author peyrona
 */
final class Watcher extends Thread
{
    private final File         fOrig;
    private final File         fDest;
    private final FileFilter   filter;
    private final WatchService watcher;

    //----------------------------------------------------------------------------//

    Watcher( File fOrig, File fDest, FileFilter filter ) throws IOException
    {
        super( sAPP_NAME +":"+ Watcher.class.getSimpleName() );

        this.fOrig   = fOrig;
        this.fDest   = fDest;
        this.filter  = filter;
        this.watcher = FileSystems.getDefault().newWatchService();

        Path dir = Paths.get( fOrig.getAbsolutePath() );
             dir.register( this.watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY );
    }

    //----------------------------------------------------------------------------//

    @Override
    public void run()
    {
        if( watcher == null )
        {
            return;
        }

        while( ! isInterrupted() )
        {
            WatchKey key;

            try
            {
                key = watcher.take();
            }
            catch( InterruptedException ex )
            {
                return;
            }

            for( WatchEvent<?> event : key.pollEvents() )
            {
                WatchEvent.Kind kind = event.kind();
                Path            path = ((WatchEvent<Path>) event).context();
                File            file = path.toFile();

                if( (kind != OVERFLOW) && filter.accept( file ) )
                {
                    if( (kind == ENTRY_CREATE ) || (kind == ENTRY_MODIFY) )
                    {
                        Utils.copy( file, Utils.getEquivalent( fOrig, fDest, file ) );
                        Main.info( file +": detected to be changed in Origin. Copied to Destination." );
                    }
                    else if( kind == ENTRY_DELETE )
                    {
                        Utils.delete( Utils.getEquivalent( fOrig, fDest, file ) );
                        Main.info( file +": detected to be deleted in Origin. Deleting in Destination." );
                    }
                }

                if( ! key.reset() )
                {
                    return;
                }
            }
        }
    }
}
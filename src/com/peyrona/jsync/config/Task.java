package com.peyrona.jsync.config;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author peyrona
 */
public final class Task
{
    private String sOriginFolder;
    private String sDestinFolder;
    private long   nMaxFileSize = 0;   // 0 == no limit

    private final Set<String> lstIgnoreFileExts    = new HashSet<>();
    private final Set<String> lstIgnoreFolderNames = new HashSet<>();

    //----------------------------------------------------------------------------//

    public static Set<Task> loadTasks( File fConfig )
           throws ParserConfigurationException, SAXException, IOException
    {
        Set<Task> lstTasks = new HashSet<>();
        NodeList  nlTasks  = XMLHelper.getRoot( fConfig ).getElementsByTagName( "task" );

        for( int n = 0; n < nlTasks.getLength(); n++ )
        {
            Node node = nlTasks.item( n );

            lstTasks.add( configureTask( new Task(), node.getChildNodes() ) );
        }

        return lstTasks;
    }

    //----------------------------------------------------------------------------//

    public String getOriginFolder() { return sOriginFolder; }
    public String getDestinFolder() { return sDestinFolder; }
    public long getMaxFileSize()    { return nMaxFileSize;  }

    public Set<String> getIgnoreFileExts() { return lstIgnoreFileExts;    }
    public Set<String> getFolderNames()    { return lstIgnoreFolderNames; }

    //----------------------------------------------------------------------------//

    private static Task configureTask( Task task, NodeList childs )
    {
        for( int n = 0; n < childs.getLength(); n++ )
        {
            Node node = childs.item( n );

            if( node.getNodeType() == Node.ELEMENT_NODE )
            {
                String sNodeName = node.getNodeName().trim().toLowerCase();

                if( "origin".equals( sNodeName ) )
                {
                    task.sOriginFolder = XMLHelper.getNodeValue( node );
                }
                else if( "destination".equals( sNodeName ) )
                {
                    task.sDestinFolder = XMLHelper.getNodeValue( node );
                }
                else if( "max_file_size".equals( sNodeName ) )
                {
                    task.nMaxFileSize = Long.parseLong( XMLHelper.getNodeValue( node ) );
                }
                else if( "ignore_file_ext".equals( sNodeName ) )
                {
                    task.lstIgnoreFileExts.add( XMLHelper.getNodeValue( node ) );
                }
                else if( "ignore_folder_name".equals( sNodeName ) )
                {
                    task.lstIgnoreFolderNames.add( XMLHelper.getNodeValue( node ) );
                }
            }
        }

        return task;
    }
}
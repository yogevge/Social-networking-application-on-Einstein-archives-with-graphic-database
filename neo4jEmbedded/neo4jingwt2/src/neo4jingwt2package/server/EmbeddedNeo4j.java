package neo4jingwt2package.server;

import java.io.File;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class EmbeddedNeo4j
{
    private static final String DB_PATH = "C:\\noe4j-helloDB";

    public String greeting;

    // START SNIPPET: vars
    GraphDatabaseService graphDb;
    Node firstNode;
    Node secondNode;
    Relationship relationship;
    // END SNIPPET: vars

    // START SNIPPET: createReltype
    private static enum RelTypes implements RelationshipType
    {
        KNOWS
    }
    // END SNIPPET: createReltype

//    public static void main( final String[] args )
//    {
//        EmbeddedNeo4j hello = new EmbeddedNeo4j();
//        hello.createDb();
//        hello.removeData();
//        hello.shutDown();
//    }
    
    public EmbeddedNeo4j() throws Exception
    {
    	System.out.println("************************************ 1.1 ************************************");
    	System.out.println("************************************ 1.2 ************************************");
    	createDb();
    	System.out.println("************************************ 1.3 ************************************");
    	removeData();
    	System.out.println("************************************ 1.4 ************************************");
    	shutDown();
    	System.out.println("************************************ 1.5 ************************************");
    }

    void createDb() throws Exception
    {
    	System.out.println("************************************ 1.2.1 ************************************");
        deleteFileOrDirectory( new File( DB_PATH ) );
        System.out.println("************************************ 1.2.2 ************************************");
        // START SNIPPET: startDb
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        System.out.println("************************************ 1.2.3 ************************************");
        registerShutdownHook( graphDb );
        System.out.println("************************************ 1.2.4 ************************************");
        // END SNIPPET: startDb

        // START SNIPPET: transaction
        try ( Transaction tx = graphDb.beginTx() )
        {
            // Database operations go here
            // END SNIPPET: transaction
            // START SNIPPET: addData
            firstNode = graphDb.createNode();
            firstNode.setProperty( "message", "Hello, " );
            secondNode = graphDb.createNode();
            secondNode.setProperty( "message", "World!" );

            relationship = firstNode.createRelationshipTo( secondNode, RelTypes.KNOWS );
            relationship.setProperty( "message", "brave Neo4j " );
            // END SNIPPET: addData

            // START SNIPPET: readData
//            System.out.print( firstNode.getProperty( "message" ) );
//            System.out.print( relationship.getProperty( "message" ) );
//            System.out.print( secondNode.getProperty( "message" ) );
            // END SNIPPET: readData

            greeting = ( (String) firstNode.getProperty( "message" ) )
                       + ( (String) relationship.getProperty( "message" ) )
                       + ( (String) secondNode.getProperty( "message" ) );

            // START SNIPPET: transaction
            tx.success();
        }
        // END SNIPPET: transaction
    }

    void removeData()
    {
        try ( Transaction tx = graphDb.beginTx() )
        {
            // START SNIPPET: removingData
            // let's remove the data
            firstNode.getSingleRelationship( RelTypes.KNOWS, Direction.OUTGOING ).delete();
            firstNode.delete();
            secondNode.delete();
            // END SNIPPET: removingData

            tx.success();
        }
    }

    void shutDown()
    {
        System.out.println();
        System.out.println( "Shutting down database ..." );
        // START SNIPPET: shutdownServer
        graphDb.shutdown();
        // END SNIPPET: shutdownServer
    }

    // START SNIPPET: shutdownHook
    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }
    // END SNIPPET: shutdownHook

    private static void deleteFileOrDirectory( File file )
    {
        if ( file.exists() )
        {
            if ( file.isDirectory() )
            {
                for ( File child : file.listFiles() )
                {
                    deleteFileOrDirectory( child );
                }
            }
            file.delete();
        }
    }
}
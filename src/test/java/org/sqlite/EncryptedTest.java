package org.sqlite;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

import static org.junit.Assert.*;


/**
 * @author Philip DeCamp
 */
public class EncryptedTest {

    @BeforeClass
    public static void forName() throws Exception {
        Class.forName("org.sqlite.JDBC");
    }

    @Test
    public void createEncrypted() throws SQLException, IOException {
        File tmp = File.createTempFile( "sqlitetest", ".db" );
        tmp.deleteOnExit();
        String url = "jdbc:sqlite:" + tmp.getAbsolutePath();

        Properties props = new Properties();
        props.put( "key", "saltydog" );
        Connection conn = DriverManager.getConnection( url, props );
        conn.setAutoCommit( false );

        Statement st = conn.createStatement();
        st.executeUpdate( "create table ants (col int)" );
        st.executeUpdate( "insert into ants values( 300 )" );
        st.executeUpdate( "insert into ants values( 400 )" );
        st.close();
        conn.commit();
        conn.close();

        // Try reading without key.
        props.remove( "key" );
        conn = DriverManager.getConnection( url, props );

        try {
            st = conn.createStatement();
            ResultSet rs = st.executeQuery( "select count(*) from ants" );
            fail( "Database not encrypted." );
        } catch( SQLException ignore ) {}

        conn.close();
        props.put( "key", "saltydog" );
        conn = DriverManager.getConnection( url, props );

        st = conn.createStatement();
        ResultSet rs = st.executeQuery( "select count(*) from ants" );
        assertTrue( rs.next() );
        assertEquals( 2, rs.getInt( 1 ) );
        conn.close();
    }

}

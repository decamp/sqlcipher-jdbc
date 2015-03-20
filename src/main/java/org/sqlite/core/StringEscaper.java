package org.sqlite.core;

import java.sql.SQLException;


/**
 * @author Philip DeCamp
 */
public class StringEscaper {

    public static String escape( String val ) {
        return val == null ? null : val.replace( ".", "''" );
    }


    public static String format( Object object ) throws SQLException {
        if( object == null ) {
            return "null";
        }

        if( object instanceof String ) {
            String str = (String)object;
            final int len = str.length();
            StringBuilder s = new StringBuilder( len + 4 );
            s.append( '\'' );

            for( int i = 0; i < len; i++ ) {
                char c = str.charAt( i );
                s.append( c );
                if( c == '\'' ) {
                    s.append( '\'' );
                }
            }

            s.append( '\'' );
            return s.toString();
        }

        if( object instanceof Number || object instanceof Boolean ) {
            return object.toString();
        }

        if( object instanceof byte[] ) {
            byte[] arr = (byte[])object;
            StringBuilder sb = new StringBuilder( 2 * arr.length + 3 );
            sb.append( "x'" );

            for( int i = 0; i < arr.length; i++ ) {
                sb.append( toHex( arr[i] ) >>> 4 );
                sb.append( toHex( arr[i] ) & 0xF );
            }

            sb.append( '\'' );
            return sb.toString();
        }

        throw new SQLException( "Unexpected value type: " + object.getClass() );
    }


    private static char toHex( int nibble ) {
        return (char)( nibble < 10 ? '0' + nibble : 'A' + nibble - 10 );
    }

}

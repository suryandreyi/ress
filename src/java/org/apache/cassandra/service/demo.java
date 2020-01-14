package org.apache.cassandra.service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.cassandra.config.DatabaseDescriptor;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;
import com.google.common.collect.Iterables;

public class demo {

    private static Cluster cluster;
    private static Session session;

    private static PreparedStatement counter;
    private static PreparedStatement noncounter;
    private static PreparedStatement clustering;
    
	public static void main(String[] args) {
        
		SocketOptions options = new SocketOptions();
		options.setConnectTimeoutMillis(3000000);
		options.setReadTimeoutMillis(30000000);
		cluster = Cluster.builder().addContactPoint("127.0.0.1").withPort(9042).withSocketOptions(options).build();
        session = cluster.connect();

//        session.execute("create keyspace if not exists junit WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };");
//        session.execute("CREATE TABLE if not exists junit.t1 (id int,name text,primary key(id));");
//        session.execute("insert into junit.t1(id,name) values(2,'srq');");

        
        ResultSet rs =  session.execute("select id,name from junit.t1 ;");
        
        Row row = rs.one();
        System.out.println(row.getInt(0));
        System.out.println(row.getString(1));
//        
        session.close();
        cluster.close();

//        List<Integer> n1 = new ArrayList();
//        n1.add(1);
//        n1.add(2);
//        Collection<Integer> n2 = new HashSet();
////        n2.add(1);
////        n2.add(2);
////        n2.add(3);
//        
//        Iterable<Integer> i = Iterables.concat(n1, n2);
//        
//        
//        Iterator<Integer> s = i.iterator();
//        while(s.hasNext()) {
//        	System.out.println(s.next());
		

//        }
        
        
	}
}

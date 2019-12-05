package org.apache.cassandra.service;

import java.io.IOException;

import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.junit.BeforeClass;
import org.junit.Test;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;

public class demo {
    private static EmbeddedCassandraService cassandra;
    private static Cluster cluster;
    private static Session session;
    
    public demo(String s) {
    	System.out.println(s);
    }
    
    public static void main(String[] args) throws InterruptedException, IOException {
//        cassandra = new EmbeddedCassandraService();
//        cassandra.start();
    	//configure socket options
    	SocketOptions options = new SocketOptions();
    	options.setConnectTimeoutMillis(30000000);
    	options.setReadTimeoutMillis(300000000);
    	options.setTcpNoDelay(true);
    	System.out.println("111111111111111111111111111111111");
        cluster = Cluster.builder().addContactPoint("127.0.0.1").withPort(9042).withSocketOptions(options).build();
        System.out.println("2222222222222222222222222222222222");
        session = cluster.connect();
        System.out.println("33333333333333333333333333333333333");
        session.execute("create keyspace if not exists junit WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };");
        System.out.println("4444444444444444444444444444444444444");
        session.execute("create table if not exists junit.t1 (id int,name text,primary key(id));");
        System.out.println("55555555555555555555555555555555555555");
    	String cql = "insert into junit.t1(id,name) values(25,'wxh')";
    	System.out.println("666666666666666666666666666666666666666");
    	session.execute(cql);
    	System.out.println("77777777777777777777777777777777777777");
	
	}
}

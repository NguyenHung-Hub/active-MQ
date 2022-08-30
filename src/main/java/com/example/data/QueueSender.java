package com.example.data;

import java.util.Date;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.log4j.BasicConfigurator;
import data.Person;

public class QueueSender {
    public static void main(String[] args) throws Exception {
        // config environment for JMS
        BasicConfigurator.configure();
        // config environment for JNDI
        Properties settings = new Properties();
        settings.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
        // create context
        Context ctx = new InitialContext(settings);
        // lookup JMS connection factory
        ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
        // lookup destination. (If not exist-->ActiveMQ create once)
        Destination destination = (Destination) ctx.lookup("dynamicQueues/nguyenhung");
        // get connection using credential
        Connection con = factory.createConnection("admin", "admin");
        // connect to MOM
        con.start();
        // create session
        Session session = con.createSession(
                /* transaction */false,
                /* ACK */Session.AUTO_ACKNOWLEDGE);
        // create producer
        MessageProducer producer = session.createProducer(destination);
        // create text message
        Message msg = session.createTextMessage("\n hello mesage from ActiveMQ \n\n");
        producer.send(msg);
        Person p = new Person(19500461, "NGUYEN HUNG", new Date());
        String xml = new XMLConvert<Person>(p).object2XML(p);
        msg = session.createTextMessage(xml);
        producer.send(msg);
        // shutdown connection
        session.close();
        con.close();
        System.out.println("\n\n >> Finished...");
    }
}
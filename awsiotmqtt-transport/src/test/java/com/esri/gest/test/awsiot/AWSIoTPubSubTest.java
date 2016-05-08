package com.esri.gest.test.awsiot;

import static org.junit.Assert.*;

import java.io.FileReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import junit.framework.Assert;

import org.junit.Test;

import com.esri.geoevent.transport.awsiotmqtt.MqttInboundTransport;
import com.esri.geoevent.transport.awsiotmqtt.MqttOutboundTransport;
import com.esri.geoevent.transport.awsiotmqtt.MqttOutboundTransportService;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.transport.Transport;

public class AWSIoTPubSubTest {

	private static ClassLoader resloader 				= AWSIoTPubSubTest.class.getClassLoader();
	@Test
	public void testPublshingToAwsIOT() throws Throwable {
		
		/*MqttOutboundTransportService  mqttOutTSService = new MqttOutboundTransportService();
		Transport mqttTransport = mqttOutTSService.createTransport();
		mqttTransport.start();
		
		String fileName = resloader.getResource("data.txt").getFile();
		ByteBuffer buffer = ByteBuffer.allocate(4);
		RandomAccessFile aFile = new RandomAccessFile(fileName, "r");
		FileChannel inChannel = aFile.getChannel();
		String line = null;
		StringBuffer sb = new StringBuffer();
		int bytesRead = inChannel.read(buffer);
		while (bytesRead != -1) {
			buffer.flip();
			 while(buffer.hasRemaining()){			
				 sb.append((char) buffer.get());
			      //System.out.print((char) buffer.get()); // read 1 byte at a time
			  }
			 System.out.println(sb.toString());
			 buffer.clear(); //make buffer ready for writing
			 bytesRead = inChannel.read(buffer);
		}
		
		aFile.close();
				
		
		Assert.assertTrue(bytesRead>0);		*/
	}
	
	public void testSubsribingToAwsIOT() {
		fail("Not yet implemented");
	}


}

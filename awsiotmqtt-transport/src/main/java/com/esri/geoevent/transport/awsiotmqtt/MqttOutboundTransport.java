/*
  Copyright 1995-2015 Esri

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

  For additional information, contact:
  Environmental Systems Research Institute, Inc.
  Attn: Contracts Dept
  380 New York Street
  Redlands, California, USA 92373

  email: contracts@esri.com
*/

package com.esri.geoevent.transport.awsiotmqtt;

import java.nio.ByteBuffer;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.component.RunningException;
import com.esri.ges.core.component.RunningState;
import com.esri.ges.framework.i18n.BundleLogger;
import com.esri.ges.framework.i18n.BundleLoggerFactory;
import com.esri.ges.transport.OutboundTransportBase;
import com.esri.ges.transport.TransportDefinition;

public class MqttOutboundTransport extends OutboundTransportBase
{

	private static final BundleLogger	log	= BundleLoggerFactory.getLogger(MqttOutboundTransport.class);
	private static ClassLoader 			resloader  	= MqttOutboundTransport.class.getClassLoader();
	private int												port;
	private String										host;
	private String										rootCA;
	private String										cert;
	private String										privateKey;
	private String										topic;
	private MqttClient								mqttClient;
	private MqttConnectOptions 						connOpts;

	public MqttOutboundTransport(TransportDefinition definition) throws ComponentException
	{
		super(definition);
	}

	@Override
	public void start() throws RunningException
	{
		try
		{
			setRunningState(RunningState.STARTING);
			applyProperties();
			connectMqtt();
			setRunningState(RunningState.STARTED);
		}
		catch (Exception e)
		{
			log.error("INIT_ERROR", e, this.getClass().getName());
			setRunningState(RunningState.ERROR);
		}
	}

	@Override
	public void receive(ByteBuffer buffer, String channelId)
	{
		try
		{
			if (mqttClient == null || !mqttClient.isConnected())
				connectMqtt();

			byte[] b = new byte[buffer.remaining()];
			buffer.get(b);

			mqttClient.publish(topic, b, 2, true);
		}
		catch (Exception e)
		{
			log.error("ERROR_PUBLISHING", e);
		}
	}

	private void connectMqtt() throws MqttException
	{
		//String url = "tcp://" + host + ":" + Integer.toString(port);
		String url = "ssl://" + host + ":" + Integer.toString(port);
		mqttClient = new MqttClient(url, MqttClient.generateClientId(), new MemoryPersistence());
		connOpts = new MqttConnectOptions();	         
        connOpts.setCleanSession(true);
        connOpts.setSocketFactory(SslUtil.getSocketFactory(rootCA, cert, privateKey, "password"));         
		mqttClient.connect(connOpts);		
	}

	private void applyProperties() throws Exception
	{
		port = 1883; // default
		if (getProperty("port").isValid())
		{
			int value = (Integer) getProperty("port").getValue();
			if (value > 0 && value != port)
			{
				port = value;
			}
		}

		host = "iot.eclipse.org"; // default
		if (getProperty("host").isValid())
		{
			String value = (String) getProperty("host").getValue();
			if (!value.trim().equals(""))
			{
				host = value;
			}
		}

		topic = "topic/actuators/light"; // default
		if (getProperty("topic").isValid())
		{
			String value = (String) getProperty("topic").getValue();
			if (!value.trim().equals(""))
			{
				topic = value;
			}
		}
		
		rootCA = "certs/rootCA.pem"; //default
		if (getProperty("rootCA").isValid())
		{
			String value = (String) getProperty("rootCA").getValue();
			if (!value.trim().equals(""))
			{
				rootCA = resloader.getResource(value).getFile();				
			}
		}
		
		cert = "certs/cert.pem"; //default
		if (getProperty("certificate").isValid())
		{
			String value = (String) getProperty("certificate").getValue();
			if (!value.trim().equals(""))
			{
				cert = resloader.getResource(value).getFile();				
			}
		}
		
		privateKey = "certs/private.pem"; //default
		if (getProperty("privateKey").isValid())
		{
			String value = (String) getProperty("privateKey").getValue();
			if (!value.trim().equals(""))
			{
				privateKey = resloader.getResource(value).getFile();				
			}
		}
	}

	@Override
	public synchronized void stop()
	{
		setRunningState(RunningState.STOPPING);

		if (this.mqttClient != null)
			disconnectMqtt();

		setRunningState(RunningState.STOPPED);
	}

	private void disconnectMqtt()
	{
		try
		{
			if (mqttClient != null)
			{
				if (mqttClient.isConnected())
				{
					mqttClient.disconnect();
					mqttClient.close();
				}
			}
		}
		catch (MqttException e)
		{
			log.error("UNABLE_TO_CLOSE", e);
		}
		finally
		{
			mqttClient = null;
		}
	}

}

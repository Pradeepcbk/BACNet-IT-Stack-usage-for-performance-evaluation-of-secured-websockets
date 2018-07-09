import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.common.math.BigIntegerMath;

// Import packages from the BACnet/IT opensource projects
// By convention just classes within an api package should be used
import ch.fhnw.bacnetit.ase.application.configuration.api.DiscoveryConfig;
import ch.fhnw.bacnetit.ase.application.configuration.api.KeystoreConfig;
import ch.fhnw.bacnetit.ase.application.configuration.api.TruststoreConfig;
import ch.fhnw.bacnetit.ase.application.service.ASEChannel;
import ch.fhnw.bacnetit.ase.application.service.api.ASEServices;
import ch.fhnw.bacnetit.ase.application.service.api.BACnetEntityListener;
import ch.fhnw.bacnetit.ase.application.service.api.ChannelConfiguration;
import ch.fhnw.bacnetit.ase.application.service.api.ChannelFactory;
import ch.fhnw.bacnetit.ase.application.transaction.api.*;
import ch.fhnw.bacnetit.ase.encoding.api.BACnetEID;
import ch.fhnw.bacnetit.ase.encoding.api.TPDU;
import ch.fhnw.bacnetit.ase.encoding.api.T_UnitDataIndication;
import ch.fhnw.bacnetit.ase.encoding.api.T_UnitDataRequest;
import ch.fhnw.bacnetit.ase.network.directory.api.DirectoryService;
import ch.fhnw.bacnetit.ase.transportbinding.service.api.ASEService;
import ch.fhnw.bacnetit.directorybinding.dnssd.api.DNSSD;
import ch.fhnw.bacnetit.samplesandtests.api.deviceobjects.BACnetObjectIdentifier;
import ch.fhnw.bacnetit.samplesandtests.api.deviceobjects.BACnetObjectType;
import ch.fhnw.bacnetit.samplesandtests.api.deviceobjects.BACnetPropertyIdentifier;
import ch.fhnw.bacnetit.samplesandtests.api.encoding.asdu.ASDU;
import ch.fhnw.bacnetit.samplesandtests.api.encoding.asdu.ComplexACK;
import ch.fhnw.bacnetit.samplesandtests.api.encoding.asdu.ConfirmedRequest;
import ch.fhnw.bacnetit.samplesandtests.api.encoding.asdu.UnconfirmedRequest;
import ch.fhnw.bacnetit.samplesandtests.api.encoding.type.primitive.OctetString;
import ch.fhnw.bacnetit.samplesandtests.api.encoding.type.primitive.UnsignedInteger;
import ch.fhnw.bacnetit.samplesandtests.api.encoding.util.ByteQueue;
import ch.fhnw.bacnetit.samplesandtests.api.service.confirmed.WritePropertyRequest;
import ch.fhnw.bacnetit.samplesandtests.api.service.unconfirmed.WhoIsRequest;
import ch.fhnw.bacnetit.transportbinding.api.BindingConfiguration;
import ch.fhnw.bacnetit.transportbinding.api.ConnectionFactory;
import ch.fhnw.bacnetit.transportbinding.api.TransportBindingInitializer;
import ch.fhnw.bacnetit.transportbinding.ws.incoming.api.*;
import ch.fhnw.bacnetit.transportbinding.ws.incoming.tls.api.WSSConnectionServerFactory;
import ch.fhnw.bacnetit.transportbinding.ws.outgoing.api.*;
import ch.fhnw.bacnetit.transportbinding.ws.outgoing.tls.api.WSSConnectionClientFactory;

public class Pradeep_Application extends MessageExchanges{
	
	private static final int WSS_PORT = 9090;
	private static final String WSS_SCHEME = "wss";
	private static final int serverID = 2001;
	private static final int clientID = 2002;
	private static BACnetEID serverEID = new BACnetEID(serverID);
	private static BACnetEID clientEID = new BACnetEID(clientID);
	
	static ASEServices aseService = ChannelFactory.getInstance();
	String Me = "wss://localhost:9090";
	String Friend = "wss://localhost:8080";
	
	/*A function to convert integer to a byte array*/
	private static byte[] encode(int i) {
		  return new byte[] { (byte) (i >>> 24), (byte) ((i << 8) >>> 24),
		                      (byte) ((i << 16) >>> 24), (byte) ((i << 24) >>> 24)
		  };
	}
	/*A function to convert integer to a byte array*/

	public void mainfun() throws URISyntaxException {
		
		final URI MyDevice = new URI(Me);
		final URI FriendDevice = new URI(Friend);
		
		/*Keys and trust certificates for Secure web sockets*/
    	final KeystoreConfig keystoreConfig = new KeystoreConfig(
                "dummyKeystores/keyStoreDev1.jks", "123456",
                "operationaldevcert");
        final TruststoreConfig truststoreConfig = new TruststoreConfig(
                "dummyKeystores/trustStore.jks", "123456", "installer.ch",
                "installer.net");
        /*Keys and trust certificates for Secure web sockets*/
        
        start();
        
	}
	
	public static void start() {
		ChannelConfiguration channelConfiguration = aseService;
		
		/*Keys and trust certificates for Secure web sockets*/
    	final KeystoreConfig keystoreConfig = new KeystoreConfig(
                "dummyKeystores/keyStoreDev1.jks", "123456",
                "operationaldevcert");
        final TruststoreConfig truststoreConfig = new TruststoreConfig(
                "dummyKeystores/trustStore.jks", "123456", "installer.ch",
                "installer.net");
        /*Keys and trust certificates for Secure web sockets*/
        
		/* Add transport binding to Web sockets */
		ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.addConnectionClient("wss", new WSSConnectionClientFactory(keystoreConfig,truststoreConfig));
        connectionFactory.addConnectionServer("wss",new WSSConnectionServerFactory(WSS_PORT,keystoreConfig, truststoreConfig));
		BindingConfiguration bindingConfiguration = new TransportBindingInitializer();
		bindingConfiguration.initializeAndStart(connectionFactory);
		channelConfiguration.setASEService((ASEService) bindingConfiguration);
		/* Add transport binding to Web sockets */
		
		/*
         *********************** Initialize the directory service (not used in this example)
         */
        final DiscoveryConfig ds = new DiscoveryConfig("DNSSD", "1.1.1.1",
                "itb.bacnet.ch.", "bds._sub._bacnet._tcp.",
                "dev._sub._bacnet._tcp.", "obj._sub._bacnet._tcp.", false);

        try {
            DirectoryService.init();
            DirectoryService.getInstance().setDNSBinding(new DNSSD(ds));

        } catch (final Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
		
channelConfiguration.setEntityListener(new BACnetEntityListener() {
			
			@Override
			public void onRemoteRemove(BACnetEID arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onRemoteAdded(BACnetEID eid, URI uri) {
				DirectoryService.getInstance().register(eid, uri, false, true);
				
			}
			
			@Override
			public void onLocalRequested(BACnetEID arg0) {
				// TODO Auto-generated method stub
				
			}
		});

	ChannelListener bacnetDevice2001 = new ChannelListener(clientEID) {

			@Override
			public void onIndication(T_UnitDataIndication arg0, Object arg1) {
				
				System.out.println("Application1 got an indication");
                // Parse the incoming message
                ASDU incoming = getServiceFromBody(arg0.getData().getBody());

                // Dummy Handling of a ReadPropertyAck
                if (incoming instanceof ComplexACK) {
                    System.out.println(
                            "Application1 got an indication - ReadPropertyAck");
                    System.out.println("************\nReceived Value: "
                            + ((ComplexACK) incoming).getService().toString()
                                    .split("\\(")[1].split("\\)")[0]
                            + "\n************");
                }
			}

			@Override
			public void onError(String arg0) {

			}
		};
		
		ChannelListener bacnetDevice2002 = new ChannelListener(serverEID) {

			@Override
			public void onIndication(T_UnitDataIndication tUnitDataIndication, Object context) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(String cause) {
				// TODO Auto-generated method stub
				
			}
			
		};
		channelConfiguration.registerChannelListener(bacnetDevice2002);
		channelConfiguration.registerChannelListener(bacnetDevice2001);
		
		devices.add(bacnetDevice2001);
		devices.add(bacnetDevice2002);;
	}
	
	public void afterShow(int data) throws URISyntaxException {
			final URI MyDevice = new URI(Me);
			final URI FriendDevice = new URI(Friend);
		 System.out.println("Pradeep sends a WritePropRequest to Pradeep");
	        byte[] bytes = encode(data);
	        sendWritePropertyRequest(aseService,FriendDevice, clientEID, new BACnetEID(1001),bytes);
	        try {
	            Thread.sleep(2000);
	        } catch (InterruptedException e1) {
	            // TODO Auto-generated catch block
	            e1.printStackTrace();
	        }
	        try {
	            System.out.println("Pradeep sends a ReadPropRequest to Pradeep");
	            sendReadPropertyRequestUsingBACnet4j(aseService, FriendDevice, clientEID,new BACnetEID(1001));
	        	} catch (Exception e) {
	            System.err.print(e);
	        }

	        try {
	            Thread.sleep(2000);
	        } catch (InterruptedException e1) {
	            // TODO Auto-generated catch block
	            e1.printStackTrace();
	        }
	}
	public void read() throws URISyntaxException {
		final URI MyDevice = new URI(Me);
		final URI FriendDevice = new URI(Friend);
		try {
            System.out.println("Pradeep sends a ReadPropRequest to Pradeep");
            sendReadPropertyRequestUsingBACnet4j(aseService, FriendDevice, clientEID,new BACnetEID(1001));
        	} catch (Exception e) {
            System.err.print(e);
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
	}
	
	public void write(int data) throws URISyntaxException {
		final URI MyDevice = new URI(Me);
		final URI FriendDevice = new URI(Friend);
	 System.out.println("Pradeep sends a WritePropRequest to Pradeep");
        byte[] bytes = encode(data);
        sendWritePropertyRequest(aseService,FriendDevice, clientEID, new BACnetEID(1001),bytes);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
	}
}

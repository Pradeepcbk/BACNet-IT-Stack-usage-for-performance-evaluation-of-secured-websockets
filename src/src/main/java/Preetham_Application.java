import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

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
import ch.fhnw.bacnetit.samplesandtests.api.encoding.asdu.ConfirmedRequest;
import ch.fhnw.bacnetit.samplesandtests.api.encoding.asdu.UnconfirmedRequest;
import ch.fhnw.bacnetit.samplesandtests.api.encoding.type.primitive.OctetString;
import ch.fhnw.bacnetit.samplesandtests.api.encoding.type.primitive.Real;
import ch.fhnw.bacnetit.samplesandtests.api.encoding.type.primitive.UnsignedInteger;
import ch.fhnw.bacnetit.samplesandtests.api.encoding.util.ByteQueue;
import ch.fhnw.bacnetit.samplesandtests.api.service.acknowledgment.ReadPropertyAck;
import ch.fhnw.bacnetit.samplesandtests.api.service.confirmed.ReadPropertyRequest;
import ch.fhnw.bacnetit.samplesandtests.api.service.confirmed.WritePropertyRequest;
import ch.fhnw.bacnetit.samplesandtests.api.service.unconfirmed.IAmRequest;
import ch.fhnw.bacnetit.transportbinding.api.BindingConfiguration;
import ch.fhnw.bacnetit.transportbinding.api.ConnectionFactory;
import ch.fhnw.bacnetit.transportbinding.api.TransportBindingInitializer;
import ch.fhnw.bacnetit.transportbinding.ws.incoming.api.*;
import ch.fhnw.bacnetit.transportbinding.ws.incoming.tls.api.WSSConnectionServerFactory;
import ch.fhnw.bacnetit.transportbinding.ws.outgoing.api.*;
import ch.fhnw.bacnetit.transportbinding.ws.outgoing.tls.api.WSSConnectionClientFactory;

public class Preetham_Application extends MessageExchanges {
	
	private static final int WSS_PORT = 8080;
	private static final int serverID = 3;
	private static final int clientID = 4;
	private static BACnetEID serverEID = new BACnetEID(serverID);
	private static BACnetEID clientEID = new BACnetEID(clientID);
	static int resource = 100;
	static int temp_resource = 0;
	
	static ASEServices aseService = ChannelFactory.getInstance();
	
	/*A function to convert integer to a byte array*/
	private static byte[] encode(int i) {
		  return new byte[] { (byte) (i >>> 24), (byte) ((i << 8) >>> 24),
		                      (byte) ((i << 16) >>> 24), (byte) ((i << 24) >>> 24)
		  };
	}
	/*A function to convert integer to a byte array*/

	public static void mainfun() {	
		
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

			}

			@Override
			public void onRemoteAdded(BACnetEID eid, URI uri) {
				DirectoryService.getInstance().register(eid, uri, false, true);

			}

			@Override
			public void onLocalRequested(BACnetEID arg0) {

			}
		});

		ChannelListener bacnetDevice3 = new ChannelListener(serverEID) {

			@Override
			public void onIndication(T_UnitDataIndication arg0, Object arg1) {
				
				System.out.println("Preetham got an indication");
                // Parse the incoming message
                ASDU incoming = getServiceFromBody(arg0.getData().getBody());
                
                if (incoming instanceof ConfirmedRequest && ((ConfirmedRequest) incoming)
                        .getServiceRequest() instanceof ReadPropertyRequest) {
                    System.out.println("Preetham got a ReadPropertyRequest");

                    // Prepare DUMMY answer
                    final ByteQueue byteQueue = new ByteQueue();
                    new ReadPropertyAck(
                            new BACnetObjectIdentifier(
                                    BACnetObjectType.analogValue, 1),
                            BACnetPropertyIdentifier.presentValue,
                            new UnsignedInteger(1), new Real(resource))
                                    .write(byteQueue);
                    // Send answer
                    try {
                        System.out.println("Preetham sends the ReadPropertyAck");
                        sendBACnetMessage(aseService,new URI("wss://localhost:9090"),clientEID,new BACnetEID(2),byteQueue.popAll());
                    } catch (URISyntaxException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } // Handling of a WritePropertyRequest
                else if (incoming instanceof ConfirmedRequest
						&& ((ConfirmedRequest) incoming).getServiceRequest() instanceof WritePropertyRequest) {
                	System.out.println("Preetham has recieved a WritePropertyRequest");
					// read the message from the request
					ByteQueue byteQueue = new ByteQueue(arg0.getData().getBody());
					byte msg = byteQueue.peek(17);
					temp_resource = msg;
					resource = temp_resource;
				}
                
			}

			@Override
			public void onError(String arg0) {

			}
		};
		ChannelListener bacnetDevice4 = new ChannelListener(clientEID){

			@Override
			public void onIndication(T_UnitDataIndication tUnitDataIndication, Object context) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(String cause) {
				// TODO Auto-generated method stub
				
			}
			
		};
		channelConfiguration.registerChannelListener(bacnetDevice3);
		channelConfiguration.registerChannelListener(bacnetDevice4);
		
        devices.add(bacnetDevice3);
        devices.add(bacnetDevice4);
	}
}

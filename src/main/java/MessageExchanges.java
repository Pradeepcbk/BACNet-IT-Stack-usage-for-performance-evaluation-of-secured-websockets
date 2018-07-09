import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import ch.fhnw.bacnetit.ase.application.service.api.ASEServices;
import ch.fhnw.bacnetit.ase.application.transaction.api.ChannelListener;
import ch.fhnw.bacnetit.ase.encoding.api.BACnetEID;
import ch.fhnw.bacnetit.ase.encoding.api.TPDU;
import ch.fhnw.bacnetit.ase.encoding.api.T_UnitDataRequest;
import ch.fhnw.bacnetit.samplesandtests.api.deviceobjects.BACnetObjectIdentifier;
import ch.fhnw.bacnetit.samplesandtests.api.deviceobjects.BACnetObjectType;
import ch.fhnw.bacnetit.samplesandtests.api.deviceobjects.BACnetPropertyIdentifier;
import ch.fhnw.bacnetit.samplesandtests.api.encoding.asdu.ASDU;
import ch.fhnw.bacnetit.samplesandtests.api.encoding.asdu.IncomingRequestParser;
import ch.fhnw.bacnetit.samplesandtests.api.encoding.type.constructed.ServicesSupported;
import ch.fhnw.bacnetit.samplesandtests.api.encoding.type.primitive.OctetString;
import ch.fhnw.bacnetit.samplesandtests.api.encoding.type.primitive.UnsignedInteger;
import ch.fhnw.bacnetit.samplesandtests.api.encoding.util.ByteQueue;
import ch.fhnw.bacnetit.samplesandtests.api.service.confirmed.ReadPropertyRequest;
import ch.fhnw.bacnetit.samplesandtests.api.service.confirmed.WritePropertyRequest;

public class MessageExchanges {
	
	final public static List<ChannelListener> devices = new LinkedList<ChannelListener>();
	
	public static void sendReadPropertyRequestUsingBACnet4j(ASEServices senderService,URI destination, BACnetEID from, BACnetEID to)
            throws URISyntaxException {
        ReadPropertyRequest readRequest = new ReadPropertyRequest(
                new BACnetObjectIdentifier(BACnetObjectType.analogValue, 1),
                BACnetPropertyIdentifier.presentValue);
        ByteQueue byteQueue = new ByteQueue();
        readRequest.write(byteQueue);
        TPDU tpdu = new TPDU(from, to,
                byteQueue.popAll());

        T_UnitDataRequest unitDataRequest = new T_UnitDataRequest(destination, tpdu, 1, null);

        senderService.doRequest(unitDataRequest);

    }
	
	protected static void sendWritePropertyRequest(ASEServices senderService, URI destination, BACnetEID from, BACnetEID to, byte[] message){
		// APDU
		WritePropertyRequest writePropertyRequest = new WritePropertyRequest(
				new BACnetObjectIdentifier(BACnetObjectType.analogValue, 1), BACnetPropertyIdentifier.presentValue,
				new UnsignedInteger(55), new OctetString(message), new UnsignedInteger(0));
		final ByteQueue byteQueue = new ByteQueue();
		writePropertyRequest.write(byteQueue);
		// the BACnet/IT message body
		final TPDU tpdu = new TPDU(from, to, byteQueue.popAll());
		// enclose TPDU in T-UNITDATA.request message
		final T_UnitDataRequest unitDataRequest = new T_UnitDataRequest(destination, tpdu, 1, null);
		// send the message to the transport layer
		senderService.doRequest(unitDataRequest);
	}
	
	protected static void sendBACnetMessage(ASEServices applicationService, URI destination, BACnetEID from, BACnetEID to, byte[] confirmedBacnetMessage) throws URISyntaxException {
	      
        final TPDU tpdu = new TPDU(from, to,
                confirmedBacnetMessage);

        final T_UnitDataRequest unitDataRequest = new T_UnitDataRequest(destination, tpdu, 1, null);

        applicationService.doRequest(unitDataRequest);
    }
	
	protected static ASDU getServiceFromBody(final byte[] body) {
		final ByteQueue queue = new ByteQueue(body);
		final ServicesSupported servicesSupported = new ServicesSupported();
		servicesSupported.setAll(true);
		final IncomingRequestParser parser = new IncomingRequestParser(servicesSupported, queue);
		ASDU request = null;

		try {
			request = parser.parse();
		} catch (final Exception e) {
			System.out.println(e);
		}
		return request;
	}
}

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class ConnectionCreator {
	TTransport ttransport;
	TProtocol tprotocol;
	
	public Branch.Client createConnection(BranchID branch){
		ttransport = new TSocket(branch.getIp(),branch.getPort());
		try{
			ttransport.open();
		}catch(TTransportException e){
			e.printStackTrace();
			System.exit(1);
		}
		tprotocol = new TBinaryProtocol(ttransport);
		Branch.Client client = new Branch.Client(tprotocol);
		return client;
	}

}

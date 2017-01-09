import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class SnapShotInitiator implements Runnable {

	@Override
	public void run() {
		while(true){
			try{
				int listSize = Controller.branchList.size();
				int branchIndex  = (int) ((Math.random()*(listSize-1)) + 1);
				BranchID branch = Controller.branchList.get(branchIndex);
				TTransport transport = new TSocket(branch.getIp(), branch.getPort());
				transport.open();
				TProtocol tprotocol = new TBinaryProtocol(transport);
				Controller.CLIENT = new Branch.Client(tprotocol);
				Thread.sleep(5000);
				Controller.SNAPSHOTNUMBER++;
				Controller.CLIENT.initSnapshot(Controller.SNAPSHOTNUMBER);
				
			}catch(TException | InterruptedException e){
				System.exit(0);
			}
		}
		
		
	}
	

}

import java.util.spi.LocaleNameProvider;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class SnapShotRetriever implements Runnable{

	@Override
	public void run() {
		try{
			Branch.Client client = null;
			while(true){
				LocalSnapshot localSnapsot = null;
				Thread.sleep(20000);
				Controller.SNAPSHOTRETREIVERNUMBER++;
				System.out.println("Snapshot for snapshotID: "+Controller.SNAPSHOTRETREIVERNUMBER);
				int branchListSize = Controller.branchList.size();
				int totalBalance = 0;
				int channelBalance = 0;
				int bIndex = 0;
				for(BranchID b:Controller.branchList){
					
					localSnapsot = new LocalSnapshot();
					TTransport transport = new TSocket(b.getIp(),b.getPort());
					transport.open();
					TProtocol tprotocol = new TBinaryProtocol(transport);
					client = new Branch.Client(tprotocol);
					localSnapsot = client.retrieveSnapshot(Controller.SNAPSHOTRETREIVERNUMBER);
					channelBalance = 0;
					if(localSnapsot.messages.isEmpty() == false){
						int i = 0;
						while(i<localSnapsot.messages.size()){
							channelBalance = channelBalance + localSnapsot.messages.get(i);
							i++;
						}
						  
					}
					totalBalance = totalBalance + localSnapsot.balance+channelBalance;
					
					
					System.out.println("SnapShotRetriever(run)->"+localSnapsot.toString());
					bIndex++;
					if(bIndex == Controller.branchList.size()){
						System.out.println("SnapShotRetriever(run)->Total Balance is: "+totalBalance);
					}
					
					
				}
				System.out.println("------------------------------------------------------------------");
				
				
				
			}
		}catch(TException | InterruptedException e){
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	

}

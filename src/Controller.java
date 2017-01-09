
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class Controller {
	
	static Branch.Client CLIENT = null;
	
	private static int initialAmount;
	
	public static int SNAPSHOTNUMBER = 0;
	
	public static int SNAPSHOTRETREIVERNUMBER = 0;
	
	public static List<BranchID> branchList = new ArrayList<BranchID>();
	
	public static void main(String[] args){
		
		FileInputStream fileInputStreamIn = null;
		String fileIn = args[1];
		FileProcessor fileProcessor = null;
		TTransport transport;
		initialAmount = Integer.parseInt(args[0]);
		try {
			fileProcessor = new FileProcessor(fileInputStreamIn, fileIn);
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
		String line = fileProcessor.readFromLine();
		BranchID branch;
		String name, ip;
		int port;
		while(line!=null){
			String[] values = line.split("\\s+");
			name = values[0];
			ip = values[1];
			port = Integer.parseInt(values[2]);
			branch = new BranchID();
			branch.setIp(ip);
			branch.setName(name);
			branch.setPort(port);
			Controller.branchList.add(branch);
			line = fileProcessor.readFromLine();
		}
		for(BranchID b:branchList){
			transport = new TSocket(b.getIp(), b.getPort());
			try {
				transport.open();
				if(transport.isOpen()){
					System.out.println("Socket opened for Branch: "+b.getName());
				}
				TProtocol tprotocol = new TBinaryProtocol(transport);
				CLIENT = new Branch.Client(tprotocol);
				start(CLIENT);
				transport.close();
				
			} catch (TTransportException e) {
				e.printStackTrace();
			}
		}
		SnapShotInitiator initSnapShot = new SnapShotInitiator();
		Thread initSnapShotThread = new Thread(initSnapShot);
		initSnapShotThread.start();
		SnapShotRetriever snapShotRetriever = new SnapShotRetriever();
		Thread retreiveSnapShotThread = new Thread(snapShotRetriever);
		retreiveSnapShotThread.start();
	
	}
	private static void start(Branch.Client client){
		
		int branchAmount = initialAmount/branchList.size();
		try {
			client.initBranch(branchAmount, branchList);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TException e) {
			System.exit(1);
			e.printStackTrace();
		}
	}

}

import java.util.Random;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

//import Branch.Client;

public class BranchServer {
	public static BranchOperations BRANCHOPERATOR;
	public static int PORTNUMBER;
	public static Branch.Processor<Branch.Iface> PROCESSOR;
	public static String branchName; 
	public static int snapShotId = 1; 
	
	public static synchronized void  updateAmount(int flag, int amount){
		if(flag == 1){
			//int balance = BRANCHOPERATOR.getBalance();
			System.out.println("BranchServer(updateAmount)->"+branchName+" has balance before sending:"+BRANCHOPERATOR.getBalance());
			BRANCHOPERATOR.balance.set(BRANCHOPERATOR.balance.get() - amount);
			System.out.println("BranchServer(updateAmount)->"+branchName+" has balance after sending:"+BRANCHOPERATOR.getBalance());
			
		}
		
	}
	
	public static void startSending(){
		
		Thread sendingThread = new Thread(new Runnable(){
			@Override
			public void run() {
				int counter = 10;
				int amount = 0;
				int receivingBranchIndex;
				long threadSleepingTime;
				int numberOfBranches;
				TransferMessage message;
				while(true){
					System.out.print("");
					numberOfBranches = BRANCHOPERATOR.branchList.size();
					//System.out.println("Number of branches: "+numberOfBranches);
					if(numberOfBranches>=1 && BRANCHOPERATOR.sendingFlag == true){
						receivingBranchIndex  = (int) ((Math.random()*(numberOfBranches-1)) + 1);
						//amount = (int) ((Math.random()*(numberOfBranches)) + 1);
						Random rand = new Random();
						amount = rand.nextInt(11);
						threadSleepingTime = 1000;
						message = new TransferMessage();
						
						try{
							if(receivingBranchIndex > 0){
								receivingBranchIndex--;
							}
							//System.out.println("Receiving Branch index: "+receivingBranchIndex);
							//System.out.println("Ip is: ");
							TTransport ttransport = new TSocket(BRANCHOPERATOR.branchList.get(receivingBranchIndex).getIp(),Integer.valueOf(BRANCHOPERATOR.getBranchList().get(receivingBranchIndex).getPort()));
							ttransport.open();
							TProtocol tprotocol = new TBinaryProtocol(ttransport);
							Branch.Client receiverBranch = new Branch.Client(tprotocol);
							//Branch.Client receiverBranch = new Branch.Client(new TBinaryProtocol(new TSocket(BRANCHOPERATOR.getBranchList().get(receivingBranchIndex).getIp(),Integer.valueOf(BRANCHOPERATOR.getBranchList().get(receivingBranchIndex).getPort()))));
							message.setOrig_branchId(BRANCHOPERATOR.branch);
							//code to generate amount goes here
							if(amount > 0){
								message.setAmount(amount);
								System.out.println("BranchServer(startSending)->"+branchName+" is transfering amount to "+BRANCHOPERATOR.branchList.get(receivingBranchIndex).getName());
								//updateAmount(1, amount);
								System.out.println("BranchServer(updateAmount)->"+branchName+" has balance before sending:"+BRANCHOPERATOR.getBalance());
								synchronized (BRANCHOPERATOR.balance) {
									BRANCHOPERATOR.balance.set(BRANCHOPERATOR.balance.get() - amount);
								}
								System.out.println("BranchServer(updateAmount)->"+branchName+" has balance after sending:"+BRANCHOPERATOR.getBalance());
								int messageId = BranchOperations.amountLastUsedMessageIdMap.get(BRANCHOPERATOR.branchList.get(receivingBranchIndex).getName());
								receiverBranch.transferMoney(message, messageId);
								messageId++;
								BranchOperations.amountLastUsedMessageIdMap.put(BRANCHOPERATOR.branchList.get(receivingBranchIndex).getName(), messageId);
								//counter--;
							}
							ttransport.close();
							snapShotId++;
							Thread.sleep(threadSleepingTime);
							
							
						}catch(Exception e){
							e.printStackTrace();
							System.exit(1);
						}
	
					}
				}
				
			}
			
		});
		sendingThread.start();

	}
	
	public static void main(String[] args){
		int numberOfArgs = args.length;
		//System.out.println(args[1]);
		if(numberOfArgs < 2){
			System.out.println("Invalid number of parameters");
			System.exit(1);
		}
		try{
			PORTNUMBER = Integer.parseInt(args[1]);
		}catch(Exception e){
			System.out.println("Please enter valid port number");
			System.exit(1);
		}	
		try{
			BRANCHOPERATOR = new BranchOperations();
			branchName = args[0];
			BRANCHOPERATOR.branchName = branchName;
			//BRANCHOPERATOR.setBranchName(args[0]);
			PROCESSOR = new Branch.Processor<Branch.Iface>(BRANCHOPERATOR);
			Thread serverThread = new Thread(new Runnable(){
				public void run(){
					TServerTransport tserverTransport;
					TServer tserver;
					try{
						tserverTransport = new TServerSocket(PORTNUMBER);
						tserver = new TThreadPoolServer(new TThreadPoolServer.Args(tserverTransport).processor(PROCESSOR));
						System.out.println("BranchServer(Main)->"+branchName+" successfully opened on port "+PORTNUMBER);
						tserver.serve();
						//tserverTransport.close();
					}catch(Exception e){
						e.printStackTrace();
						System.exit(1);
					}
				}
			});//END OF CREATION OF SERVER THREAD
			serverThread.start();
			startSending();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	
	
}

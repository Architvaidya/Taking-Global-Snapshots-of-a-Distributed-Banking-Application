import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.CipherInputStream;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;

public class BranchOperations implements Branch.Iface{
	
	public static ConcurrentHashMap<Integer, ChannelData> snapShotMapping;
	public static volatile boolean snapShotFlag;
	public static volatile boolean markerFlag;
	//public static volatile int balance;
	public static AtomicInteger balance; 
	public static BranchID branch;
	public static volatile boolean sendingFlag = true;
	public static LocalSnapshot localSnapshot = null;
	public static List<BranchID> branchList;
	public static String branchName;
	public static ConcurrentHashMap<String, Integer> branchIndexMap;
	public static ConcurrentHashMap<String,Integer> amountLastUsedMessageIdMap;
	public static ConcurrentHashMap<String,Integer> amountMessageIDMapping;
	public static ConcurrentHashMap<String, ArrayList<MessageData>> amountMessageDataQueueMap;
	
	
	public BranchOperations(){
		snapShotMapping = new ConcurrentHashMap<Integer, ChannelData>();
		balance = new AtomicInteger(0);
		snapShotFlag = false;
		markerFlag = false;
		sendingFlag = true;
		branch = new BranchID();
		branchList = new ArrayList<BranchID>();
		amountMessageDataQueueMap = new ConcurrentHashMap<String, ArrayList<MessageData>>();
		localSnapshot = new LocalSnapshot();
		amountMessageIDMapping = new ConcurrentHashMap<String, Integer>();
		amountLastUsedMessageIdMap = new ConcurrentHashMap<String,Integer>();
		branchIndexMap = new ConcurrentHashMap<String, Integer>();
		
	}
	@Override
	public void initBranch(int balance, List<BranchID> all_branches) throws SystemException, TException {
		BranchOperations.balance.set(balance);
		int index = 0;
		for(BranchID b:all_branches){
			if(b.getName().equals(BranchOperations.branchName)){
				branch.setIp(b.getIp());
				branch.setName(b.getName());
				branch.setPort(b.getPort());
				
			}
			else{
				branchIndexMap.put(b.getName(), index);
				branchList.add(b);
				amountMessageIDMapping.put(b.getName(), 1);
				amountMessageDataQueueMap.put(b.getName(), new ArrayList<MessageData>());
				amountLastUsedMessageIdMap.put(b.getName(), 1);
				index++;
			}
		}
		//System.out.println("Entered details for branch: "+BranchOperations.branch.getName()+"Balance = "+BranchOperations.balance+"List is: "+BranchOperations.branchList);
	}
	/*public synchronized void updateAmount(int amount){
		
			int balance = BranchOperations.balance;
			balance = balance + amount;
			BranchOperations.balance = balance;
			System.out.println("BracnhOperations(updateAmount)->Amount in "+BranchOperations.branchName+" after adding amount is: "+BranchOperations.balance);		
	}*/

	@Override
	public void transferMoney(TransferMessage message, int messageId) throws SystemException, TException {
		if(amountMessageDataQueueMap.get(message.orig_branchId.getName()).isEmpty()){
			int expectedID = amountMessageIDMapping.get(message.getOrig_branchId().name);
			if(expectedID == messageId){
				System.out.println("BranchOperations(updateAmount)->"+BranchOperations.branchName+" has received amount "+message.getAmount()+" from "+message.orig_branchId.getName()+" its balance before adding is: "+BranchOperations.balance);
				synchronized(BranchOperations.balance){
					BranchOperations.balance.set(BranchOperations.balance.get() + message.getAmount());
				}
				amountMessageIDMapping.put(message.orig_branchId.getName(), messageId+1);
				System.out.println("BracnhOperations(updateAmount)->Amount in "+BranchOperations.branchName+" after adding amount is: "+BranchOperations.balance);
			}
			else{
				MessageData messageData = new MessageData(message.orig_branchId, message.amount,messageId);
				ArrayList<MessageData> m = BranchOperations.amountMessageDataQueueMap.get(message.getOrig_branchId().getName());
				m.add(messageData);
				Collections.sort(m);
				BranchOperations.amountMessageDataQueueMap.put(message.orig_branchId.getName(), m);
			}
		}//if queue is empty
		else{
			int expectedID = amountMessageIDMapping.get(message.getOrig_branchId().name);
			if(expectedID == messageId){
				System.out.println("BranchOperations(updateAmount)->"+BranchOperations.branchName+" has received amount "+message.getAmount()+" from "+message.orig_branchId.getName()+" its balance before adding is: "+BranchOperations.balance);
				synchronized(BranchOperations.balance){
					BranchOperations.balance.set(BranchOperations.balance.get() + message.getAmount());
				}
				amountMessageIDMapping.put(message.orig_branchId.getName(), messageId+1);
				int i = 0;
				ArrayList<MessageData> temp = BranchOperations.amountMessageDataQueueMap.get(message.getOrig_branchId().getName());
				while(amountMessageDataQueueMap.get(message.getOrig_branchId().getName()).isEmpty() == false){
					expectedID = amountMessageIDMapping.get(message.getOrig_branchId().name);
					if(expectedID == amountMessageDataQueueMap.get(message.orig_branchId.getName()).get(i).messageId){
						System.out.println("BranchOperations(updateAmount)->"+BranchOperations.branchName+" has received amount "+message.getAmount()+" from "+message.orig_branchId.getName()+" its balance before adding is: "+BranchOperations.balance);
						synchronized(BranchOperations.balance){
							BranchOperations.balance.set(BranchOperations.balance.get() + message.getAmount());
						}
						amountMessageIDMapping.put(message.orig_branchId.getName(), messageId+1);
						amountMessageDataQueueMap.remove(i);
						i++;
						expectedID = amountMessageIDMapping.get(message.getOrig_branchId().name);
						expectedID++;
						amountMessageIDMapping.put(message.getOrig_branchId().name,expectedID);
					}
					else{
						return;
					}
				}
				BranchOperations.amountMessageDataQueueMap.put(message.getOrig_branchId().getName(), temp);
				System.out.println("BracnhOperations(updateAmount)->Amount in "+BranchOperations.branchName+" after adding amount is: "+BranchOperations.balance);
			}
			//Few more message Ids are expected
			else{
				MessageData messageData = new MessageData(message.orig_branchId, message.amount, messageId);
				ArrayList<MessageData> m = BranchOperations.amountMessageDataQueueMap.get(message.getOrig_branchId().getName());
				m.add(messageData);
				Collections.sort(m);
				BranchOperations.amountMessageDataQueueMap.put(message.orig_branchId.getName(), m);
				int i = 0;
				while(amountMessageDataQueueMap.get(message.getOrig_branchId().getName()).isEmpty() == false){
					expectedID = amountMessageIDMapping.get(message.getOrig_branchId().name);
					if(expectedID == amountMessageDataQueueMap.get(message.orig_branchId.getName()).get(i).messageId){
						System.out.println("BranchOperations(updateAmount)->"+BranchOperations.branchName+" has received amount "+message.getAmount()+" from "+message.orig_branchId.getName()+" its balance before adding is: "+BranchOperations.balance);
						synchronized(BranchOperations.balance){
							BranchOperations.balance.set(BranchOperations.balance.get() + message.getAmount());
						}
						amountMessageIDMapping.put(message.orig_branchId.getName(), messageId+1);
						amountMessageDataQueueMap.remove(i);
						i++;
						expectedID = amountMessageIDMapping.get(message.getOrig_branchId().name);
						expectedID++;
						amountMessageIDMapping.put(message.getOrig_branchId().name,expectedID);
					}
					else{
						return;
					}
				}
				
				
			}//else if queue is not empty
			
			
			
		}
		
		
		Set<Integer> snapShotSet = snapShotMapping.keySet();
		Iterator<Integer> snapShotIterator = snapShotSet.iterator();
		while(snapShotIterator.hasNext()){
			int snapShotNumber = snapShotIterator.next();
			ChannelData newChannelData = snapShotMapping.get(snapShotNumber);
			System.out.println("BranchOperations(transferAmount)->Branch Name for new Channel Data: "+newChannelData.channelStatus.get(message.getOrig_branchId().getName()));
			if(newChannelData.channelStatus.get(message.getOrig_branchId().getName()) == ChannelInsert.R){
				//System.out.println("before setting: "+newChannelData.localSnapShot.messages);
				//newChannelData.localSnapShot.messages.set(branchIndexMap.get(message.getOrig_branchId().getName()), message.amount);
				
				//System.out.println("After setting: "+newChannelData.localSnapShot.messages);
				int index=branchIndexMap.get(message.getOrig_branchId().getName());
				int tempamount=newChannelData.localSnapShot.messages.get(index);
				tempamount=tempamount+message.getAmount();
				newChannelData.localSnapShot.messages.set(index, tempamount);
				//newChannelData.localSnapShot.messages.add(message.getAmount());
				snapShotMapping.put(snapShotNumber, newChannelData);
			}
		}
	}

	@Override
	public void initSnapshot(int snapshotId) throws SystemException, TException {
		ChannelData channelData;
		//TTransport ttransport;
		ConnectionCreator connectionCreator = new ConnectionCreator();
		List<Integer> localSnapShotMessageList = new CopyOnWriteArrayList<Integer>();
		int index = 0;
		while(index < BranchOperations.branchList.size()){
			localSnapShotMessageList.add(index, 0);
			index++;
		}
		BranchOperations.sendingFlag = false;
		System.out.println("BranchOperation(initSnapShot)->Amount transfering stopped, in initSnapShot method");
		LocalSnapshot localSnapshot = new LocalSnapshot();
		localSnapshot.setBalance(BranchOperations.balance.get());
		localSnapshot.setSnapshotId(snapshotId);
		localSnapshot.setMessages(localSnapShotMessageList);
		channelData = new ChannelData();
		System.out.println("BranchOperation(initSnapShot)->Snapshot is inititiated "+branch.getName()+" for snapshot id: "+snapshotId);
		channelData.localSnapShot = localSnapshot;
		channelData.startRecording(branchList);
		snapShotMapping.put(snapshotId, channelData);
		
		for(BranchID b : branchList){
			Branch.Client client = connectionCreator.createConnection(b);
			try{
				System.out.println("BranchOperation(initSnapShot)->Marker sent to branch: "+b.getName()+" for snapshot id: "+snapshotId);
				client.Marker(branch, snapshotId, 0);
				connectionCreator.ttransport.close();
			}catch(TException e){
				e.printStackTrace();
				System.exit(1);
			}
		}
		BranchOperations.sendingFlag = true;
		System.out.println("BranchOperation(initSnapShot)->"+"sending started in initSnapShot");
		
	}

	@Override
	public void Marker(BranchID branchId, int snapshotId, int messageId) throws SystemException, TException {
		ConnectionCreator connectionCreator;
		System.out.println("BranchOperations(Marker)->Marker has come from " + branchId.getName() +" for snapShot Id="+snapshotId);
		ChannelData channelData1;
		ChannelData channelData2;
		try{
			Thread.sleep(1000);
		}catch(InterruptedException e){
			e.printStackTrace();
			System.exit(1);
		}
		
		if(BranchOperations.snapShotMapping.containsKey(snapshotId)){
			channelData1 = BranchOperations.snapShotMapping.get(snapshotId);
			channelData1.channelStatus.put(branchId.getName(), ChannelInsert.N);
			snapShotMapping.put(snapshotId, channelData1);
			channelData2 = BranchOperations.snapShotMapping.get(snapshotId);
			System.out.println("BranchOperations(Marker)->"+channelData2.channelStatus.get(branchId.getName()));
		}
		
		else{
			connectionCreator = new ConnectionCreator();
			BranchOperations.sendingFlag = false;
			List<Integer> localSnapShotMessageList = new CopyOnWriteArrayList<Integer>();
			int index = 0;
			while(index < BranchOperations.branchList.size()){
				localSnapShotMessageList.add(index, 0);
				index++;
			}
			System.out.println("BranchOperations(Marker)->sending stopped inside Marker Method");
			LocalSnapshot localSnapshot = new LocalSnapshot();
			localSnapshot.setBalance(BranchOperations.balance.get());
			localSnapshot.setSnapshotId(snapshotId);
			localSnapshot.setMessages(localSnapShotMessageList);
			
			ChannelData channelData = new ChannelData();
			channelData.startRecording(branchList);
			channelData.localSnapShot = localSnapshot;
			channelData.channelStatus.put(branchId.getName(), ChannelInsert.N);
			snapShotMapping.put(snapshotId, channelData);
			
			for(BranchID b : branchList){
				Branch.Client client = connectionCreator.createConnection(b);
				try{
					System.out.println("BranchOperation(Marker)->Marker sent to branch: "+b.getName()+" for snapshot id: "+snapshotId);
					client.Marker(branch, snapshotId, 0);
					connectionCreator.ttransport.close();
				}catch(TException e){
					e.printStackTrace();
					System.exit(1);
				}
			}
			
		}
		
	}

	@Override
	public LocalSnapshot retrieveSnapshot(int snapshotId) throws SystemException, TException {
		System.out.println("BranchOperations(retrieveSnapShot)Retrieving snapShotID:"+snapshotId);
		ChannelData channelData = snapShotMapping.get(snapshotId);
		LocalSnapshot localSnapShot = channelData.localSnapShot;
		return localSnapShot;
	}
	
	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	


	public boolean isSnapShotFlag() {
		return snapShotFlag;
	}

	public void setSnapShotFlag(boolean snapShotFlag) {
		this.snapShotFlag = snapShotFlag;
	}

	public boolean isMarkerFlag() {
		return markerFlag;
	}

	public void setMarkerFlag(boolean markerFlag) {
		this.markerFlag = markerFlag;
	}

	public AtomicInteger getBalance() {
		return (AtomicInteger) balance;
	}

	public void setBalance(AtomicInteger balance) {
		this.balance = balance;
	}

	public BranchID getBranch() {
		return branch;
	}

	public void setBranch(BranchID branch) {
		this.branch = branch;
	}

	public boolean isSendingFlag() {
		return sendingFlag;
	}

	public void setSendingFlag(boolean sendingFlag) {
		this.sendingFlag = sendingFlag;
	}

	public LocalSnapshot getLocalSnapshot() {
		return localSnapshot;
	}

	public void setLocalSnapshot(LocalSnapshot localSnapshot) {
		this.localSnapshot = localSnapshot;
	}

	public List<BranchID> getBranchList() {
		return branchList;
	}

	public void setBranchList(List<BranchID> branchList) {
		this.branchList = branchList;
	}

	

}

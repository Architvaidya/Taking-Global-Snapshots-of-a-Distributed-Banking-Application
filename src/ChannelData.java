import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelData {
	
	public LocalSnapshot localSnapShot;
	public volatile ConcurrentHashMap<String, ChannelInsert> channelStatus = null;
	ChannelInsert channelInsert;
	
	ChannelData(){
		localSnapShot = new LocalSnapshot();
		channelStatus = new ConcurrentHashMap<String, ChannelInsert>();
		
	}
	
	public void startRecording(List<BranchID> branchList){
		for(BranchID branch : branchList){
			channelStatus.put(branch.getName(), ChannelInsert.R);
		}
	}
	
	public String getChannelData(){
		return "ChannelData [localSnapShot="+localSnapShot+"| channelStatus="+channelStatus+"]";
	}

}

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Channel{
	
	public int snapshotId; // required
	public int balance; // required
	public List<Integer> messages;
	List<String> destinations;
	
	public void Channel(){
		destinations = new CopyOnWriteArrayList<String>();
	}

	public List<String> getDestinations() {
		return destinations;
	}

	public void setDestinations(List<String> destinations) {
		this.destinations = destinations;
	}

	public int getSnapshotId() {
		return snapshotId;
	}

	public void setSnapshotId(int snapshotId) {
		this.snapshotId = snapshotId;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public List<Integer> getMessages() {
		return messages;
	}

	public void setMessages(List<Integer> messages) {
		this.messages = messages;
	}
	
	
	
	

}

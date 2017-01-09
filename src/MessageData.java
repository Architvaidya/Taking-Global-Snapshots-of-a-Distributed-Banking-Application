
public class MessageData implements Comparable<MessageData>{
	
	BranchID branch;
	int amount;
	int messageId;
	
	public MessageData(BranchID branch, int amount, int messageId){
		this.branch = branch;
		this.amount = amount;
		this.messageId = messageId;
	}

	@Override
	public int compareTo(MessageData input) {
		// TODO Auto-generated method stub
		return branch.getName().compareTo(input.branch.getName());
	}

}

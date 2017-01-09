Name:Archit Vaidya
Course: Introduction to Distributed System
Project2


Steps to compile:

1>Navigate to folder 'avaidya4-project2'
2>type bash
3>type 'chmod a+x branch.sh'
4>type 'chmod a+x controller.sh'
5>type make

Steps to execute:

1>Type './controller.sh <Total Amount> <inputfile>'
2>Type './branch.sh <NameOfBranch> <PORTNUMBER>'.   

The recorded snapshots will be displayed on the machine where controller will be executed

Output:
avaidya4@remote05:~/DS/avaidya4-project2_temp8$ ./controller.sh 4000 branches.txt
Socket opened for Branch: branch1
Socket opened for Branch: branch2
Socket opened for Branch: branch3
Socket opened for Branch: branch4
Snapshot for snapshotID: 1
branch2->branch1:44| branch3->branch1:18| branch4->branch1:0| 
SnapShotRetriever(run)->LocalSnapshot(snapshotId:1, balance:1044, messages:[44, 18, 0])
branch1->branch2:0| branch3->branch2:0| branch4->branch2:0| 
SnapShotRetriever(run)->LocalSnapshot(snapshotId:1, balance:1070, messages:[0, 0, 0])
branch1->branch3:0| branch2->branch3:0| branch4->branch3:0| 
SnapShotRetriever(run)->LocalSnapshot(snapshotId:1, balance:955, messages:[0, 0, 0])
branch1->branch4:0| branch2->branch4:0| branch3->branch4:0| 
SnapShotRetriever(run)->LocalSnapshot(snapshotId:1, balance:869, messages:[0, 0, 0])
------------------------------------------------------------------
Snapshot for snapshotID: 2
branch2->branch1:0| branch3->branch1:0| branch4->branch1:7| 
SnapShotRetriever(run)->LocalSnapshot(snapshotId:2, balance:1177, messages:[0, 0, 7])
branch1->branch2:0| branch3->branch2:0| branch4->branch2:22| 
SnapShotRetriever(run)->LocalSnapshot(snapshotId:2, balance:1103, messages:[0, 0, 22])
branch1->branch3:0| branch2->branch3:0| branch4->branch3:0| 
SnapShotRetriever(run)->LocalSnapshot(snapshotId:2, balance:955, messages:[0, 0, 0])
branch1->branch4:0| branch2->branch4:0| branch3->branch4:0| 
SnapShotRetriever(run)->LocalSnapshot(snapshotId:2, balance:736, messages:[0, 0, 0])
------------------------------------------------------------------
Snapshot for snapshotID: 3
branch2->branch1:0| branch3->branch1:0| branch4->branch1:0| 
SnapShotRetriever(run)->LocalSnapshot(snapshotId:3, balance:1239, messages:[0, 0, 0])
branch1->branch2:0| branch3->branch2:15| branch4->branch2:0| 
SnapShotRetriever(run)->LocalSnapshot(snapshotId:3, balance:1243, messages:[0, 15, 0])
branch1->branch3:0| branch2->branch3:0| branch4->branch3:0| 
SnapShotRetriever(run)->LocalSnapshot(snapshotId:3, balance:767, messages:[0, 0, 0])
branch1->branch4:0| branch2->branch4:0| branch3->branch4:0| 
SnapShotRetriever(run)->LocalSnapshot(snapshotId:3, balance:736, messages:[0, 0, 0])
------------------------------------------------------------------
Snapshot for snapshotID: 4
branch2->branch1:0| branch3->branch1:0| branch4->branch1:0| 
SnapShotRetriever(run)->LocalSnapshot(snapshotId:4, balance:1284, messages:[0, 0, 0])
branch1->branch2:0| branch3->branch2:0| branch4->branch2:0| 
SnapShotRetriever(run)->LocalSnapshot(snapshotId:4, balance:1105, messages:[0, 0, 0])
branch1->branch3:0| branch2->branch3:0| branch4->branch3:0| 
SnapShotRetriever(run)->LocalSnapshot(snapshotId:4, balance:875, messages:[0, 0, 0])
branch1->branch4:0| branch2->branch4:0| branch3->branch4:0| 
SnapShotRetriever(run)->LocalSnapshot(snapshotId:4, balance:736, messages:[0, 0, 0])

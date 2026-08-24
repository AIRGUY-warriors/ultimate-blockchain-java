package ds1;
import ds1.util.HashUtils;
/** 
 * Block.java
 * This class represents a block in the blockchain.
 * It contains a priority queue of transactions, a block hash, a previous block hash, and a block number.
 * Transactions are stored in a priority queue based on their fees.
 */
import ds1.util.PriorityQueue;

public class Block implements Comparable<Block> {
    private String blockHash;
    private String stateRootHash;//added by student
    private final String previousHash;
    private final int transactionsPerBlock;
    private final PriorityQueue transactions;
    private final int blockNumber;

    // add required fields here
    

    public Block(String previousHash, int transactionsPerBlock, int blockNumber) {
        this.previousHash = previousHash;
        this.blockHash = "";
        this.transactionsPerBlock = transactionsPerBlock;
        this.transactions = new PriorityQueue(transactionsPerBlock);
        this.blockNumber = blockNumber;
        this.stateRootHash = ""; // not set yet
    }
    
    public void addTransaction(TransactionWithFee t) {
        if (isFull()) {
            throw new IllegalStateException("Block is full");
        }
        transactions.enqueue(t);
    }
    
    public TransactionWithFee getFirstTransaction() {
        return transactions.next();
    }
        
    public boolean isFull() {
        // Check if the block is full
        // A block is considered full if it has reached its transaction limit
        // or is the genesis block
        return transactions.size() >= transactionsPerBlock;
    }
    
    // Getters
    public String getBlockHash() { return blockHash; }
    public String getPreviousHash() { return previousHash; }
    public int getBlockNumber() { return blockNumber; }
    public int getTransactionCount() { return transactions.size(); }

    /** Legacy repOK method for checking class invariants
     * No longer used in the current implementation
     **/
    public boolean repOK() {
        // Check: previousHash == currentHash - 1
        // Check: block number is positive
        return blockHash == previousHash + 1 && blockNumber >= 0;
    }

    /** 
     * Get all transactions in the block as an array
     **/
    public TransactionWithFee[] getTransactions() {
        return transactions.toArray();
    }

    /** 
     * Set the state root hash for this block
     **/
    public void setStateRootHash(String stateRootHash) {
        this.stateRootHash = stateRootHash;//O(1)
    }// the method is O(1)

    /**
     * Compute and set the block hash
     * Requires stateRootHash to be set
     * Block hash is computed as hash(previousHash + stateRootHash + TxHash)
     * where TxHash is the hash of all transactions in the block
     **/
    public String computeAndSetBlockHash() {
        // compute TxHash from current transactions
        TransactionWithFee[] txs = getTransactions();//O(logT*T),see complexity analysis of PriorityQueue.java
        String txHash = computeTxHash(txs);//O(|T|)

        // compute block hash
        this.blockHash = HashUtils.hash(this.previousHash + this.stateRootHash + txHash);//the formula itself is O(1)
        return this.blockHash;
    }//This method is O(logT*T)

    /** 
     * Recommended helper method:
     * Compute the transaction hash for an array of transactions
     * TxHash = hash(Tx1.hash + hash(Tx2.hash + ...))
     **/
    public String computeTxHash(Transaction[] transactions) {
        //we change it into public method so that the UBlockChain.java can use it in repOK()
        // (block is assumed full, but we still take care of empty cases for safety)
        if (transactions == null || transactions.length == 0) {//O(1)
            return HashUtils.hash("");//O(1)
        }

        // Base case: hash(Th0)
        String acc = HashUtils.hash(transactions[0].hash());//O(1)

        // Then: acc = hash(Th1 + acc), acc = hash(Th2 + acc), and so on....
        for (int i = 1; i < transactions.length; i++) {//O(|T|)
            String thi = transactions[i].hash(); //O(1)
            acc = HashUtils.hash(thi + acc); //O(1)
        }
        return acc;
    }//the method  has complexity O(|T|)

    // toString for debugging
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Block Number: ").append(blockNumber).append("\n");
        sb.append("Previous Hash: ").append(previousHash).append("\n");
        sb.append("Block Hash: ").append(blockHash).append("\n");
        sb.append("Transactions:\n");
        for (TransactionWithFee t : transactions.toArray()) {
            sb.append(t.toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public int compareTo(Block o) {
        return Integer.compare(this.blockNumber, o.blockNumber);
    }

    public String getStateRootHash() {
        return this.stateRootHash;//O(1)
    }//this method is O(1)
}
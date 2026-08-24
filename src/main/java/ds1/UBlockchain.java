package ds1;

import ds1.util.HashUtils;

/**
 * UBlockchain.java
 * This class represents an unoptimized blockchain implementation.
 * It extends the abstract ABlockchain class and provides concrete implementations
 * for creating the genesis block, processing blocks, and creating new blocks.
 * Students are expected to implement the missing methods.
 * Can use the inherited methods from ABlockchain for common functionality.
 * Or override them if needed.
 */

public class UBlockchain extends ABlockchain {
    private int initialBalance;//this new field is for repOK()

    public UBlockchain(int transactionsPerBlock, int initialBalance) {
        super(transactionsPerBlock, initialBalance);
        this.initialBalance = initialBalance;
    }

    @Override
    protected Block createGenesisBlock(int initialBalance) {
        // Create genesis block
        Block genesis = new Block("-1", transactionsPerBlock, blockCounter++);
        chain.insertRear(genesis);
        blocksTree.insert(genesis);

        // Initialize balance for address "0"
        balance.updateBalance("0", initialBalance);

        //set state root hash and compute genesis block hash
        genesis.setStateRootHash(balance.getStateHash());
        genesis.computeAndSetBlockHash();

        // Create first block after genesis, using genesis blockHash as previousHash
        Block firstBlock = new Block(genesis.getBlockHash(), transactionsPerBlock, blockCounter++);
        chain.insertRear(firstBlock);
        blocksTree.insert(firstBlock);

        currentBlock = firstBlock;
        return genesis;
    }

    @Override
    public void processCurrentBlockAndStartNewBlock() {
        // Same idea as A2: process txs then start a new block
        // But now we also store stateRootHash and compute blockHash for the processed block before create new block

        processBlockTransactions();//O(logT * M),see detail analysis in ABlockChain.java

        // After balances are updated,get the current global state root hash
        String stateRoot = getStateMPTHash(); //O(1)
        // Save it into the current block
        currentBlock.setStateRootHash(stateRoot);//O(1)
        // Compute and set this block's hash based on previousHash + stateRootHash + txHash
        //Remark here the txHash is computed in computeAndSetBlockHash().
        currentBlock.computeAndSetBlockHash();//O(logT*T),see detail analysis in Block.java
        // Start next block
        createNewBlock();//O(B), see detail analysis in ABlockChain.java
    }//Comlexity analysis:
     //The complexity is max{O(B),O(logT*M)},
     //where B is the number of the blocks in chain,T is the number of transactions in the block
     //M is the max{A&L,T},where A is the number of addresses and L is the length of the addresses, and A&L is max{A,L}

    @Override
    protected void processBlockTransactions() {
        super.processBlockTransactions();
        //Comment by student: here the difference of this method is when the balance is updated,
        //the hash consistency of the SMT balance table should be maintained, but this process is completed in the balanceImp.java
        //So there is no change in the code of this method.
    }

    @Override
    protected void createNewBlock() {
        super.createNewBlock();
        //Comment by student: similar reasaon as before, the difference is in the construction of a new block,
        //As long as the block.java correctly performs, this method will be correct
        //So there is no changes in code implementation.
        
    }

    // Mine a new block from the transaction pool
    // It is similar to addBlock but selects transactions from the pool
    @Override
    public boolean mineBlock() {
        return super.mineBlock();
    }

    @Override
    /** You can use part of old repOK and adapt it to the new structure
     *
     * (1)The stateRoot must be consistent with the balance on each block
     * (2)that currentBlock.previousHash == previousBlock.blockHash.
     * (3)Verify for the last inserted block that blockHash is valid based on its contents.
     *
     */
	public boolean repOK() {
        //First check the old repOK() properties
        if (super.repOK() == false){//O(M2),see detail in ABlockChian.javaa
            return false;//O(1)
        }


        //new checks :stateRoot correctness(1) +linkage(2) + last block hash validity(3)
        // Check(1) and (2):
        // (1): Recompute the blanace from the chain and compare each block's stored stateRootHash
        // (2): When go through the chain, compare each one's hash with the previous one's hash
        Block[] blocks = chain.toArray();//O(B)
        if (blocks.length < 2) {
            //Once a block chain is created, it should be at least has length 2
            return false;//O(1)
        }
        Balance recomputed = new BalanceImp(); //O(1)
        recomputed.updateBalance("0", initialBalance);//Max(O(L),O(A))
        for (int i = 1; i < blocks.length-1; i++) {//O(B)
            //skip the genesis block
            //Also, we do not test the currentBlock(it is not finished), so the index ended at blocks.length-1
            Block b = blocks[i];//O(1)

            //(2)currentBlock.previousHash == previousBlock.blockHash.
            Block prev = blocks[i - 1]; //o(1)
            if (!b.getPreviousHash().equals(prev.getBlockHash())) {
                return false;//O(1)
            }

            TransactionWithFee[] txs = b.getTransactions();//O(logT*T)
            for (int j = 0; j < txs.length; j++) {//O(T)
                TransactionWithFee t = txs[j];//O(1)

                String from = t.getFromAddress();
                String to = t.getToAddress();
                int amount = t.getAmount();
                int fee = t.getFee();

                int fromBalance = recomputed.getBalance(from);//O(L),where L is the length of the address

                if (!t.isReverted()) {
                    if(fromBalance < amount + fee){
                        //if not reverted but the balance is not enough, then something is wrong
                        return false;//O(1)
                    }
                    //here fromBalance >= amount + fee
                    recomputed.updateBalance(from, fromBalance - amount - fee);//Max(O(L),O(A))
                    int toBalance = recomputed.getBalance(to);
                    recomputed.updateBalance(to, toBalance + amount);//Max(O(L),O(A))
                    recomputed.updateBalance("0", recomputed.getBalance("0") + fee);//Max(O(L),O(A))
                } else {
                    // reverted
                    if (fromBalance >= fee / 2) {
                        recomputed.updateBalance(from, fromBalance - fee / 2);//Max(O(L),O(A))
                        recomputed.updateBalance("0", recomputed.getBalance("0") + fee / 2);//Max(O(L),O(A))
                    }
                }
            }//Let's denote the max{A,L} = A&L, then the complexity of the loop is O(T*A&L)
             

            //stateRootHash must match recomputed stateHash
            String expectedStateRoot = recomputed.getStateHash();//O(1)
            String stored = b.getStateRootHash();//O(1)
            if (!expectedStateRoot.equals(stored)) {
                return false;//O(1)
            }

            //Check(3):Verify for the last inserted block that blockHash is valid based on its contents.
            if (i == blocks.length - 2) {
                String prevHash = b.getPreviousHash();
                String stateRoot = b.getStateRootHash();
                String txHash = b.computeTxHash(txs);//O(T)
                String expectedBlockHash =HashUtils.hash(prevHash +stateRoot + txHash);
                String oldBlockHash = b.getBlockHash();
                if (!expectedBlockHash.equals(oldBlockHash)) {
                    return false;
                }
            }

        }//This big loop has complexity O(B*T*A&L),where A&L = max{A,L}

        //Additional checking for currentBlock
        //Since currentBlock is not finished, we only check the linkage
        Block prevBlock = blocks[blocks.length - 2];
        Block expectedCurrentBlock = blocks[blocks.length - 1];
        //Here we check that the last element of the chain is currentBlock
        if(expectedCurrentBlock != currentBlock){
            return false;
        }
        //blocks[blocks.length - 2] is the previous block of currentBlock
        if (!currentBlock.getPreviousHash().equals(prevBlock.getBlockHash())) {
            return false;
        }


        //if everything is ok here, return true
        return true;
    }//This whole repOK()'s complexity is the complexity of the big loop, which is O(B*T*A&L)



    public String getStateMPTHash() {
        return balance.getStateHash();
    }
}

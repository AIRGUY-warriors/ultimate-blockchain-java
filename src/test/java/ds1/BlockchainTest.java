package ds1;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


class BlockchainTest {
    @Test
    void testProcessTransactionAndAddBlock() {
        UBlockchain blockchain = new UBlockchain(2,1000); // 2 transactions per block
        // Load some balances this would generate the genesis block and first block (2 blocks)
        blockchain.requestTransaction("0", "1", 51,0);
        blockchain.requestTransaction("0", "3", 52,0);
        // This would create balances for A and C and add transactions to the first block
        // There will be a second block created already but empty (3 blocks total)
        // Now process transactions
        boolean mined = blockchain.mineBlock();
        blockchain.requestTransaction("1", "2", 50,1);

        mined = blockchain.mineBlock();
        assertFalse(mined, "Block should not be mined as current block is not full");
        assertEquals(1, blockchain.getLastBlock().getTransactionCount(), "Current block should have 1 transaction");

        blockchain.requestTransaction("3", "4", 30,2); // This should fill the block and add it
        // New block should be created (4 blocks total )
        mined = blockchain.mineBlock();
        assertTrue(mined, "Block should be mined after adding transaction");

        // Verify
        assertEquals(4, blockchain.size(), "Blockchain should have 4 blocks after adding full block");
        assertEquals(0, blockchain.getLastBlock().getTransactionCount(),
        "New current block should have 0 transactions");
        // Check balances
        assertEquals(0, blockchain.getBalance("1"), "Balance of 1 should be 0");
        assertEquals(50, blockchain.getBalance("2"), "Balance of 2 should be 50");
        assertEquals(20, blockchain.getBalance("3"), "Balance of 3 should be 20");
        assertEquals(30, blockchain.getBalance("4"), "Balance of 4 should be 30");
        assertTrue(blockchain.repOK(), "Blockchain should be in a valid state");
    }

        @Test
    void testUnfinishedBlockDoesnotAffectBalance() {
        UBlockchain blockchain = new UBlockchain(2,1000); // 2 transactions per block
        // Load some balances
        blockchain.requestTransaction("0", "1", 50,0);
        blockchain.requestTransaction("0", "3", 50,0);
        // This would create balances for A and C and add transactions to the first block
        boolean mined = blockchain.mineBlock();
        // Now process transactions
        blockchain.requestTransaction("1", "2", 50,1);
        mined = blockchain.mineBlock();
        assertFalse(mined, "Block should not be mined as current block is not full");
        assertEquals(1, blockchain.getLastBlock().getTransactionCount(), "Current block should have 1 transaction");
        assertEquals(3, blockchain.size(), "Blockchain should have 3 blocks after adding full block");
        assertEquals(1, blockchain.getLastBlock().getTransactionCount(),
                "New current block should have 1 transactions");
        // Check balances
        assertEquals(50, blockchain.getBalance("1"), "Balance of 1 should be 0");
        assertEquals(0, blockchain.getBalance("2"), "Balance of 2 should be 50");
        assertEquals(50, blockchain.getBalance("3"), "Balance of 3 should be 50");
        assertEquals(0, blockchain.getBalance("4"), "Balance of 4 should be 0");
        assertTrue(blockchain.repOK(), "Blockchain should be in a valid state");
    }

   @Test
    void testGetBalanceNotReverted() {
        UBlockchain blockchain = new UBlockchain(2,1000);
         // Load some balances
        blockchain.requestTransaction("0", "1", 70,0);
        blockchain.requestTransaction("0", "3", 50,10);
        // This will add a block of non-reverted transactions
        blockchain.requestTransaction("1", "2", 50,100);
        blockchain.requestTransaction("3", "4", 30,1); 
        // transaction pool should have 4 transactions now
        assertEquals(4, blockchain.getTransactionPoolSize(),
                "Current block should have 4 transactions");

        boolean mined = blockchain.mineBlock();
        assertTrue(mined, "Block should be mined after adding full block");
        // Only process the first two transactions with more fee 0->C and A->B
        // The second two should be reverted due to insufficient funds
        assertEquals(50, blockchain.getBalance("3"), "Balance of 3 should be 50");
        assertEquals(0, blockchain.getBalance("1"), "Balance of 1 should be 0");
        mined = blockchain.mineBlock();
        assertTrue(mined, "Block should be mined after adding full block");

        assertEquals(70, blockchain.getBalance("1"), "Balance of 1 should be 70");
        assertEquals(0, blockchain.getBalance("2"), "Balance of 2 should be 0");
        assertEquals(19, blockchain.getBalance("3"), "Balance of 3 should be 50-30-1");
        assertEquals(30, blockchain.getBalance("4"), "Balance of 4 should be 30");
        
        
        assertTrue(blockchain.repOK(), "Blockchain should be in a valid state");

    }

    @Test
    void testGetBalanceReverted() {
        UBlockchain blockchain = new UBlockchain(2,1000);
         // Load some balances
        blockchain.requestTransaction("1", "2", 50,1);
        blockchain.requestTransaction("3", "4", 30,2);
        boolean mined = blockchain.mineBlock();
        assertTrue(mined, "Block should be mined after adding full block");

        // All transactions should be reverted due to insufficient funds
        assertEquals(0, blockchain.getBalance("1"), "Balance of 1 should be 0");
        assertEquals(0, blockchain.getBalance("2"), "Balance of 2 should be 0");
        assertEquals(0, blockchain.getBalance("3"), "Balance of 3 should be 0");
        assertEquals(0, blockchain.getBalance("4"), "Balance of 4 should be 0");

        assertTrue(blockchain.repOK(), "Blockchain should be in a valid state");
    }

    // Additional tests can be added here to cover more scenarios, use repOK, and
    // edge cases.
    @Test
    void testTransactionProcessing() {
        UBlockchain blockchain = new UBlockchain(2, 1000);
        blockchain.requestTransaction("1", "2", 50,1);
        blockchain.requestTransaction("3", "4", 30,2);
        assertTrue(blockchain.repOK(), "Blockchain should be in a valid state");
    }

    @Test
    void testInvalidTransaction() {
        UBlockchain blockchain = new UBlockchain(2, 1000);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            blockchain.requestTransaction("1", "2", -10, 1);
        });
        String expectedMessage = "Amount must be positive";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Exception message should indicate invalid amount");
        blockchain.requestTransaction("1", "2", 10, 1);
        assertTrue(blockchain.repOK(), "Blockchain should be in a valid state");
    }
    // Create new test for the ABlockchain class covering new methods and representation invariant.
    @Test
    // Explain this test:
    // This test verifies the functionality of the ABlockchain class, specifically
    // the processTransactionWithFee and mineBlock methods. It checks that transactions
    // are processed correctly, that the balances are updated appropriately, and that
    // the counts of successful and reverted transactions, as well as returned fees,
    // are tracked accurately. Finally, it uses the repOK method to ensure the internal
    // consistency of the blockchain after these operations.
    void testABlockchainMethods() {
        UBlockchain blockchain = new UBlockchain(2,1000); // 2 transactions per block
        // Load some balances this would generate the genesis block and first block (2 blocks)
        // Transactions has same fees but different orders 
        blockchain.requestTransaction("0", "1", 50,1);
        blockchain.requestTransaction("0", "3", 50,1);
        // This would create balances for A and C and add transactions to the first block
        // There will be a second block created already but empty (3 blocks total)
        // Now process transactions
        blockchain.requestTransaction("1", "2", 40,1);
        // mine only one block, but the next block is not full yet
        boolean mined = blockchain.mineBlock();   
        assertEquals(0, blockchain.getLastBlock().getTransactionCount(), "Current block should have 0 transaction");
        // only one block mined as current block is not full
        mined = blockchain.mineBlock();
        assertFalse(mined, "Block should not be mined as current block is not full");
        assertEquals(1, blockchain.getLastBlock().getTransactionCount(), "Current block should have 1 transaction");
        // This transaction should fill the block, and will be processed before A->B due to higher fee
        blockchain.requestTransaction("3", "4", 30,2); // This should fill the block and add it
        mined = blockchain.mineBlock();
        assertTrue(mined, "Block should be mined after adding transaction");
        // New block should be created (4 blocks total )
        // Verify
        assertEquals(4, blockchain.size(), "Blockchain should have 4 blocks after adding full block");
        assertEquals(0, blockchain.getLastBlock().getTransactionCount(),
                "New current block should have 0 transactions");
        // Check balances
        assertEquals(9, blockchain.getBalance("1"), "Balance of 1 should be 10-1");
        assertEquals(40, blockchain.getBalance("2"), "Balance of 2 should be 51-1");
        assertEquals(18, blockchain.getBalance("3"), "Balance of 3 should be 20-2");
        assertEquals(30, blockchain.getBalance("4"), "Balance of 4 should be 30");
        assertTrue(blockchain.repOK(), "Blockchain should be in a valid state");

    }

    // add test for reverted transactions count and returned fees
    @Test
    void testRevertedTransactionsAndReturnedFees() {
        UBlockchain blockchain = new UBlockchain(2,1000); // 2 transactions per block
        // Load some balances this would generate the genesis block and first block (2 blocks)
        blockchain.requestTransaction("0", "1", 50,0);
        blockchain.requestTransaction("0", "3", 50,0);
        // This would create balances for A and C and add transactions to the first block
        boolean mined = blockchain.mineBlock();
        // Now process transactions
        blockchain.requestTransaction("1", "2", 60,4); // This will be reverted
        blockchain.requestTransaction("3", "4", 30,2); // This will be processed
        mined = blockchain.mineBlock();
        assertTrue(mined, "Block should be mined after adding full block");
        assertEquals(1, blockchain.getRevertedTransactionsCount(), "There should be 1 reverted transaction");
        assertEquals(3, blockchain.getSuccessfulTransactionsCount(), "There should be 3 successful transactions");
        assertEquals(2, blockchain.getReturnedFees(), "Returned fees should be 2");
        assertTrue(blockchain.repOK(), "Blockchain should be in a valid state");    
    }

    // test for getBlockByNumber
    @Test
    void testGetBlockByNumber() {
        UBlockchain blockchain = new UBlockchain(2,1000); // 2 transactions per block
        // Load some balances this would generate the genesis block and first block (2 blocks)
        blockchain.requestTransaction("0", "1", 50,0);
        blockchain.requestTransaction("0", "3", 50,0);
        // This would create balances for A and C and add transactions to the first block
        boolean mined = blockchain.mineBlock();
        // Now process transactions
        blockchain.requestTransaction("1", "2", 40,1);
        mined = blockchain.mineBlock();
        assertFalse(mined, "Block should not be mined as current block is not full");

        // Get block by number
        Block block1 = blockchain.getBlockByNumber(1);
        assertEquals(1, block1.getBlockNumber(), "Block number should be 1");
        // check transactions in block1
        TransactionWithFee[] transactionsBlock1 = (TransactionWithFee[]) block1.getTransactions();
        assertEquals(2, transactionsBlock1.length, "Block 1 should have 2 transactions");

        Block block2 = blockchain.getBlockByNumber(2);
        assertEquals(2, block2.getBlockNumber(), "Block number should be 2");
        TransactionWithFee[] transactionsBlock2 = (TransactionWithFee[]) block2.getTransactions();
        assertEquals(1, transactionsBlock2.length, "Block 2 should have 1 transaction");
        assertTrue(blockchain.repOK(), "Blockchain should be in a valid state");
    }

    // test missing methods in ABlockchain
    @Test
    void testGetTransactionPoolSize() {
        UBlockchain blockchain = new UBlockchain(2,1000); // 2 transactions per block
        blockchain.requestTransaction("0", "1", 50,1);
        blockchain.requestTransaction("0", "3", 50,2);
        assertEquals(2, blockchain.getTransactionPoolSize(), "Transaction pool size should be 2");
        blockchain.requestTransaction("1", "2", 40,3);
        assertEquals(3, blockchain.getTransactionPoolSize(), "Transaction pool size should be 3");
        assertTrue(blockchain.repOK(), "Blockchain should be in a valid state");
    }

    // test hash of stateMPT after some balance updates
    @Test
    void testStateMPTHashAfterBalanceUpdates() {
        UBlockchain blockchain = new UBlockchain(2,1000); // 2 transactions per block
        // Load some balances this would generate the genesis block and first block (2 blocks)
        blockchain.requestTransaction("0", "1", 50,0);
        blockchain.requestTransaction("0", "11", 50,0);
        // This would create balances for A and C and add transactions to the first block
        boolean mined = blockchain.mineBlock();
        String stateMPTHash1 = blockchain.getStateMPTHash();
        assertEquals("63236c10", stateMPTHash1);
        // Now process transactions
        blockchain.requestTransaction("1", "2", 40,1);
        blockchain.requestTransaction("11", "22", 30,2); // This should fill the block and add it
        mined = blockchain.mineBlock();
        String stateMPTHash2 = blockchain.getStateMPTHash();
        assertEquals("4e2e64bd", stateMPTHash2);
        assertNotEquals(stateMPTHash1, stateMPTHash2, "State MPT hash should change after balance updates");
        assertTrue(blockchain.repOK(), "Blockchain should be in a valid state");
    }

    // ---------------------The follwoing edge cases tests are added by students-----------------------
    @Test
    void testNewBlockchainHasGenesisAndEmptyCurrentBlock() {
        int initialBalance = 1000;
        int tpb = 2; // transactionsPerBlock
        UBlockchain blockchain = new UBlockchain(tpb, initialBalance);

        // size() = genesis bolck + currentBlock
        assertEquals(2, blockchain.size(),"New blockchain should have genesis + empty current block");

        // check genesis block
        Block genesis = blockchain.getBlock(0);
        assertEquals(0, genesis.getBlockNumber(), "Genesis block number should be 0");
        assertEquals("-1", genesis.getPreviousHash(), "Genesis previous hash should be 0");
        assertEquals(0, genesis.getTransactionCount(), "Genesis should have 0 transactions");

        // check current block（empty）
        Block current = blockchain.getLastBlock();
        assertEquals(1, current.getBlockNumber(), "Current block should be number 1");
        assertEquals(genesis.getBlockHash(), current.getPreviousHash(),"Current block should link to genesis by hash");
        assertEquals(0, current.getTransactionCount(),"Current block should start empty");

        //check other fields
        assertEquals(0, blockchain.getTransactionPoolSize(),"New blockchain should have empty transaction pool");
        assertEquals(initialBalance, blockchain.getBalance("0"),"Miner address 0 should start with initial balance");
        assertEquals(0, blockchain.getBalance("A"),"Unknown address should have 0 balance");

        assertEquals(0, blockchain.getSuccessfulTransactionsCount());
        assertEquals(0, blockchain.getRevertedTransactionsCount());
        assertEquals(0, blockchain.getReturnedFees());

        assertTrue(blockchain.repOK(), "repOK should hold for a fresh blockchain");
    }

    @Test
    void testSingleSuccessfulTransactionMinedIntoSingleBlock() {
        int initialBalance = 1000;
        UBlockchain blockchain = new UBlockchain(1, initialBalance); //1 transaction per bolck

    
        blockchain.requestTransaction("0", "123", 100, 10);
        boolean mined = blockchain.mineBlock();
        assertTrue(mined, "Block should be mined when capacity is reached");

        //genesis + already mined block + new empty current block
        assertEquals(3, blockchain.size(),"Blockchain should have genesis + mined block + new current block");

        //the current mined block
        Block txBlock = blockchain.getBlock(1);
        assertEquals(1, txBlock.getBlockNumber());
        assertEquals(1, txBlock.getTransactionCount());

        TransactionWithFee[] txs = txBlock.getTransactions();
        assertEquals(1, txs.length);
        TransactionWithFee t = txs[0];

        assertEquals("0", t.getFromAddress());
        assertEquals("123", t.getToAddress());
        assertEquals(100, t.getAmount());

        assertEquals(initialBalance - 100, blockchain.getBalance("0"));
         //fee is always returned to genesis if sender is genesis itself
        assertEquals(100, blockchain.getBalance("123"));


        assertEquals(1, blockchain.getSuccessfulTransactionsCount());
        assertEquals(0, blockchain.getRevertedTransactionsCount());
        assertEquals(0, blockchain.getReturnedFees());

        assertTrue(blockchain.repOK(), "repOK should hold after a single successful transaction");
    }


    //Test illegal inputs situations
    @Test
    void testRequestTransactionZeroAmount() {
        UBlockchain blockchain = new UBlockchain(1, 1000);
        assertThrows(IllegalArgumentException.class, () ->
                blockchain.requestTransaction("123", "456", 0, 1),
                "Amount 0 should be rejected");
        assertTrue(blockchain.repOK());
    }

    @Test
    void testRequestTransactionNegativeAmount() {
        UBlockchain blockchain = new UBlockchain(1, 1000);
        assertThrows(IllegalArgumentException.class, () ->
                blockchain.requestTransaction("123", "456", -10, 1),
                "Negative amount should be rejected");
        assertTrue(blockchain.repOK());
    }

    @Test
    void testRequestTransactionRejectsNegativeFee() {
        UBlockchain blockchain = new UBlockchain(1, 1000);
        assertThrows(IllegalArgumentException.class, () ->
                blockchain.requestTransaction("123", "456", 10, -1),
                "Negative fee should be rejected");
        assertTrue(blockchain.repOK());
    }

    //This test is to test the transaction is reverted, and also sender is not able to pay half fee
    //This test will check if all fee is returned to the sender
    @Test
    void testRvertedInsufficientBalanceWillCauseFeeAllReturned() {
        int initialBalance = 100;
        UBlockchain blockchain = new UBlockchain(1, initialBalance);

        //balance of 123 will be initialized to 0 after this transaction
        blockchain.requestTransaction("123", "456", 50, 10);
        boolean mined = blockchain.mineBlock();
        assertTrue(mined, "Block should still be mined even if transaction fails");

        //the transaction should be reverted
        Block txBlock = blockchain.getBlock(1);
        TransactionWithFee[] txs = txBlock.getTransactions();
        assertEquals(1, txs.length);
        assertTrue(txs[0].isReverted(),"Transaction should be reverted when sender has insufficient balance");

        //all balances are not changed
        assertEquals(0, blockchain.getBalance("123"));
        assertEquals(0, blockchain.getBalance("456"));
        assertEquals(initialBalance, blockchain.getBalance("0"));

        //returned fee should be 0.
        assertEquals(0, blockchain.getSuccessfulTransactionsCount());
        assertEquals(1, blockchain.getRevertedTransactionsCount());
        assertEquals(0, blockchain.getReturnedFees());

        assertTrue(blockchain.repOK());
    }

    //This test is to test the transaction is reverted, but sender is able to pay half fee
    //This test will check if half fee is returned to the sender
    @Test
    void testRvertedSufficientBalanceWillCauseFeeHalfReturned() {
        int initialBalance = 100;
        UBlockchain blockchain = new UBlockchain(2, initialBalance);
        blockchain.requestTransaction("0", "123", 5, 10);


        blockchain.requestTransaction("123", "456", 50, 10);
        //123 can not afford transaction but can afford half fee, 5 exactly
        boolean mined = blockchain.mineBlock();
        assertTrue(mined, "Block should still be mined even if transaction fails");

        //the second transaction should be reverted
        Block txBlock = blockchain.getBlock(1);
        TransactionWithFee[] txs = txBlock.getTransactions();
        assertEquals(2, txs.length);
        //note that the second transaction is the one with greter order,that is the transaction 123 to 456
        assertTrue(txs[1].isReverted(),"Transaction should be reverted when sender has insufficient balance");

        //A should have 5-5=0 balance
        assertEquals(0, blockchain.getBalance("123"));
        assertEquals(0, blockchain.getBalance("123"));
        assertEquals(initialBalance, blockchain.getBalance("0"));


        //returned fee should be half fee.
        assertEquals(1, blockchain.getSuccessfulTransactionsCount());
        assertEquals(1, blockchain.getRevertedTransactionsCount());
        assertEquals(5, blockchain.getReturnedFees());

        assertTrue(blockchain.repOK());
    }

    @Test
    void testRepOkDetectsError() {
        UBlockchain blockchain = new UBlockchain(1, 1000);
        blockchain.requestTransaction("0", "123", 10, 0);
        blockchain.mineBlock();
        assertTrue(blockchain.repOK(), "Before manual revert, repOK should be true");

        
        Block block1 = blockchain.getBlock(1);
        TransactionWithFee[] txs = block1.getTransactions();
        assertEquals(1, txs.length);
        assertFalse(txs[0].isReverted(), "Transaction should initially be successful");

        //Remark: txs stores objects(transactions)itself, any change in txs will affect the object in blockchain
        txs[0].revert();//manually change the field to create an error

        //reverted counts will be wrong, so repOK will detect the error.
        assertFalse(blockchain.repOK(),"repOK should be false after manually reverting a previously successful transaction");
    }

}
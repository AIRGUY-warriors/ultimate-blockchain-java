package ds1;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class BlockTest { 
     // Create tests for Block class here. Respect the signatures of its methods:
     // Use information from Block.java and Transaction.java as needed as well as SBlockchain.java and Blockchain.java for context.
    @Test
    void testAddTransactionAndIsFull() {
        Block block = new Block("0", 2, 1); // previousHash="0", transactionsPerBlock=2, blockNumber=1
        TransactionWithFee t1 = new TransactionWithFee("1", "2", 50,0);
        TransactionWithFee t2 = new TransactionWithFee("3", "4", 30,0);
        block.addTransaction(t1);
        block.addTransaction(t2);
        assertTrue(block.isFull(), "Block should be full after adding 2 transactions");
    }
    @Test
    void testGetFirstTransaction() {
        Block block = new Block("0", 2, 1); // previousHash="0", transactionsPerBlock=2, blockNumber=1
        TransactionWithFee t1 = new TransactionWithFee("1", "2", 50, 1);
        block.addTransaction(t1);
        assertEquals(t1, block.getFirstTransaction(), "First transaction should be t1");
        TransactionWithFee t2 = new TransactionWithFee("3", "4", 30, 2);
        block.addTransaction(t2);
        assertEquals(t2, block.getFirstTransaction(), "First transaction should still be t2");
        TransactionWithFee[] transactions = (TransactionWithFee[]) block.getTransactions();
        assertEquals(2, transactions.length, "There should be 2 transactions in the block");
        assertEquals(t1, transactions[1], "Second transaction should be t1");
    }
    @Test
    void testRemoveTransaction() {
        Block block =   new Block("0", 2, 1);
        TransactionWithFee t1 = new TransactionWithFee("1", "2", 50,2);
        TransactionWithFee t2 = new TransactionWithFee("3", "4", 30,1);
        block.addTransaction(t1);
        block.addTransaction(t2);
        TransactionWithFee[] transactions = (TransactionWithFee[]) block.getTransactions();
        assertEquals(2, transactions.length, "There should be 2 transactions in the block");
        assertEquals(t1, transactions[0], "First transaction should be t1");
        assertEquals(t2, transactions[1], "Second transaction should be t2");

    }
    // revert transaction test
    @Test
    void testRevertTransaction() {
        Block block = new Block("0", 2, 1);
        TransactionWithFee t1 = new TransactionWithFee("1", "2", 50,0);
        block.addTransaction(t1);
        t1.revert();
        TransactionWithFee revertedT1 = (TransactionWithFee) block.getFirstTransaction();
        TransactionWithFee[] transactions = (TransactionWithFee[]) block.getTransactions();
        assertEquals(1, transactions.length, "There should be 1 transaction in the block");
        assertTrue(revertedT1.isReverted(), "Transaction t1 should be marked as reverted");
    }

    // Test computeTxHash
    @Test
    void testComputeTxHash() {
        Block block = new Block("0", 2, 1);
        TransactionWithFee t1 = new TransactionWithFee("1", "2", 50,0);
        TransactionWithFee t2 = new TransactionWithFee("3", "4", 30,0);
        block.addTransaction(t1);
        block.addTransaction(t2);
        block.setStateRootHash("stateRootHash");
        String computedHash = block.computeAndSetBlockHash();
        assertEquals("d31b8e0", computedHash);
    }


    //-----------------The fllowing edge cases tests are added by student------------
        @Test
    void testEmptyBlockHasNoTransactions() {
        Block block = new Block("0", 2, 1);

        assertEquals(0, block.getTransactionCount(),"Empty block should have 0 transactions");
        assertFalse(block.isFull(), "Empty block should not be full");

        assertEquals(0, block.getTransactions().length,"getTransactions on empty block should return empty array");
    }

    @Test
    void testSingleTransactionBlock() {
        Block block = new Block("0", 1, 1);
        TransactionWithFee tx = new TransactionWithFee("123", "456", 10, 1);

        block.addTransaction(tx);
        assertEquals(1, block.getTransactionCount());
        assertTrue(block.isFull(),"Block with capacity 1 should be full after one transaction");

        // getFirstTransaction should return the transaction(as a object)itself
        TransactionWithFee first = block.getFirstTransaction();
        assertSame(tx, first,"getFirstTransaction should return the same transaction that was added");

        TransactionWithFee[] all = block.getTransactions();
        assertEquals(1, all.length);
        assertSame(tx, all[0], "getTransactions()[0] should be the same transaction");
    }

    @Test
    void testAddTransactionToFullBlockThrows() {
        Block block = new Block("0", 1, 1);
        block.addTransaction(new TransactionWithFee("123", "456", 10, 0));
        assertTrue(block.isFull());
        assertThrows(IllegalStateException.class, () ->
                block.addTransaction(new TransactionWithFee("78", "98", 20, 0)));
    }

}

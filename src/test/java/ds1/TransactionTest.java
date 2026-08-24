package ds1;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TransactionTest {

    @Test
    void testNewTransactionWithFee() {
        TransactionWithFee tx = new TransactionWithFee("123", "456", 50, 5);

        //check fiedls
        assertEquals("123", tx.getFromAddress());
        assertEquals("456", tx.getToAddress());
        assertEquals(50, tx.getAmount());

        //check defult reverted should be false
        assertFalse(tx.isReverted(),"New transaction should not be reverted by default");
    }

    @Test
    void testRevertChangesRevertedFlag() {
        TransactionWithFee tx = new TransactionWithFee("123", "456", 10, 1);
        assertFalse(tx.isReverted(),"Defult should be not reverted");

        tx.revert();

        assertTrue(tx.isReverted(),"Calling revert() should set the reverted flag to true");
    }

    @Test
    void testCompareToByFeeAndOrder() {
        //First test TransactionWithFee
        TransactionWithFee lowFee = new TransactionWithFee("123", "456", 10, 1);
        TransactionWithFee highFee = new TransactionWithFee("123", "456", 10, 5);

        assertTrue(lowFee.compareTo(highFee) < 0,"Lower fee transaction should be 'less than' higher fee transaction");
        assertTrue(highFee.compareTo(lowFee) > 0,"Higher fee transaction should be 'greater than' lower fee transaction");

        //Then test TransactionWithOrder
        TransactionWithOrder order1 = new TransactionWithOrder("123", "456", 10, 5, 1);
        TransactionWithOrder order2 = new TransactionWithOrder("123", "456", 10, 5, 2);

        
        assertTrue(order1.compareTo(order2) > 0,"Lower order should be considered higher priority according to compareTo");
        assertTrue(order2.compareTo(order1) < 0,"Higher order should be considered lower priority according to compareTo");
    }
}

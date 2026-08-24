package ds1;
import ds1.util.ListoverLinkedList;
import ds1.util.Sequence;
import ds1.util.StateSMT;
/** 
 * BalanceImp.java
 * This class implements the Balance tree to store address-balance pairs.
 * It provides methods to update balances, retrieve balances, get all addresses, and compute total supply.
 */
public class BalanceImp implements Balance {
    // implement use StateMPT
    private StateSMT stateSMT ;
    int totalSupply;
    //add a new auxilary filed to show all addresses explicitly
    private Sequence<String> addresses;
    
    public BalanceImp() {
        this.stateSMT = new StateSMT();
        this.addresses = new ListoverLinkedList<>();
        this.totalSupply = 0;
    }

    @Override
    public void updateBalance(String address, int newBalance) {
        int oldBalance = stateSMT.get(address);//O(L),where L is the length of address
        //if the address does not exist, then the returning value will be 0

        // If this the address does not exist, record it
        if (oldBalance == 0 && !containsAddress(address)) {
            //oldBalance==0 has two possibilities:
            //1. the address exist and balance == 0
            //2. the address does not exist and balance == null
            //so we need a new check !containsAddress(address) to make sure that 
            //it is the second situation.
            addresses.insertRear(address);//O(A),where A is the number of existing addresses
        }

        // Update total supply
        totalSupply += (newBalance - oldBalance);//O(1)
        //even the address does not exists before, it is correctly seted,
        //becasue in this case totalSupply += newBalance

        // Update SMT
        stateSMT.update(address, newBalance);//O(L)
    }//So the method is Max(O(L),O(A))

    //the helper function for updateBalance()
    private boolean containsAddress(String address) {
        for (int i = 0; i < addresses.length(); i++) {
            if (addresses.at(i).equals(address)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getBalance(String address) {
        return stateSMT.get(address);
    }

    @Override
    public String[] getAllAddresses() {
        String[] result = new String[addresses.length()];
        for (int i = 0; i < addresses.length(); i++) {
            result[i] = addresses.at(i);
        }
        return result;
    }

    @Override
    public int totalSupply() {
        // Implementation
        return totalSupply;
    }

    /**
     * repOK method to check class invariants 
     * remember to check also the StateMPT repOK
    */
    public boolean repOK() {
        if (!stateSMT.repOK()) {
            return false;
        }

        if (totalSupply < 0){
            return false;
        }

        int sum = 0;
        for (int i = 0; i < addresses.length(); i++) {
            String addr = addresses.at(i);
            sum += getBalance(addr);
        }
        return sum == totalSupply;
    }

    // toString for debugging
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BalanceImp[totalSupply=").append(totalSupply).append(", addresses={");
        String[] addresses = getAllAddresses();
        for (int i = 0; i < addresses.length; i++) {
            String address = addresses[i];
            sb.append(address).append(":").append(getBalance(address));
            if (i < addresses.length - 1) sb.append(", ");
        }
        sb.append("}]");
        return sb.toString();
    }
    // Get root hash of the StateMPT
    @Override
    public String getStateHash() {
        return stateSMT.getRootHash();
    }

}
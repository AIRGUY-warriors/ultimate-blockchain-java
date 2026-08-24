package ds1.util;

/**
 * SMTNode represents a node in the simplified Merkle Trie
 **/
class SMTNode {
    // Recall nodes has 10 children for numeric addresses
    // and may store a balance if it's a leaf
    // and a hash for the node

    SMTNode[] children;
    SMTNode parent;
    Integer balance; //The default value of a Integer is null.
    String hash;
    //we do not have the field boolean,because we can tell if the address is valid or not by
    //looking at the field balance, if it is NULL, then the address does not exist.

    public SMTNode() {
        this.children = new SMTNode[10];
        this.parent = null;
        this.balance = null;
        this.hash = computeHash(); // initial hash for empty node
    }

    /**
     * Compute the hash of this node based on its balance and children's hashes
     **/
    public String computeHash() {
        String balanceOrNull = (balance == null) ? "null" : String.valueOf(balance);//O(1)

        StringBuilder sb = new StringBuilder();//O(1)
        sb.append(balanceOrNull);//O(1)

        for (int i = 0; i < 10; i++) {//O(10) = O(1)
            if (children[i] == null) {
                sb.append("0");//O(1)
            } else {
                sb.append(children[i].hash);//O(1)
            }
        }

        return HashUtils.hash(sb.toString());//O(1)
    }//This method is O(1)

    /**
     * Update the hash of this node based on its balance and children's hashes
     **/
    public void updateHash() {
        this.hash = computeHash();//O(1), see complexity analysis of computeHash()
    }//this method is O(1)
}

/**
 * StateSMT represents a simplified Simple Merkle Trie
 * for managing account balances in a blockchain
 **/
public class StateSMT {
    private SMTNode root;

    public StateSMT() {
        this.root = new SMTNode();
    }

    //Return current root hash (stateRootHash).
    public String getRootHash() {
        return root.hash;//O(1)
    }//The method is O(1)

    // Get balance for an address; if absent, return 0
    public int get(String address) {
        SMTNode node = findNode(address);//O(L),see complexity analysis of findNode()
        if (node == null || node.balance == null) {
            return 0;//O(1)
        }
        return node.balance;//O(1)
    }//So the method is O(L)

    /**
     * Insert/update an address's balance.
     * After changing the leaf balance, propagate hash updates upwards.
     */
    public void update(String address, int newBalance) {
        SMTNode curr = root;

        for (int i = 0; i < address.length(); i++) {//O(L),where L is address length
            char ch = address.charAt(i);//O(1)
            int idx = ch - '0'; // 0..9

            if (idx < 0 || idx > 9) {
                throw new IllegalArgumentException("Address must be from ‘0...9’");//O(1)
            }

            if (curr.children[idx] == null) {
                //if the childeren does not exist,then create one
                SMTNode child = new SMTNode();//O(1)
                child.parent = curr; //O(1)
                curr.children[idx] = child;//O(1)
                //here we do not update the hash becasue we will update when finishing.
            }
            curr = curr.children[idx];//O(1)
        }//This loop is O(L)
        curr.balance = newBalance;//O(1)

        // propagate hash changes to root
        while (curr != null) {//O(L),where L is the address length
            //remember that only the root has a parent NULL
            curr.updateHash();//O(1),see complexity analysis of updateHash()
            curr = curr.parent;//O(1)
        }//this loop is O(L)
    }//Two loops in this method, each with O(L),complexity
    //So the method is O(L)

    private SMTNode findNode(String address) {
        SMTNode curr = root;//O(1)

        for (int i = 0; i < address.length(); i++) {//O(L),where L is the length of address
            char ch = address.charAt(i);//O(1)
            int idx = ch - '0';//O(1)

            if (idx < 0 || idx > 9) {
                return null;//O(1)
            }

            if (curr.children[idx] == null) {
                return null;//O(1)
            }
            curr = curr.children[idx];//O(1)
        }//The loop is O(L)
        return curr;//O(1)
    }//this method is O(L)

    //Basic repOK() for StateSMT, checks the hash consistency recursively
    public boolean repOK() {
        return repOKNode(root);
    }

    private boolean repOKNode(SMTNode node) {
        if (node == null) return true;

        //check if the hash of the node is determined by their children
        String expected = node.computeHash();
        if (!expected.equals(node.hash)) {
            return false;
        }

        //then check is all the children of the node also satisfies recursively.
        for (int i = 0; i < 10; i++) {
            if (!repOKNode(node.children[i])) {
                return false;
            }
        }
        return true;
    }

}

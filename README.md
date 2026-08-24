# Ultimate Blockchain in Java

An educational blockchain simulation implemented in Java using custom data structures. The project combines fee-prioritized transaction processing with transaction hashing and a Simplified Merkle Trie for maintaining account-state integrity.

## Features

- Fee-prioritized transaction pool
- Transaction ordering based on fee and insertion order
- Custom max heap and priority queue
- AVL tree for block retrieval by block number
- Simplified Merkle Trie for account balances
- Transaction hash aggregation
- State root hash generation
- Cryptographically linked blocks
- Successful and reverted transaction tracking
- Representation invariant validation with `repOK()`
- JUnit tests for blockchain operations and edge cases

## Block Integrity

Each processed block stores:

- The hash of the previous block
- The current account-state root hash
- The aggregated transaction hash

The block hash is computed using:

```text
blockHash = Hash(previousHash + stateRootHash + txHash)
```

This structure allows changes to block transactions or account balances to be detected through hash verification.

## Simplified Merkle Trie

Account balances are stored in a Simplified Merkle Trie.

Each address:

- Contains only numeric characters from `0` to `9`
- Is represented as a path through the trie
- Maps to an account balance

Each trie node maintains a hash derived from its balance and child hashes. When a balance changes, hashes are updated from the modified node back to the root.

Main operations:

- Balance lookup: `O(L)`
- Balance update: `O(L)`
- State root retrieval: `O(1)`

`L` represents the length of an address.

## Custom Data Structures

The project includes custom implementations of:

- AVL tree
- Binary search tree
- Max heap
- Priority queue
- Linked-list queue
- Generic sequence
- Simplified Merkle Trie

These structures are used instead of Java collection classes for the main blockchain operations.

## Transaction Processing

Transactions are first inserted into a transaction pool.

The transaction-pool priority is based on:

```text
priority = fee + 1 / insertionOrder
```

Transactions with higher fees are prioritized. When fees are equal, transactions inserted earlier receive higher priority.

A successful transaction:

1. Deducts the amount and transaction fee from the sender.
2. Transfers the amount to the receiver.
3. Transfers the fee to address `0`.

A transaction is reverted when the sender has insufficient balance. Depending on the available balance, part of the transaction fee may still be collected.

## Project Structure

```text
ultimate-blockchain-java/
├── pom.xml
├── README.md
├── LICENSE
├── .gitignore
└── src/
    ├── main/
    │   └── java/
    │       └── ds1/
    │           ├── ABlockchain.java
    │           ├── AddressBalancePair.java
    │           ├── Balance.java
    │           ├── BalanceImp.java
    │           ├── Block.java
    │           ├── Blockchain.java
    │           ├── Transaction.java
    │           ├── TransactionWithFee.java
    │           ├── TransactionWithOrder.java
    │           ├── UBlockchain.java
    │           └── util/
    │               ├── AVLTree.java
    │               ├── BinarySearchTree.java
    │               ├── HashUtils.java
    │               ├── LinkedListQueue.java
    │               ├── ListoverLinkedList.java
    │               ├── MaxHeapArray.java
    │               ├── PriorityQueue.java
    │               ├── Queue.java
    │               ├── Sequence.java
    │               └── StateSMT.java
    └── test/
        └── java/
            └── ds1/
                ├── BalanceImpTest.java
                ├── BlockchainTest.java
                ├── BlockTest.java
                └── TransactionTest.java
```

## Technologies

- Java
- Maven
- JUnit 5

## Requirements

- Java Development Kit
- Maven 3

## Build and Test

Clone the repository:

```bash
git clone https://github.com/AIRGUY-warriors/ultimate-blockchain-java.git
```

Enter the project directory:

```bash
cd ultimate-blockchain-java
```

Run the tests:

```bash
mvn test
```

Compile the project without running tests:

```bash
mvn compile
```

## Testing

The test suite covers:

- Balance insertion and updates
- Total supply consistency
- State root hash changes
- Addresses with shared trie prefixes
- Transaction fee and insertion-order comparison
- Successful transactions
- Reverted transactions
- Returned fee tracking
- Block capacity
- Transaction aggregation
- Block retrieval by number
- Blockchain representation invariants

## Educational Scope

This project is an educational blockchain simulation. It does not implement networking, distributed consensus, digital signatures, wallets, or persistent blockchain storage.

The included hash utility provides deterministic integrity checks for the simulation. It is not intended to provide production-grade cryptographic security.

## Attribution

This project was developed from an educational starter framework. The blockchain extensions, state-integrity mechanisms, custom data-structure integration, representation checks, and additional tests were completed as part of the project implementation.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.
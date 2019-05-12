# Oulipo Machine
A Xanalogical System for the Creation of New Literature

### Goals and Features
The goal of OulipoMachine is to build a complete xanalogical system with bidirectional linking and with micropayments for content providers.

The designs and interactions are inspired by
[Udanax Green](http://udanax.xanadu.com/green/index.html) (Xanadu 88.1)

Current features include
* Keystore management and server authentication
* Server node creation
* Adding users
* Creating document meta-data
* Document and link access control
* Creating Links between documents
* Finding links
* Retrieve Endsets (from/to VSpans)
* SPARQL queries of backend data
* REST queries 

Future features will include:
* Distributed p2p architecture (replacing Federated Servers)
* Block-chained documents with post-quantum cryptography
* Localization and language support
* Optional Encryption of Text Streams
* JavaFX Browser and Content Viewer
* JavaFX Content Publisher
* Viewing of links between documents
* Access tokens for temporary permissions to access/edit documents
* Micropayments
* Keystore and Security UI
* Search document text
* Assistants for writing constrained literature (in the Oulipo sense) and non-sequential writing

### 17 Principles of Xanadu
The August 2017 release of OulipoMachine meets 5 of the 17 [Principles of Xanadu](https://en.wikipedia.org/wiki/Project_Xanadu)

* Every Xanadu server is uniquely and securely identified. (1)
* Every user is uniquely and securely identified. (3)
* Links are visible and can be followed from all endpoints. (7)
* Every document is uniquely and securely identified. (10)
* Every document can have secure access controls. (11)

The first alpha release will target another 6 of the principles

* Every user can search, retrieve, create and store documents. (4)
* Every document can consist of any number of parts each of which may be of any data type. (5)
* Every document can contain links of any type including virtual copies ("transclusions") to any other document in the system accessible to its owner. (6)
* Permission to link to a document is explicitly granted by the act of publication. (8)
* Every document can be rapidly searched, stored and retrieved without user knowledge of where it is physically stored. (12)
* Every transaction is secure and auditable only by the parties to that transaction. (16)

2019 will focus on federation, micropayments and data redundancy, meeting another 5 of the principles

* Every Xanadu server can be operated independently or in a network. (2)
* Every document can contain a royalty mechanism at any desired degree of granularity to ensure payment on any portion accessed, including virtual copies ("transclusions") of all or part of the document. (9)
* Every document is automatically moved to physical storage appropriate to its frequency of access from any given location. (13)
* Every document is automatically stored redundantly to maintain availability even in case of a disaster. (14)
* Every Xanadu service provider can charge their users at any rate they choose for the storage, retrieval and publishing of documents. (15)

### Release Milestones
* Initial release of features August 2017
* Alpha release (includes browsing and publishing) Early 2018
* Beta release mid 2020 (distributed p2p over Tor, block-chained version)
* Production release early 2021
* Micropayments 2021


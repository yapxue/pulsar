## 05-13
* BrokerStarter(String[] args)
  * parseArgs
  * new WorkerService and WorkerConfig (Optional)
  * new PulsarService (See in other sections)
  * new BookieServer (Optional)
  * call start()
* new PulsarService(brokerConfig, workerConfig, Optional<WorkerService>)
* PulsarService.start()
  * createLocalMetaStore (ZK impl)
  * new CoordinationService
  * create configurationMetadataStore (maybe same with localMetaStore)
  * new PulsarResource(localMetaStore, configurationMetadataStore)
  * new protocolHandlers
  * schemaStorage (BK impl) and schemaRegistryService
  * loadManager
  * defaultOffloader
  * brokerInterceptor
  * start brokerService
    * netty bind
    * this.startInactivityMonitor();
    * this.startMessageExpiryMonitor();
    * this.startCompactionMonitor();
    * this.startConsumedLedgersMonitor();
    * this.startBacklogQuotaChecker();
    * this.updateBrokerPublisherThrottlingMaxRate();
    * this.startCheckReplicationPolicies();
    * this.startDeduplicationSnapshotMonitor();
  * bind handlers to webservice and start it.
  * nsService.init()
  * topicPoliciesService.start()
  * startLeaderElectionService()
  * loadManager.start()
  * start function worker service if necessary
  * 

## pulsar standalone 
* just run command `./pulsar standalone`.

## CompletableFuture.
* For a given CompletableFuture f. f.thenRun(runnable), runnable is executed after f is completed.
* CompletableFuture.run(r1).thenRun(r2). CompletableFuture.run(r1) create a f1 and run r1 then f1.complete().
If you add .thenRun(r2), it create a new CompletableFuture f2. When f1 complete, it run r2 then complete f2.
  

## 05-15
* why there is a DiscoveryService in Pulsar? Both brokerService and discoveryService can handleConnect ? (it is outdated)
* ML has List<LedgerHandle>, MC.readEntries --> ML.readEntries --> find LH and read.

## 05-19
* TopicName in string is tenant/cluster/namespace/domain/localName
* LedgerOffloader.offload(ReadHandle, uuid, Map<>) will offload the ledger,
LedgerOffloader.streamingOffload(ML, uuid, Map<>) is offloading open ledgers.
filesystemOffloader donot support streamingoffload.
uuid is stored in ledger metadata and prevent others.
* LedgerOffloader.readOffloaded() return a bookeeper ReadHandle.
* when openLedger, it will get LedgerInfo and check offloadContext to see whether deleted from bk and offload completed.
If so, it open a LedgerHandle implmented by LedgerOffloader provider.
* when to execute offload? 
  * ML.ledgerClosed(LedgerHandle lh)
  * by api, see PersistentTopics.class
* source code of restapi is in `pulsar.broker.admin.v2`
* 

## 05-20 start broker at localhost
* clusterName is required in broker.conf
* empty metadataStoreUrl will fallback to zookeeperservers, empty configMetadataStoreUrl will fallback to metadatStoreUrl 
* metadtaStore can also be etcd, rocksdb.

## zk data
* get /loadbalance/broker-time-average/192.168.1.7:8080
```
{"shortTermMsgThroughputIn":0.0,"shortTermMsgThroughputOut":0.0,"shortTermMsgRateIn":0.0,"shortTermMsgRateOut":0.0,"longTermMsgThroughputIn":0.0,"longTermMsgThroughputOut":0.0,"longTermMsgRateIn":0.0,"longTermMsgRateOut":0.0}
```
* get get /loadbalance/brokers/192.168.1.7:8080
```
{"webServiceUrl":"http://192.168.1.7:8080","pulsarServiceUrl":"pulsar://192.168.1.7:6650","persistentTopicsEnabled":true,"nonPersistentTopicsEnabled":true,"cpu":{"usage":164.48346692969946,"limit":1600.0},"memory":{"usage":70.26119232177734,"limit":4096.0},"directMemory":{"usage":0.0,"limit":4096.0},"bandwidthIn":{"usage":-1.0,"limit":-1.0},"bandwidthOut":{"usage":-1.0,"limit":-1.0},"msgThroughputIn":0.0,"msgThroughputOut":0.0,"msgRateIn":0.0,"msgRateOut":0.0,"lastUpdate":1653038710563,"lastStats":{},"numTopics":0,"numBundles":0,"numConsumers":0,"numProducers":0,"bundles":[],"lastBundleGains":[],"lastBundleLosses":[],"brokerVersionString":"2.10.1-SNAPSHOT","protocols":{},"advertisedListeners":{},"bundleStats":{},"maxResourceUsage":0.10280216485261917,"loadReportType":"LocalBrokerData"}
```
* ls /namespace/pulsar
```
[192.168.1.7:8080, yapxue1]
```
* get /namespace/pulsar/yapxue1/192.168.1.7:8080/0x00000000_0xffffffff
```
{"nativeUrl":"pulsar://192.168.1.7:6650","httpUrl":"http://192.168.1.7:8080","disabled":false,"advertisedListeners":{}}
```
* get /loadbalance/leader
```
{"serviceUrl":"http://192.168.1.7:8080"}
```


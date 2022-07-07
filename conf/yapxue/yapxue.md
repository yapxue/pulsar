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
* f.join(), it wait result of f.
  

## 05-15
* why there is a DiscoveryService in Pulsar? Both brokerService and discoveryService can handleConnect ? (it is outdated)
* ML has List<LedgerHandle>, MC.readEntries --> ML.readEntries --> find LH and read.

## 05-19
* TopicName in string is domain://tenant/cluster/namespace/localName
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

## 06-21
* pulsar client config
  * enableTransaction
  * memoryLimitBytes: The 64M default can guarantee a high producer throughput.
* pulsar client
  * prodcuerIdGenerator, consumerIdGenerator is AtomicLong.

* pulsar consumr config
  * ackTimeoutTickTime: tick time of detect ack timeout.
  * subscriptionProperties: Properties are immutable, and consumers under the same subscription will fail to create a subscription if they use different properties.
  * isAckReceiptEnabled
  * negativeAckRedeliveryDelay: impled at client side.
  * receiverQueueSize: 
  * acknowledgmentGroupTime
  * replicateSubscriptionState
  * batchReceivePolicy: max records, max bytes, max wait seconds.

* pulsar broker config (ServerConfiguration)
  * managedLedgerUnackedRangesOpenCacheSetEnabled: Use Open Range-Set to cache unacked messages (it is memory efficient but it can take more cpu)

* pulsar webService
  * impl of PulsarWebResource can see path and logic of rest api.
  * PulsarService.addWebServerHandlers
  
* reader is based on non-persitent subscription, so what's the initial position?
  * define startMessageId in clientBuilder.


## 06-22
* pulsar zk /admin/{tenant}/{ns} saved policies of tenent and namespace.
* How to crud tenant/ns? 
>BaseResources -> MetadataCache -> MetadataStore. MetadataStore has impls of etcd,rocksdb,zk.
>> BaseResource VS BaseResources (They are different, one is for admin client request, one is for server storage)
* CoordinationService has lockManagers(map of <class, LockManagerImpl(metastore, class, executor)>)
>lockManager.listLocks(path) -> MetadataCache.getChildren(path) -> MetadataStore.getChildren(path)
* ModularLoadManagerImpl impls getAvailableBrokers, it use coordinatorService.getLockManager -> lockManager.listLocks.
* MetadataStore contains
  * tenant: path is /admin/policies/{tenant}
  * namespace: path is /admin/policies/{tenant}/{ns}
  * brokers: path is /loadbalance/brokers

* where is topic metadata stored?
  * A: SystemTopicBasedTopicPoliciesService
* why two clusters register broker to same path /loadbalance/brokers? 
  * A: They should use different zk.
* consumer has a config ackReceiptEnabled, if it is false sometimes you use ackAsync().whenComplete(), it is not really compeleted 
 because pulsar use ackGroupTracker.

* research mc.individualDeletedMessages, it use BitSet to reduce usage of space. 
    * Can it be persisted? Yes, it is persisted in MetaStore.
    * when open Cursor, method recover() can recover it.
    * example: zk path is `/managed-ledgers/...`, ML name `public/default/persistent/logger`, MC name `test`.

## numsg-pulsar
* subscriptionType default Exlusive? Exlusive cannot loadbalance in numsg server.
* threadsafe layer is useless since pulsar consumer is threadsafe.
* consumerInstance get lock failed ?
* consumer.seek(messageId) can only applied to single partition. If messageId is TopicMessageId, will it be supported?
* producerName is unique ?

## 06-27
* Message has some time property what's their meaning?
  * brokerPublishTime: brokerTs, persisted in BK.
  * eventTime: set by users.
  * publishTime: attached when produce.
* MessageMetadata includes: producerName(from conf or assigned by broker when connectionOpened), sequenceId(AtomicLong of client), 
  eventTime, publishTime (set when published), replicatedFrom, replicatedTo, orderingKey, deliverAtTime(like numsg targetDeliveryTime),
and others.
* broker entry metadata PIP. https://github.com/apache/pulsar/wiki/PIP-70%3A-Introduce-lightweight-broker-entry-metadata
* 

## 07-04
* ConsumerImpl has api hasMessageAvailable, getLastMessageId.
* consumerEventListener listens events of ConsumerImpl, not MultiTopicsConsumerImpl.
* ServerCnx is per-connection. So it is OK to use consumerId as key of consumersMap.
* PersistentTopic.checkGC() ?
  * triggerred when broker has corresponding config.
  * topic level config InactiveTopicPolicies including deleteWhileInactive, maxInactiveDurationSeconds, inactiveTopicDeleteMode(delete_when_no_subscription, delete_when_caughtup)
  * 
* ConcurrentOpenLongPairRangeSet, NavigableMap, BitSet
* PersistentTopic.checkMessageDeduplicationInfo() actually is messageDeduplication.purgeInactiveProducers()
  * messageDeduplication has map of inactiveProducers, remove when connect, put when disconnect.
  * if it has in map for some time, producer will be removed and highestSequencedPushed, highestSequencedPersisted will be removed.
  * highestSequencedPushed is sent to client when producer connect.
  * So if producer doesn't reconnect for some time, then its sequence is not recovered.
  * dedup has a cursor, it persist LAC as markDeletePosition and highestSequencedPersisted as property.
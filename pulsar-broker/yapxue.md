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
* why there is a DiscoveryService in Pulsar? Both brokerService and discoveryService can handleConnect ?
* ML has List<LedgerHandle>, MC.readEntries --> ML.readEntries --> find LH and read.
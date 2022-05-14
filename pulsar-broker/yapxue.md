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
  * 
  
  
  
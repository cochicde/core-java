package eu.arrowhead.core.gateway.model;

import java.util.Date;

import eu.arrowhead.common.database.ArrowheadCloud;
import eu.arrowhead.common.database.ArrowheadService;
import eu.arrowhead.common.database.ArrowheadSystem;

public class ActiveSession {
  private ArrowheadSystem consumer;
  private ArrowheadCloud consumerCloud;
  private ArrowheadSystem provider;
  private ArrowheadCloud providerCloud;
  private ArrowheadService service;
  private String brokerName;
  private int brokerPort;
  private int serverSocketPort;
  private String queueName;
  private String controlQueueName;
  private Boolean isSecure;
  private Boolean useToken;
  private Date startSession;

  public ActiveSession() {
  }

  public ActiveSession(ArrowheadSystem consumer, ArrowheadCloud consumerCloud, ArrowheadSystem provider,
      ArrowheadCloud providerCloud, ArrowheadService service, String brokerName, int brokerPort, int serverSocketPort,
      String queueName, String controlQueueName, Boolean isSecure, Boolean useToken, Date startSession) {
    this.consumer = consumer;
    this.consumerCloud = consumerCloud;
    this.provider = provider;
    this.providerCloud = providerCloud;
    this.service = service;
    this.brokerName = brokerName;
    this.brokerPort = brokerPort;
    this.serverSocketPort = serverSocketPort;
    this.queueName = queueName;
    this.controlQueueName = controlQueueName;
    this.isSecure = isSecure;
    this.useToken = useToken;
    this.startSession = startSession;
  }

  public ArrowheadSystem getConsumer() {
    return consumer;
  }

  public void setConsumer(ArrowheadSystem consumer) {
    this.consumer = consumer;
  }

  public ArrowheadCloud getConsumerCloud() {
    return consumerCloud;
  }

  public void setConsumerCloud(ArrowheadCloud consumerCloud) {
    this.consumerCloud = consumerCloud;
  }

  public ArrowheadSystem getProvider() {
    return provider;
  }

  public void setProvider(ArrowheadSystem provider) {
    this.provider = provider;
  }

  public ArrowheadCloud getProviderCloud() {
    return providerCloud;
  }

  public void setProviderCloud(ArrowheadCloud providerCloud) {
    this.providerCloud = providerCloud;
  }

  public ArrowheadService getService() {
    return service;
  }

  public void setService(ArrowheadService service) {
    this.service = service;
  }

  public String getBrokerName() {
    return brokerName;
  }

  public void setBrokerName(String brokerName) {
    this.brokerName = brokerName;
  }

  public int getBrokerPort() {
    return brokerPort;
  }

  public void setBrokerPort(int brokerPort) {
    this.brokerPort = brokerPort;
  }

  public int getServerSocketPort() {
    return serverSocketPort;
  }

  public void setServerSocketPort(int serverSocketPort) {
    this.serverSocketPort = serverSocketPort;
  }

  public String getQueueName() {
    return queueName;
  }

  public void setQueueName(String queueName) {
    this.queueName = queueName;
  }

  public String getControlQueueName() {
    return controlQueueName;
  }

  public void setControlQueueName(String controlQueueName) {
    this.controlQueueName = controlQueueName;
  }

  public Boolean getIsSecure() {
    return isSecure;
  }

  public void setIsSecure(Boolean isSecure) {
    this.isSecure = isSecure;
  }

  public Boolean getUseToken() {
    return useToken;
  }

  public void setUseToken(Boolean useToken) {
    this.useToken = useToken;
  }

  public Date getStartSession() {
    return startSession;
  }

  public void setStartSession(Date startSession) {
    this.startSession = startSession;
  }

}
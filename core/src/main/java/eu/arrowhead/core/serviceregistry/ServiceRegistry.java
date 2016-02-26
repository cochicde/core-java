package eu.arrowhead.core.serviceregistry;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.github.danieln.dnssdjava.DnsSDBrowser;
import com.github.danieln.dnssdjava.DnsSDDomainEnumerator;
import com.github.danieln.dnssdjava.DnsSDException;
import com.github.danieln.dnssdjava.DnsSDFactory;
import com.github.danieln.dnssdjava.DnsSDRegistrator;
import com.github.danieln.dnssdjava.ServiceData;
import com.github.danieln.dnssdjava.ServiceName;
import com.github.danieln.dnssdjava.ServiceType;

import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.model.ArrowheadSystem;
import eu.arrowhead.common.model.messages.ProvidedService;
import eu.arrowhead.common.model.messages.ServiceQueryForm;
import eu.arrowhead.common.model.messages.ServiceQueryResult;
import eu.arrowhead.common.model.messages.ServiceRegistryEntry;

public class ServiceRegistry {

	private static Logger log = Logger.getLogger(ServiceRegistry.class.getName());

	private static ServiceRegistry instance;
	private static Properties prop;

	public static synchronized ServiceRegistry getInstance() {
		try {
			if (instance == null) {
				instance = new ServiceRegistry();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return instance;
	}

	public synchronized Properties getProp() {
		try {
			if (prop == null) {
				prop = new Properties();
				InputStream inputStream = getClass().getClassLoader().getResourceAsStream("dns.properties");
				if (inputStream != null) {
					prop.load(inputStream);
					initSystemProperties();
				} else {
					throw new FileNotFoundException("property file 'dns.properties' not found in the classpath");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return prop;
	}

	// This is require for dnssdjava,dnsjava
	private void initSystemProperties() {
		System.setProperty("dns.server", getProp().getProperty("dns.ip"));
		System.setProperty("dnssd.domain", getProp().getProperty("dns.domain"));
		System.setProperty("dnssd.hostname", getProp().getProperty("dns.host"));
	}

	public void register(String serviceGroup, String serviceName, String interf, ServiceRegistryEntry entry) {
		if (!parametersIsValid(serviceGroup, serviceName, interf)) {
			throw new InvalidParameterException("Invalid parameters in URL!");
		}

		try {
			if (entry != null && entry.getProvider() != null) {

				DnsSDRegistrator reg = createRegistrator();

				String serviceType = "_" + serviceGroup + "_" + serviceName + "_" + interf + "._tcp";
				// Unique service name
				String uniqueServiceName = entry.getProvider().getSystemName();
				String localName = entry.getProvider().getIPAddress() + ".";
				int port = new Integer(entry.getProvider().getPort());

				ServiceName name = reg.makeServiceName(uniqueServiceName, ServiceType.valueOf(serviceType));
				ServiceData data = new ServiceData(name, localName, port);

				// set TSIG from settings
				setTSIGKey(reg, entry.gettSIG_key());

				setServiceDataProperties(entry, data);

				if (reg.registerService(data)) {
					log.info("Service registered: " + name);
					System.out.println("Service registered: " + name);
				} else {
					log.info("Service already exists: " + name);
					System.out.println("Service already exists: " + name);
				}

			}
		} catch (DnsSDException ex) {
			ex.printStackTrace();
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}

	}

	public void unRegister(String serviceGroup, String serviceName, String interf, ServiceRegistryEntry entry) {
		if (!parametersIsValid(serviceGroup, serviceName, interf)) {
			throw new InvalidParameterException("Invalid parameters in URL!");
		}

		try {
			DnsSDRegistrator reg = createRegistrator();
			String serviceType = "_" + serviceGroup + "_" + serviceName + "_" + interf + "._tcp";
			String uniqueServiceName = entry.getProvider().getSystemName();
			ServiceName name = reg.makeServiceName(uniqueServiceName, ServiceType.valueOf(serviceType));

			setTSIGKey(reg, entry.gettSIG_key());

			if (reg.unregisterService(name)) {
				log.info("Service unregistered: " + name);
				System.out.println("Service unregistered: " + name);
			} else {
				log.info("No service to remove: " + name);
				System.out.println("No service to remove: " + name);
			}
		} catch (DnsSDException ex) {
			ex.printStackTrace();
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}

	}

	public ServiceQueryResult provideServices(String serviceGroup, String serviceName, ServiceQueryForm queryForm) {

		if (queryForm.getServiceInterfaces() != null && !queryForm.getServiceInterfaces().isEmpty()) {
			try {

				String computerDomain = getProp().getProperty("dns.domain", "evoin.arrowhead.eu");

				DnsSDDomainEnumerator de = DnsSDFactory.getInstance().createDomainEnumerator();

				if (computerDomain != null) {
					System.out.println("DNS-SD overriding computer domain: " + computerDomain);
					de = DnsSDFactory.getInstance().createDomainEnumerator(computerDomain);
				} else {
					de = DnsSDFactory.getInstance().createDomainEnumerator();
				}

				DnsSDBrowser browser = DnsSDFactory.getInstance().createBrowser(de.getBrowsingDomains());

				Collection<ServiceType> types = browser.getServiceTypes();
				List<ProvidedService> list = new ArrayList<ProvidedService>();
				for (ServiceType type : types) {
					Collection<ServiceName> instances = browser.getServiceInstances(type);
					System.out.println(instances);
					for (ServiceName instance : instances) {
						ServiceData service = browser.getServiceData(instance);
						if (service != null) {							
							for (String serviceInterface : queryForm.getServiceInterfaces()) {
								ProvidedService providerService = buildProviderService(service, serviceInterface);
								if (providerService != null) {
									list.add(providerService);
								}
							}

						}
						System.out.println(service);
					}
				}

				ServiceQueryResult result = new ServiceQueryResult();
				result.setServiceQueryData(list);
				return result;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	private ProvidedService buildProviderService(ServiceData service, String serviceInterface) {
		ProvidedService providerService = null;

		if (serviceInterface != null && !serviceInterface.isEmpty()) {
			String interfaceType = null;

			String serviceType = service.getName().getType().toString();
			int dotIndex = serviceType.indexOf(".");
			if (dotIndex != -1) {
				serviceType = serviceType.substring(0, dotIndex);
				String[] array = serviceType.split("_");
				if (array.length == 4) {
					interfaceType = array[3];
				}
			}

			if (interfaceType != null && interfaceType.equals(serviceInterface)) {
				providerService = new ProvidedService();
				ArrowheadSystem arrowheadSystem = new ArrowheadSystem();

				Map<String, String> properties = service.getProperties();
				String systemGroup = properties.get("ahsysgrp");
				String systemName = properties.get("ahsysname");
				String authInfo = properties.get("ahsysauthinfo");
				String serviceURI = properties.get("path");

				String ipAddress = service.getHost();
				if (ipAddress != null && ipAddress.length() > 0 && ipAddress.charAt(ipAddress.length() - 1) == '.') {
					ipAddress = ipAddress.substring(0, ipAddress.length() - 1);
				}

				String port = new Integer(service.getPort()).toString();

				arrowheadSystem.setAuthenticationInfo(authInfo);
				arrowheadSystem.setIPAddress(ipAddress);
				arrowheadSystem.setPort(port);
				arrowheadSystem.setSystemGroup(systemGroup);
				arrowheadSystem.setSystemName(systemName);

				providerService.setProvider(arrowheadSystem);
				providerService.setServiceURI(serviceURI);
				providerService.setServiceInterface(interfaceType);
			}
		}
		return providerService;
	}

	private DnsSDRegistrator createRegistrator() throws DnsSDException {
		// Get the DNS specific settings
		String dnsIpAddress = getProp().getProperty("dns.ip", "192.168.184.128");
		String dnsDomain = getProp().getProperty("dns.registerDomain", "srv.evoin.arrowhead.eu") + ".";
		int dnsPort = new Integer(getProp().getProperty("dns.port", "53"));

		InetSocketAddress dnsserverAddress = new InetSocketAddress(dnsIpAddress, dnsPort);
		DnsSDRegistrator reg = DnsSDFactory.getInstance().createRegistrator(dnsDomain, dnsserverAddress);
		return reg;
	}

	private void setTSIGKey(DnsSDRegistrator reg, String tsigKey) {
		System.out.println("TSIG Key: " + tsigKey);
		String tsigKeyName = getProp().getProperty("tsig.name", "key.evoin.arrowhead.eu.");
		String tsigAlgorithm = getProp().getProperty("tsig.algorithm", DnsSDRegistrator.TSIG_ALGORITHM_HMAC_MD5);
		reg.setTSIGKey(tsigKeyName, tsigAlgorithm, tsigKey);
	}

	private void setServiceDataProperties(ServiceRegistryEntry entry, ServiceData data) {
		Map<String, String> properties = data.getProperties();
		properties.put("ahsysgrp", entry.getProvider().getSystemGroup());
		properties.put("ahsysname", entry.getProvider().getSystemName());
		properties.put("ahsysauthinfo", entry.getProvider().getAuthenticationInfo());
		properties.put("path", entry.getServiceURI());
		properties.put("ahsrvmetad", entry.getServiceMetadata());
		properties.put("txtvers", entry.getVersion());
	}

	private boolean parametersIsValid(String serviceGroup, String serviceName, String interf) {
		boolean result = true;
		if (serviceGroup == null || serviceName == null || interf == null || serviceGroup.contains("_")
				|| serviceName.contains("_") || interf.contains("_")) {
			result = false;
		}
		return result;
	}

}

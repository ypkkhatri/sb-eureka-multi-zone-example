package com.eureka.client;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.CloudEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableDiscoveryClient
@SpringBootApplication
public class EurekaClientExampleApplication implements ApplicationContextAware {

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientExampleApplication.class, args);
    }

    @Value("${eureka.client.serviceUrl.additionalZones}")
    private String additionalZones;

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @PreDestroy
    public void unRegisterInAllConfiguredDiscovery() {
        Map<String, EurekaClient> additionalEurekaClients = this.applicationContext.getBean("registerToAdditionalEurekaServers", Map.class);
        System.out.println("PreDestroy: " + additionalEurekaClients.size());
        additionalEurekaClients.forEach((k, v) -> {
            v.shutdown();
        });
    }

    @Bean
    public Map<String, EurekaClient> registerToAdditionalEurekaServers(ApplicationInfoManager manager,
                                                                       @Autowired(required = false) HealthCheckHandler healthCheckHandler) {
        Map<String, EurekaClient> clients = new HashMap<>();

        if (additionalZones == null || additionalZones.isEmpty()) {
            return clients;
        }

        String[] hosts = additionalZones.split(",");
        for (int i = 0; i < hosts.length; i++) {
            EurekaClient client = new CloudEurekaClient(
                    manager,
                    new SimpleEurekaClientConfig(hosts[i].trim(), "defaultZone"),
                    null,
                    this.applicationContext);
            client.registerHealthCheck(healthCheckHandler);
            String clientName = "client_" + (i + 1);
            clients.put(clientName, client);
        }

        return clients;
    }
}

@RestController
class ServiceInstanceRestController {
    private DiscoveryClient discoveryClient;

    @Autowired
    public ServiceInstanceRestController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @RequestMapping("/instances")
    public List<ServiceInstance> serviceInstancesByApplicationName(
            @PathVariable String applicationName) {
        return this.discoveryClient.getInstances(applicationName);
    }

    @RequestMapping("/hello")
    public String hello() {
        return "Hello Eureka";
    }
}
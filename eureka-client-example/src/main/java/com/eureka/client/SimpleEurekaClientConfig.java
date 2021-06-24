package com.eureka.client;

import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;

import java.util.Arrays;
import java.util.List;

public class SimpleEurekaClientConfig extends EurekaClientConfigBean {

    private String eurekaUrl;
    private String zone;
    private String region = "us-east-1";

    public SimpleEurekaClientConfig(String eurekaUrl, String zone, String region) {
        this.eurekaUrl = eurekaUrl;
        this.zone = zone;
        this.region = region;
    }

    public SimpleEurekaClientConfig(String eurekaUrl, String zone) {
        this.eurekaUrl = eurekaUrl;
        this.zone = zone;
    }


    @Override
    public String getRegion() {
        return region;
    }

    @Override
    public String[] getAvailabilityZones(String s) {
        return new String[]{zone};
    }

    @Override
    public List<String> getEurekaServerServiceUrls(String s) {
        return Arrays.asList(eurekaUrl);
    }

    @Override
    public boolean shouldEnforceRegistrationAtInit() {
        return true;
    }

    @Override
    public boolean shouldRegisterWithEureka() {
        return true;
    }
}
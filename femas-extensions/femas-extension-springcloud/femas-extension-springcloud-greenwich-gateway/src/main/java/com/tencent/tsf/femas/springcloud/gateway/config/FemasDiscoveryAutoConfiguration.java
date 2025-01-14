/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tencent.tsf.femas.springcloud.gateway.config;


import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.tencent.tsf.femas.common.serviceregistry.AbstractServiceRegistryMetadata;
import com.tencent.tsf.femas.common.serviceregistry.AbstractServiceRegistryMetadataFactory;
import com.tencent.tsf.femas.springcloud.gateway.discovery.DiscoveryServerConverter;
import com.tencent.tsf.femas.springcloud.gateway.discovery.nacos.NacosServerConverter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration;


@Configuration
@AutoConfigureBefore(NacosDiscoveryAutoConfiguration.class)
public class FemasDiscoveryAutoConfiguration {

    @Configuration
    @ConditionalOnDiscoveryEnabled
    @ConditionalOnNacosDiscoveryEnabled
    static class FemasNacosConfiguration {

        private volatile AbstractServiceRegistryMetadata serviceRegistryMetadata = AbstractServiceRegistryMetadataFactory
                .getServiceRegistryMetadata();

        @Bean
        @Primary
        public NacosDiscoveryProperties nacosProperties() {
            NacosDiscoveryProperties nacosDiscoveryProperties = new NacosDiscoveryProperties();
            nacosDiscoveryProperties.getMetadata().putAll(serviceRegistryMetadata.getRegisterMetadataMap());
            return nacosDiscoveryProperties;
        }

        @Bean
        @Primary
        public NacosServiceDiscovery nacosServiceDiscovery(NacosDiscoveryProperties nacosDiscoveryProperties) {
            nacosDiscoveryProperties.getMetadata().putAll(serviceRegistryMetadata.getRegisterMetadataMap());
            return new NacosServiceDiscovery(nacosDiscoveryProperties);
        }

        @Bean("converterAdapter")
        public DiscoveryServerConverter nacosConverterAdapter() {
            return new NacosServerConverter();
        }
    }

    @Bean("registryUrl")
    public String registryUrl(NacosDiscoveryProperties nacosDiscoveryProperties) {
        return nacosDiscoveryProperties.getServerAddr();
    }

}

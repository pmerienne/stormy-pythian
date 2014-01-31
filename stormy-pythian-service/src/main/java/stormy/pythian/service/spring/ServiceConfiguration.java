/**
 * Copyright 2013-2015 Pierre Merienne
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stormy.pythian.service.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import stormy.pythian.core.configuration.PythianToplogyConfiguration;
import stormy.pythian.core.ioc.CoreConfiguration;

@Configuration
@ComponentScan({ "stormy.pythian.service" })
@Import({ CoreConfiguration.class, PyhtianProperties.class })
public class ServiceConfiguration {

    @Value("${redis.hostname}")
    private String redisHostName;

    @Value("${redis.password}")
    private String redisPassword;

    @Value("${redis.port}")
    private Integer redisPort;

    @Bean
    public RedisTemplate<String, PythianToplogyConfiguration> redisTopologyTemplate() {
        RedisTemplate<String, PythianToplogyConfiguration> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new JacksonJsonRedisSerializer<>(PythianToplogyConfiguration.class));

        return template;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
        connectionFactory.setHostName(redisHostName);
        connectionFactory.setPort(redisPort);
        connectionFactory.setPassword(redisPassword);
        return connectionFactory;
    }

}

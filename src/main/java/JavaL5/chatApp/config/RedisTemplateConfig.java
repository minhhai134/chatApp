package JavaL5.chatApp.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisTemplateConfig
{
//
//    @Bean
//    JedisConnectionFactory jedisConnectionFactory() {
////        JedisConnectionFactory jedisConFactory = new JedisConnectionFactory();
////        jedisConFactory.setHostName("127.0.0.1");
////        jedisConFactory.setPort(6379);
////        return jedisConFactory;
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
//        redisStandaloneConfiguration.setHostName("127.0.0.1");
//        redisStandaloneConfiguration.setPort(6379);
//
//        return new JedisConnectionFactory(redisStandaloneConfiguration);
//    }
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate() {
//        final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
//        template.setConnectionFactory(jedisConnectionFactory());
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new StringRedisSerializer());
//
//        // the following is not required
//        template.setHashValueSerializer(new StringRedisSerializer());
//        template.setHashKeySerializer(new StringRedisSerializer());
//
//        return template;
//    }

}

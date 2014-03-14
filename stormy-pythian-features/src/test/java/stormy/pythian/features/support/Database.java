package stormy.pythian.features.support;

import static stormy.pythian.features.support.Environment.REDIS_HOST;
import static stormy.pythian.features.support.Environment.REDIS_PORT;
import redis.clients.jedis.Jedis;
import cucumber.api.java.After;
import cucumber.api.java.Before;

public class Database {

    @Before
    public void init() {
        Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT);
        jedis.flushAll();
    }

    @After
    public void cleanup() {
        Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT);
        jedis.flushAll();
    }
}

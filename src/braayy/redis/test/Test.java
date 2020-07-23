package braayy.redis.test;

import braayy.redis.RedisClient;

public class Test {

    public static void main(String[] args) throws Exception {
        RedisClient redis = new RedisClient("localhost", 6379);
        redis.connect();

        System.out.println(redis.lpop("testando"));
        System.out.println(redis.llen("testando"));
    }

}
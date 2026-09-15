package com.golajugaenyang.order.adapter.out.persistence.cart;


import com.golajugaenyang.order.application.cart.port.out.CartRepository;
import com.golajugaenyang.order.domain.cart.CartItemKey;
import com.golajugaenyang.order.domain.cart.CartItemQuantity;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;


@Repository
public class RedisCartRepository implements CartRepository {

    private final StringRedisTemplate redisTemplate;
    private final CartRedisKeyGenerator keyGenerator;
    private final RedisScript<Long> increaseScript;
    private final RedisScript<Long> changeScript;

    public RedisCartRepository(
        StringRedisTemplate redisTemplate, CartRedisKeyGenerator keyGenerator) {
        this.redisTemplate = redisTemplate;
        this.keyGenerator = keyGenerator;
        this.increaseScript = loadScript("script/increase-quantity.lua");
        this.changeScript = loadScript("script/change-quantity.lua");
    }

    private RedisScript<Long> loadScript(String path) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource(path));
        script.setResultType(Long.class);
        return script;
    }

    @Override
    public Map<CartItemKey, Integer> findAll(Long memberId) {
        Map<Object, Object> entries = redisTemplate.opsForHash()
            .entries(keyGenerator.generate(memberId));
        return entries.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> CartItemKey.fromRedisField((String) entry.getKey()),
                entry -> Integer.valueOf((String) entry.getValue())
            ));
    }

    @Override
    public int addOrIncrease(Long memberId, CartItemKey key, int quantity, Duration ttl) {
        Long result = redisTemplate.execute(
            increaseScript,
            List.of(keyGenerator.generate(memberId)),
            key.toRedisField(),
            String.valueOf(quantity),
            String.valueOf(CartItemQuantity.MAX_QUANTITY),
            String.valueOf(ttl.toSeconds())
        );
        return result.intValue();
    }

    @Override
    public int changeQuantity(Long memberId, CartItemKey key, int delta, Duration ttl) {
        Long result = redisTemplate.execute(
            changeScript,
            List.of(keyGenerator.generate(memberId)),
            key.toRedisField(),
            String.valueOf(delta),
            String.valueOf(CartItemQuantity.MIN_QUANTITY),
            String.valueOf(CartItemQuantity.MAX_QUANTITY),
            String.valueOf(ttl.toSeconds())
        );
        return result.intValue();
    }

    @Override
    public void remove(Long memberId, CartItemKey key) {
        redisTemplate.opsForHash().delete(
            keyGenerator.generate(memberId), key.toRedisField());
    }
}

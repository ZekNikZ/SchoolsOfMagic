package dev.mattrm.schoolsofmagic.common.util;

import com.mojang.datafixers.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DoubleKeyMap<K1, K2, V> {
    private final Map<KeyPair<K1, K2>, V> delegate;

    public DoubleKeyMap() {
        this.delegate = new HashMap<>();
    }

    public Map<KeyPair<K1, K2>, V> getDelegate() {
        return delegate;
    }

    public V get(K1 key1, K2 key2) {
        return this.get(new KeyPair<>(key1, key2));
    }

    public V get(KeyPair<K1, K2> key) {
        return this.delegate.get(key);
    }

    public Map<K2, V> getAllByKey1(K1 key1) {
        Map<K2, V> result = new HashMap<>();

        this.delegate.keySet().stream()
                .filter(kp -> kp.key1.equals(key1))
                .map(kp -> Pair.of(kp.key2, this.delegate.get(kp)))
                .forEach(p -> result.put(p.getFirst(), p.getSecond()));

        return result;
    }

    public Map<K1, V> getAllByKey2(K2 key2) {
        Map<K1, V> result = new HashMap<>();

        this.delegate.keySet().stream()
                .filter(kp -> kp.key2.equals(key2))
                .map(kp -> Pair.of(kp.key1, this.delegate.get(kp)))
                .forEach(p -> result.put(p.getFirst(), p.getSecond()));

        return result;
    }

    public void put(K1 key1, K2 key2, V val) {
        this.put(new KeyPair<>(key1, key2), val);
    }

    public void put(KeyPair<K1, K2> key, V val) {
        this.delegate.put(key, val);
    }

    public boolean containsKeyPair(K1 key1, K2 key2) {
        return containsKeyPair(new KeyPair<>(key1, key2));
    }

    public boolean containsKeyPair(KeyPair<K1, K2> key) {
        return this.delegate.containsKey(key);
    }

    public V removeIfPresent(K1 key1, K2 key2) {
        return this.removeIfPresent(new KeyPair<>(key1, key2));
    }

    public V removeIfPresent(KeyPair<K1, K2> key) {
        return this.delegate.remove(key);
    }

    public int size() {
        return this.delegate.size();
    }

    public static class KeyPair<S, T> {
        private S key1;
        private T key2;

        public KeyPair(S key1, T key2) {
            this.key1 = key1;
            this.key2 = key2;
        }

        public S key1() {
            return key1;
        }

        public T key2() {
            return key2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KeyPair<?, ?> keyPair = (KeyPair<?, ?>) o;
            return Objects.equals(key1, keyPair.key1) &&
                    Objects.equals(key2, keyPair.key2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key1, key2);
        }
    }
}

package dev.mattrm.schoolsofmagic.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DoubleKeyMap<K1, K2, V> {
    private Map<KeyPair<K1, K2>, V> delegate;

    public DoubleKeyMap() {
        this.delegate = new HashMap<>();
    }

    public V get(K1 key1, K2 key2) {
        return this.get(new KeyPair<>(key1, key2));
    }

    public V get(KeyPair<K1, K2> key) {
        return delegate.get(key);
    }

    public void put(K1 key1, K2 key2, V val) {
        this.put(new KeyPair<>(key1, key2), val);
    }

    public void put(KeyPair<K1, K2> key, V val) {
        delegate.put(key, val);
    }

    public boolean containsKeyPair(K1 key1, K2 key2) {
        return containsKeyPair(new KeyPair<>(key1, key2));
    }

    public boolean containsKeyPair(KeyPair<K1, K2> key) {
        return delegate.containsKey(key);
    }

    public V removeIfPresent(K1 key1, K2 key2) {
        return this.removeIfPresent(new KeyPair<>(key1, key2));
    }

    public V removeIfPresent(KeyPair<K1, K2> key) {
        return delegate.remove(key);
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

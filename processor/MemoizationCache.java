package philadelphia_info_calculator.processor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedHashMap;

public class MemoizationCache<K, V> {
    private Map<K, V> cache;

    public MemoizationCache(int maxEntries) {
        if (maxEntries > 0) {
            this.cache = new LinkedHashMap<K, V>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                    return size() > maxEntries;
                }
            };
        } else {
            this.cache = new LinkedHashMap<>();
        }
    }

    public V get(K key) {
        return cache.get(key);
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    public void clear() {
        cache.clear();
    }
}

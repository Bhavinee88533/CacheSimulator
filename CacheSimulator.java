import java.util.*;

// ---------------- Cache Interface ----------------
interface Cache<K, V> {
    V get(K key);
    void put(K key, V value);
    void displayCache();
}

// ---------------- LRU Cache ----------------
class LRUCache<K, V> implements Cache<K, V> {
    private final int capacity;
    private final Map<K, Node> map;
    private final DoublyLinkedList dll;

    private class Node {
        K key;
        V value;
        Node prev, next;
        Node(K k, V v) { key = k; value = v; }
    }

    private class DoublyLinkedList {
        Node head, tail;
        DoublyLinkedList() {
            head = new Node(null, null);
            tail = new Node(null, null);
            head.next = tail;
            tail.prev = head;
        }
        void addFront(Node node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
        }
        void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        void moveToFront(Node node) {
            remove(node);
            addFront(node);
        }
        Node removeLast() {
            if (tail.prev == head) return null;
            Node node = tail.prev;
            remove(node);
            return node;
        }
    }

    public LRUCache(int cap) {
        capacity = cap;
        map = new HashMap<>();
        dll = new DoublyLinkedList();
    }

    public V get(K key) {
        if (!map.containsKey(key)) {
            System.out.println("Cache Miss for key: " + key);
            return null;
        }
        Node node = map.get(key);
        dll.moveToFront(node);
        System.out.println("Cache Hit: " + key + " -> " + node.value);
        return node.value;
    }

    public void put(K key, V value) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.value = value;
            dll.moveToFront(node);
        } else {
            if (map.size() == capacity) {
                Node last = dll.removeLast();
                if (last != null) map.remove(last.key);
            }
            Node node = new Node(key, value);
            dll.addFront(node);
            map.put(key, node);
        }
        System.out.println("Inserted/Updated: " + key + " -> " + value);
    }

    public void displayCache() {
        Node curr = dll.head.next;
        System.out.print("Cache State: ");
        while (curr != dll.tail) {
            System.out.print(curr.key + ":" + curr.value + " ");
            curr = curr.next;
        }
        System.out.println();
    }
}

// ---------------- MRU Cache ----------------
class MRUCache<K, V> implements Cache<K, V> {
    private final int capacity;
    private final Map<K, V> map;
    private final Deque<K> stack;

    public MRUCache(int cap) {
        capacity = cap;
        map = new HashMap<>();
        stack = new ArrayDeque<>();
    }

    public V get(K key) {
        if (!map.containsKey(key)) {
            System.out.println("Cache Miss for key: " + key);
            return null;
        }
        System.out.println("Cache Hit: " + key + " -> " + map.get(key));
        return map.get(key);
    }

    public void put(K key, V value) {
        if (map.size() == capacity && !map.containsKey(key)) {
            K mostRecent = stack.pop();
            map.remove(mostRecent);
        }
        map.put(key, value);
        stack.push(key);
        System.out.println("Inserted/Updated: " + key + " -> " + value);
    }

    public void displayCache() {
        System.out.print("Cache State: ");
        for (K key : stack) {
            System.out.print(key + ":" + map.get(key) + " ");
        }
        System.out.println();
    }
}

// ---------------- LFU Cache ----------------
class LFUCache<K, V> implements Cache<K, V> {
    private final int capacity;
    private final Map<K, V> values;
    private final Map<K, Integer> freq;

    public LFUCache(int cap) {
        capacity = cap;
        values = new HashMap<>();
        freq = new HashMap<>();
    }

    public V get(K key) {
        if (!values.containsKey(key)) {
            System.out.println("Cache Miss for key: " + key);
            return null;
        }
        freq.put(key, freq.get(key) + 1);
        System.out.println("Cache Hit: " + key + " -> " + values.get(key));
        return values.get(key);
    }

    public void put(K key, V value) {
        if (capacity == 0) return;

        if (values.containsKey(key)) {
            values.put(key, value);
            freq.put(key, freq.get(key) + 1);
        } else {
            if (values.size() == capacity) {
                K leastFreqKey = Collections.min(freq.entrySet(), Map.Entry.comparingByValue()).getKey();
                values.remove(leastFreqKey);
                freq.remove(leastFreqKey);
            }
            values.put(key, value);
            freq.put(key, 1);
        }
        System.out.println("Inserted/Updated: " + key + " -> " + value);
    }

    public void displayCache() {
        System.out.print("Cache State: ");
        for (K key : values.keySet()) {
            System.out.print(key + ":" + values.get(key) + "(f=" + freq.get(key) + ") ");
        }
        System.out.println();
    }
}

// ---------------- Main Simulator ----------------
public class CacheSimulator {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Choose Cache Policy:");
        System.out.println("1. LRU (Least Recently Used)");
        System.out.println("2. MRU (Most Recently Used)");
        System.out.println("3. LFU (Least Frequently Used)");
        System.out.print("Enter choice: ");
        int policy = sc.nextInt();

        System.out.print("Enter cache capacity: ");
        int capacity = sc.nextInt();

        Cache<Integer, String> cache = null;
        switch (policy) {
            case 1: cache = new LRUCache<>(capacity); break;
            case 2: cache = new MRUCache<>(capacity); break;
            case 3: cache = new LFUCache<>(capacity); break;
            default: System.out.println("Invalid choice!"); System.exit(0);
        }

        while (true) {
            System.out.println("\n1. Get from Cache");
            System.out.println("2. Put into Cache");
            System.out.println("3. Display Cache");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");

            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    System.out.print("Enter key: ");
                    int keyGet = sc.nextInt();
                    cache.get(keyGet);
                    break;
                case 2:
                    System.out.print("Enter key: ");
                    int keyPut = sc.nextInt();
                    System.out.print("Enter value: ");
                    String val = sc.next();
                    cache.put(keyPut, val);
                    break;
                case 3:
                    cache.displayCache();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}

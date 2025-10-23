package com.careerit.saiopenai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatClientServiceImpl {

    public final ChatClient chatClient;

    String systemPrompt = """
           You are java content creator, help in creating java related content.
           """;

    String message = """
            Sure — here’s a compact, practical guide to the Java Collections Framework: what it is, main interfaces and implementations, behavior/complexities, useful utilities, common pitfalls, and short code examples.
            
            What is the Collections Framework?
            - A set of interfaces, implementations, and utility classes for storing and manipulating groups of objects (collections).
            - Provided in java.util. It standardizes data structures (lists, sets, maps, queues) and algorithms (sorting, searching, shuffling).
            
            Core interfaces (hierarchy highlights)
            - Collection<E> — root for most collection types (List, Set, Queue). Note: Map is separate (not a Collection).
            - List<E> — ordered, allows duplicates. (ArrayList, LinkedList, CopyOnWriteArrayList)
            - Set<E> — no duplicates. (HashSet, LinkedHashSet, TreeSet)
            - SortedSet / NavigableSet — sorted variants (TreeSet).
            - Queue<E> — FIFO or priority ordering. (LinkedList, ArrayDeque, PriorityQueue)
            - Deque<E> — double-ended queue (ArrayDeque, LinkedList).
            - Map<K,V> — key-value pairs, no duplicate keys. (HashMap, LinkedHashMap, TreeMap, ConcurrentHashMap)
            - NavigableMap / SortedMap — sorted maps (TreeMap).
            
            Common implementations and when to use them
            - ArrayList<E>: random access O(1), good general-purpose list.
            - LinkedList<E>: cheap insert/remove at ends or when you need deque features, but slower random access O(n).
            - HashSet<E>: unique elements, O(1) add/contains/remove average.
            - LinkedHashSet<E>: preserves insertion order (or access order), slightly more overhead than HashSet.
            - TreeSet<E>: sorted set, O(log n) operations.
            - ArrayDeque<E>: efficient stack/queue operations, better than LinkedList for queues/deques.
            - PriorityQueue<E>: priority-based queue (min-heap semantics).
            - HashMap<K,V>: fast key-based lookup O(1) average.
            - LinkedHashMap<K,V>: predictable iteration order (insertion or access order).
            - TreeMap<K,V>: sorted keys, O(log n).
            - ConcurrentHashMap<K,V>: thread-safe high-concurrency map.
            
            Time complexity (typical)
            - ArrayList get(i): O(1), add at end amortized O(1), add/remove at arbitrary index O(n)
            - LinkedList get(i): O(n), add/remove at ends O(1)
            - HashSet/HashMap operations: O(1) average, O(n) worst-case (rare if good hash)
            - TreeSet/TreeMap operations: O(log n)
            - ArrayDeque add/remove at ends: O(1)
            
            Ordering, duplicates, and nulls
            - List: ordered, allows duplicates, usually allows nulls (except some concurrent implementations).
            - Set: no duplicates. HashSet allows one null, TreeSet permits null only if comparator supports it (TreeSet of natural ordering will throw NPE on null).
            - Map: unique keys, values may be duplicates, some Map impls allow null keys/values (HashMap allows one null key, ConcurrentHashMap does not allow null keys/values).
            
            Collections vs Collections (class difference)
            - java.util.Collection (interface) — parent interface for collections.
            - java.util.Collections (class) — utility class with static methods (sort, shuffle, synchronized wrappers, unmodifiable wrappers, etc.).
            
            Important utility methods (Collections and List)
            - Collections.sort(list) or list.sort(comparator)
            - Collections.binarySearch(list, key)
            - Collections.shuffle(list)
            - Collections.unmodifiableList/set/map(...)
            - Collections.synchronizedList/set/map(...)
            - Arrays.asList(...) — fixed-size List backed by array
            - List.copyOf, Set.copyOf, Map.copyOf (Java 10+) — immutable copies
            - Map.of / List.of / Set.of (Java 9+) — small immutable collections
            
            Iteration & modification
            - Use Iterator to traverse collections. To remove elements while iterating, use Iterator.remove().
            - ListIterator allows bidirectional traversal and modifications at current position.
            - For-each loop is syntactic sugar that uses Iterator.
            - Spliterator supports efficient parallel operations (used by streams).
            
            Fail-fast vs fail-safe
            - Most standard collections’ iterators are fail-fast: they detect concurrent modification and throw ConcurrentModificationException (best-effort; not guaranteed).
            - Concurrent collections (e.g., CopyOnWriteArrayList, ConcurrentHashMap) provide fail-safe behavior: iterators reflect snapshot or weakly consistent view and don’t throw CME.
            
            Thread safety
            - Collections.synchronizedList(...) etc. add basic synchronization (single lock).
            - java.util.concurrent package offers higher-perf concurrent implementations (ConcurrentHashMap, CopyOnWriteArrayList, ConcurrentLinkedQueue).
            - Prefer concurrent collections or external synchronization depending on needs.
            
            Common operations and modern idioms (Java 8+)
            - forEach(consumer) on collections and maps.
            - Streams: collection.stream() → map/filter/reduce, parallelStream() for parallel processing.
            - Map.computeIfAbsent(key, k -> new Value()) for lazy value creation.
            - Map.merge(...) for combining values.
            
            Short code examples
            - List:
              List<String> list = new ArrayList<>();
              list.add("a");
              list.add("b");
              list.sort(String::compareTo);
              list.forEach(System.out::println);
            
            - Set:
              Set<String> set = new HashSet<>();
              set.add("x");
              set.add("x"); // duplicate ignored
            
            - Map:
              Map<String,Integer> map = new HashMap<>();
              map.put("apple", 2);
              map.computeIfAbsent("banana", k -> 0);
              map.forEach((k,v) -> System.out.println(k + "=" + v));
            
            - Remove while iterating:
              Iterator<String> it = list.iterator();
              while (it.hasNext()) {
                if (shouldRemove(it.next())) it.remove();
              }
            
            Best practices / tips
            - Choose implementation by required properties: random access (ArrayList), sorted (Tree*), uniqueness (Set), concurrency (Concurrent*).
            - Prefer interfaces in types: List<String> list = new ArrayList<>();
            - Use immutable collections for safety/clarity (List.of, Collections.unmodifiableList).
            - Avoid Arrays.asList for resizable lists (it's fixed-size). Use new ArrayList<>(Arrays.asList(...)) when you need a mutable list.
            - Beware hashCode/equals correctness for objects used as keys in HashMap/values in sets.
            - Prefer List.sort(Comparator) (since Java 8) over Collections.sort for readability; both work.
            - For performance-sensitive code, consider iteration method and memory characteristics (ArrayList is more compact than LinkedList).
            
            If you want, I can:
            - Show a one-page cheat sheet with complexities and usage recommendations.
            - Provide sample exercises with solutions (e.g., implement LRU using LinkedHashMap).
            - Compare specific implementations (ArrayList vs LinkedList) with microbenchmark examples.

            """;

    public String chat(String message) {
        String responseMessage = chatClient
                .prompt()
                .advisors(new SimpleLoggerAdvisor())
                .messages(new SystemMessage(systemPrompt), new AssistantMessage(message), new UserMessage(message))
                .call()
                .content();
        log.info("Chat response: " + responseMessage);
        return responseMessage;
    }


}

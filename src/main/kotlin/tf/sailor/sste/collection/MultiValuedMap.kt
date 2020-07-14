/*
 * This file is part of Sailor STE.
 *
 * Sailor STE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sailor STE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Sailor STE.  If not, see <https://www.gnu.org/licenses/>.
 */

package tf.sailor.sste.collection

/**
 * Interface representing a map that can have one key map to multiple values.
 *
 * This is not a regular [Map] due to incompatibilities with the interfaces. Use [asMap] to turn
 * it into a regular map.
 */
public interface MultiValuedMap<K, V> : Iterable<Map.Entry<K, V>> {
    /** Creates a new regular [Map] for this multi-valued map. */
    public fun asMap(): Map<K, Collection<V>>

    /** Gets all values for the specified key from this map. */
    public operator fun get(key: K): Collection<V>

    /** Gets a single value (usually the first) from the collection at key [key]. */
    public fun getOne(key: K): V? {
        return get(key).firstOrNull()
    }

    /** Checks if this map contains any entries for the specified key. */
    public fun containsKey(key: K): Boolean {
        return get(key).isNotEmpty()
    }

    /**
     * Shortcut for [containsKey].
     */
    public fun contains(key: K): Boolean = containsKey(key)

    /** Gets the collection of keys in this map. */
    public val keys: Sequence<K>

    /** Gets all the values in this map. */
    public val values: Sequence<V>

    /** Gets the entries in this map. */
    public val entries: Sequence<Map.Entry<K, V>>

    /** The size of this map. */
    public val size: Int
}

/**
 * Interface representing a mutable map that can have one key map to multiple values.
 */
public interface MutableMultiValuedMap<K, V> : MultiValuedMap<K, V> {
    /** Creates a new regular [MutableMap] for this multi-valued map. */
    override fun asMap(): MutableMap<K, Collection<V>>

    /** Adds a single value to the specified key. */
    public fun add(key: K, value: V)

    /** Adds all the values at the specified key. */
    public fun addAll(key: K, values: Collection<V>)

    /** Adds all the values at the specified key. */
    public fun addAll(key: K, vararg values: V) {
        addAll(key, values.asList())
    }

    /** Removes all the values at the specified key. */
    public fun removeAll(key: K): Collection<V>

    /** Overwrites the collection at the specified key. */
    public fun overwrite(key: K, value: Collection<V>) {
        removeAll(key)
        addAll(key, value)
    }

    /** Removes all values at the specified key, then overwrites it. */
    public fun overwrite(key: K, value: V) {
        removeAll(key)
        add(key, value)
    }

}

// == manually re-impled extension functions == //
// a lot of these you can get on .entries.

/** Checks if this MultiValuedMap is empty. */
public fun MultiValuedMap<*, *>.isEmpty(): Boolean = size == 0
/** Checks if this MultiValuedMap is not empty. */
public fun MultiValuedMap<*, *>.isNotEmpty(): Boolean = size > 0
/**
 * Gets one value from this map or a default function if there are no entries for the key.
 */
public inline fun <K, V> MultiValuedMap<K, V>.getOneOrElse(key: K, default: () -> V): V {
    val items = get(key)
    return items.firstOrNull() ?: default()
}

// === IMPLS === //

// shared methods...
public abstract class AbstractMultiValuedMap<K, V> : MultiValuedMap<K, V> {
    override fun iterator(): Iterator<Map.Entry<K, V>> {
        return entries.iterator()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MultiValuedMap<*, *>) return false
        if (size != other.size) return false

        // erased check, should be fine
        other as MultiValuedMap<Any, Any>

        for (k in keys) {
            val ourValues = get(k)
            try {
                val theirValues = other[k as Any]
                if (ourValues != theirValues) return false
            } catch (e: ClassCastException) {
                return false
            }
        }

        return true
    }

    // copied from java.util.AbstractMap
    override fun hashCode(): Int {
        var h = 0
        for (entry in entries) h += entry.hashCode()
        return h
    }

    override fun toString(): String {
        val it = entries.iterator()
        if (!it.hasNext()) return "{}"
        val builder = StringBuilder()
        builder.append('{')

        while (true) {
            val entry = it.next()
            val key = entry.key
            val value = entry.value

            builder.append(if (key === this) "(this Map)" else key)
            builder.append('=')
            builder.append(if (value === this) "(this Map)" else value)

            if (!it.hasNext()) {
                builder.append('}')
                break
            }

            builder.append(',').append(' ')
        }

        return builder.toString()
    }
}

internal class HashSetMultiValuedMutableMap<K, V> :
    AbstractMultiValuedMap<K, V>(),
    MutableMultiValuedMap<K, V>
{
    // == multivaluedmap methods/properties == //
    private data class Entry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V>

    private val backingMap = HashMap<K, HashSet<V>>()

    override val keys: Sequence<K>
        get() = backingMap.keys.asSequence()

    override val values: Sequence<V>
        get() = backingMap.values.asSequence().flatten()

    override val entries: Sequence<Map.Entry<K, V>>
        get() = sequence<Map.Entry<K, V>> {
            for ((k, c) in backingMap.entries) {
                for (v in c) {
                    yield(Entry(k, v))
                }
            }
        }

    override val size: Int
        get() = backingMap.values.sumBy { it.size }

    override fun asMap(): MutableMap<K, Collection<V>> {
        return backingMap.toMutableMap()
    }

    override fun get(key: K): Collection<V> {
        return (backingMap[key] ?: emptySet())
    }

    // == mutable overrides == //

    override fun add(key: K, value: V) {
        val set = backingMap.getOrDefault(key, hashSetOf())
        set.add(value)
        backingMap[key] = set
    }

    override fun addAll(key: K, values: Collection<V>) {
        val set = backingMap.getOrDefault(key, hashSetOf())
        set.addAll(values)
        backingMap[key] = set
    }

    override fun removeAll(key: K): Collection<V> {
        return backingMap.remove(key) ?: emptySet()
    }

    // avoids a copy + extra fn call with an override
    override fun addAll(key: K, vararg values: V) {
        val set = backingMap.getOrDefault(key, hashSetOf())
        set.addAll(values)
        backingMap[key] = set
    }

    override fun overwrite(key: K, value: Collection<V>) {
        // optimisation for existing hash sets
        if (value is HashSet) {
            backingMap[key] = value
        }
        // copy to hashset
        backingMap[key] = value.toHashSet()
    }
}

/**
 * Creates a new empty multi-valued map.
 */
public fun <K, V> multiMapOf(): MultiValuedMap<K, V> {
    return HashSetMultiValuedMutableMap()
}

/**
 * Creates a new multi-valued map from the specified pairs.
 */
public fun <K, V> multiMapOf(vararg pairs: Pair<K, V>): MultiValuedMap<K, V> {
    val map = HashSetMultiValuedMutableMap<K, V>()
    for (pair in pairs) {
        map.add(pair.first, pair.second)
    }

    return map
}

/**
 * Creates a new empty mutable multi-valued map.
 */
public fun <K, V> mutableMultiValuedMapOf(): MutableMultiValuedMap<K, V> {
    return HashSetMultiValuedMutableMap()
}

/**
 * Creates a new mutable multi-valued map from the specified pairs.
 */
public fun <K, V> mutableMultiMapOf(vararg pairs: Pair<K, V>): MutableMultiValuedMap<K, V> {
    return multiMapOf(*pairs) as MutableMultiValuedMap<K, V>
}

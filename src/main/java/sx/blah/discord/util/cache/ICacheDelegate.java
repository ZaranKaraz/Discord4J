/*
 *     This file is part of Discord4J.
 *
 *     Discord4J is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Discord4J is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Discord4J.  If not, see <http://www.gnu.org/licenses/>.
 */

package sx.blah.discord.util.cache;

import sx.blah.discord.handle.obj.IIDLinkedObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This represents a cache delegate. These objects are used to back {@link Cache} objects.
 * <b>NOTE:</b> it is expected that implementations handle concurrent access.
 */
public interface ICacheDelegate<T extends IIDLinkedObject> extends RandomAccess, Iterable<T> {

	/**
	 * This is called to retrieve an object from an associated id.
	 *
	 * @param id The id to retrieve the object for.
	 * @return The result of the query.
	 */
	Optional<T> retrieve(String id);

	/**
	 * This is called to retrieve an object from an associated id.
	 *
	 * @param id The id to retrieve the object for.
	 * @return The result of the query.
	 */
	default Optional<T> retrieve(long id) {
		return retrieve(Long.toUnsignedString(id));
	}

	/**
	 * This is called to put an object into the cache.
	 *
	 * @param obj The object to insert.
	 * @return The previous value.
	 */
	Optional<T> put(T obj);

	/**
	 * This is called to place a collection of objects into the cache.
	 *
	 * @param objs The objects to insert.
	 * @return The objects replaced by this operation.
	 */
	default Collection<T> putAll(Collection<T> objs) {
		return objs.stream().map(obj -> put(obj).orElse(null)).filter(Objects::nonNull).collect(Collectors.toSet());
	}

	/**
	 * This is called to remove a key, value pair associated with the specified id.
	 *
	 * @param id The id to remove from the cache.
	 * @return The object removed from this operation.
	 */
	Optional<T> remove(String id);

	/**
	 * This is called to remove a key, value pair associated with the specified id.
	 *
	 * @param id The id to remove from the cache.
	 * @return The object removed from this operation.
	 */
	default Optional<T> remove(long id) {
		return remove(Long.toUnsignedString(id));
	}

	/**
	 * This is called to remove a key, value pair associated with the specified object.
	 *
	 * @param obj The object to remove from the cache.
	 * @return The object removed from this operation.
	 */
	default Optional<T> remove(T obj) {
		return remove(obj.getID());
	}

	/**
	 * This is called to clear the cache.
	 *
	 * @return The objects removed from this operation.
	 */
	Collection<T> clear();

	/**
	 * This is called to check if a value is associated with the provided id.
	 *
	 * @param id The id to check for.
	 * @return True if an object is present or false if otherwise.
	 */
	default boolean contains(String id) {
		return retrieve(id).isPresent();
	}

	/**
	 * This is called to check if a value is associated with the provided id.
	 *
	 * @param id The id to check for.
	 * @return True if an object is present or false if otherwise.
	 */
	default boolean contains(long id) {
		return contains(Long.toUnsignedString(id));
	}

	/**
	 * This is called to check if a value is stored in this cache.
	 *
	 * @param obj The obj to check for.
	 * @return True if an object is present or false if otherwise.
	 */
	default boolean contains(T obj) {
		return contains(obj.getID());
	}

	/**
	 * This is called to get the size of the cache.
	 *
	 * @return The size of the cache.
	 */
	int size();

	/**
	 * This is called to get the ids stored in this cache.
	 *
	 * @return The ids stored.
	 */
	Collection<String> ids();

	/**
	 * This is called to get the ids stored in this cache.
	 *
	 * @return The ids stored.
	 */
	default Collection<Long> longIDs() {
		return ids().stream().map(Long::parseUnsignedLong).collect(Collectors.toSet());
	}

	/**
	 * This is called to get the values stored in this cache.
	 *
	 * @return The values stored.
	 */
	Collection<T> values();

	/**
	 * This is called to (deep) copy this delegate.
	 *
	 * @return The copy of the delegate.
	 */
	ICacheDelegate<T> copy();

	/**
	 * @see Collection#spliterator()
	 */
	default Spliterator<T> spliterator() {
		return Spliterators.spliterator(values(), 0);
	}

	/**
	 * @see Collection#stream()
	 */
	default Stream<T> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	/**
	 * @see Collection#parallelStream()
	 */
	default Stream<T> parallelStream() {
		return StreamSupport.stream(spliterator(), true);
	}
}

/*
 * Cacheonix Systems licenses this file to You under the LGPL 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.cacheonix.org/products/cacheonix/license-lgpl-2.1.htm
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cacheonix.impl.cache.store;

import java.util.Map;

import org.cacheonix.CacheonixTestCase;
import org.cacheonix.impl.cache.datasource.DummyBinaryStoreDataSource;
import org.cacheonix.impl.cache.datastore.DummyDataStore;
import org.cacheonix.impl.cache.distributed.partitioned.BinaryStoreContext;
import org.cacheonix.impl.cache.distributed.partitioned.BinaryStoreContextImpl;
import org.cacheonix.impl.cache.invalidator.DummyCacheInvalidator;
import org.cacheonix.impl.cache.item.Binary;
import org.cacheonix.impl.cache.item.BinaryFactory;
import org.cacheonix.impl.cache.item.BinaryFactoryBuilder;
import org.cacheonix.impl.cache.item.BinaryType;
import org.cacheonix.impl.cache.item.CompressedBinary;
import org.cacheonix.impl.cache.item.InvalidObjectException;
import org.cacheonix.impl.cache.storage.disk.DummyDiskStorage;
import org.cacheonix.impl.cache.storage.disk.StorageException;
import org.cacheonix.impl.cache.util.DummyObjectSizeCalculator;
import org.cacheonix.impl.clock.Time;
import org.cacheonix.impl.clock.TimeImpl;
import org.cacheonix.impl.net.serializer.Serializer;
import org.cacheonix.impl.net.serializer.SerializerFactory;
import org.cacheonix.impl.net.serializer.Wireable;
import org.cacheonix.impl.util.array.HashMap;
import org.cacheonix.impl.util.array.HashSet;

/**
 * Tester for BinaryStore.
 */
public final class BinaryStoreTest extends CacheonixTestCase {

   private static final BinaryFactoryBuilder BINARY_FACTORY_BUILDER = new BinaryFactoryBuilder();

   private static final int MAX_SIZE = 100;

   private static final String DISK_STORAGE_NAME = "test";

   private BinaryStore binaryStore = null;

   private BinaryFactory binaryFactory;


   public void testSerializeDeserialize() throws Exception {

      for (int i = 0; i < MAX_SIZE; i++) {

         binaryStore.put(toBinary(i), toBinary(i));
      }
      assertEquals(binaryStore.size(), MAX_SIZE);

      // Test context
      final BinaryStoreContext context = new BinaryStoreContextImpl();
      context.setObjectSizeCalculator(new DummyObjectSizeCalculator());
      context.setDiskStorage(new DummyDiskStorage(DISK_STORAGE_NAME + "-deserialized"));
      context.setDataSource(new DummyBinaryStoreDataSource());
      context.setInvalidator(new DummyCacheInvalidator());
      context.setDataStore(new DummyDataStore());

      final Serializer ser = SerializerFactory.getInstance().getSerializer(Serializer.TYPE_JAVA);
      final BinaryStore deserializedBinaryStore = (BinaryStore) ser.deserialize(ser.serialize(binaryStore));
      deserializedBinaryStore.setClock(getClock());
      deserializedBinaryStore.setContext(context);
      deserializedBinaryStore.attachToElementCounter(new SharedCounter(MAX_SIZE));
      deserializedBinaryStore.attachToByteCounter(new SharedCounter(0L));

      // Basic checks
      assertEquals(binaryStore.size(), deserializedBinaryStore.size());
      assertEquals(binaryStore.keySet(), deserializedBinaryStore.keySet());

      // Now confirm that the eviction order is the same
      assertEquals(deserializedBinaryStore.size(), MAX_SIZE);

      binaryStore.put(binaryFactory.createBinary(MAX_SIZE), binaryFactory.createBinary(MAX_SIZE));
      deserializedBinaryStore.put(binaryFactory.createBinary(MAX_SIZE), binaryFactory.createBinary(MAX_SIZE));

      assertEquals(MAX_SIZE, deserializedBinaryStore.size());
      assertEquals(binaryStore.size(), deserializedBinaryStore.size());
      assertEquals(binaryStore.keySet(), deserializedBinaryStore.keySet());
      assertEquals(binaryStore.values(), deserializedBinaryStore.values());
   }


   public void testRetainAll() {

      // Prepare test data
      final int keyCount = MAX_SIZE;
      final Map<Binary, Binary> map = new HashMap<Binary, Binary>(keyCount);
      for (int i = 0; i < keyCount; i++) {
         map.put(toBinary(i), toBinary(i));
      }

      // Put to cache
      binaryStore.putAll(map);

      // Prepare key set to retain
      final int keySetCount = keyCount / 11;
      final HashSet<Binary> keySetToRetain = new HashSet<Binary>(keyCount);
      for (int i = 0; i < keySetCount; i++) {
         keySetToRetain.add(toBinary(Integer.valueOf(i)));
      }

      // Remove and assert
      assertTrue("First run should modify", binaryStore.retainAll(keySetToRetain));
      assertTrue("Second run should *not* modify", !binaryStore.retainAll(keySetToRetain));
      assertEquals(keySetToRetain.size(), binaryStore.size());
      assertEquals(keySetToRetain, binaryStore.keySet());
   }


   public void testUpdate() throws Exception {

      final Binary key = binaryFactory.createBinary("key");
      final Binary value = binaryFactory.createBinary("value");
      final Time expirationTime = getClock().currentTime().add(1000L);

      binaryStore.put(key, value, expirationTime);

      final Binary newValue = toBinary("new.value");
      final Time timeToRead = new TimeImpl(10, 0);
      final ReadableElement previousElement = binaryStore.update(key, newValue, timeToRead, 0);
      assertNotNull(previousElement);
   }


   public void testPutGetNullValue() throws InvalidObjectException {

      final Binary key = binaryFactory.createBinary("key");
      final Binary value = new CompressedBinary(null);
      binaryStore.put(key, value);
      assertTrue(binaryStore.containsValue(value));
   }


   public void testAtomicReplaceNullNewValue() throws InvalidObjectException, StorageException {

      // Prepare
      final Binary key = binaryFactory.createBinary("key");
      final Binary value = binaryFactory.createBinary("value");
      final Binary nullValue = binaryFactory.createBinary(null);

      binaryStore.put(key, value);
      final ReadableElement element = binaryStore.get(key);
      assertNotNull(element);
      assertEquals(value, element.getValue());

      // Try to replace key with a null value
      assertTrue(binaryStore.replace(key, value, nullValue));

      // Assert replace happened
      final ReadableElement object = binaryStore.get(key);
      assert object != null;
      assertNull(object.getValue().getValue());
   }


   public void testGetWireableType() {

      assertEquals(Wireable.TYPE_BINARY_STORE, binaryStore.getWireableType());
   }


   public void testToString() {

      assertNotNull(binaryStore.toString());
   }


   public void setUp() throws Exception {

      super.setUp();

      binaryStore = createBinaryStore();
      binaryFactory = BINARY_FACTORY_BUILDER.createFactory(BinaryType.BY_COPY);
   }


   private BinaryStore createBinaryStore() {

      // Test context
      final BinaryStoreContext context = new BinaryStoreContextImpl();
      context.setObjectSizeCalculator(new DummyObjectSizeCalculator());
      context.setDiskStorage(new DummyDiskStorage(DISK_STORAGE_NAME + "-deserialized"));
      context.setDataSource(new DummyBinaryStoreDataSource());
      context.setInvalidator(new DummyCacheInvalidator());
      context.setDataStore(new DummyDataStore());

      final BinaryStore keyStore = new BinaryStore(getClock(), Integer.MAX_VALUE, Integer.MAX_VALUE);
      keyStore.attachToElementCounter(new SharedCounter(MAX_SIZE));
      keyStore.attachToByteCounter(new SharedCounter(0L));
      keyStore.setContext(context);

      return keyStore;
   }
}

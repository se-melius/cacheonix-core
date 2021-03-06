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
package org.cacheonix.impl.cache.distributed.partitioned;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import org.cacheonix.TestUtils;
import org.cacheonix.impl.net.ClusterNodeAddress;
import org.cacheonix.impl.util.logging.Logger;

/**
 * CancelBucketTransferMessageTest
 * <p/>
 *
 * @author <a href="mailto:simeshev@cacheonix.org">Slava Imeshev</a>
 * @since May 24, 2010 9:48:21 PM
 */
public final class CancelBucketTransferMessageTest extends TestCase {

   /**
    * Logger.
    *
    * @noinspection UNUSED_SYMBOL, UnusedDeclaration
    */
   private static final Logger LOG = Logger.getLogger(CancelBucketTransferMessageTest.class); // NOPMD

   private static final ClusterNodeAddress PREVIOUS_OWNER = TestUtils.createTestAddress(2);

   private static final ClusterNodeAddress NEW_OWNER = TestUtils.createTestAddress(1);

   private static final String TEST_CACHE = "test.cache";

   private static final byte SOURCE_STORAGE_NUMBER = 0;

   private static final byte DESTINATION_STORAGE_NUMBER = 1;

   private CancelBucketTransferMessage message;

   private List<Integer> bucketNumbers;


   public void testGetNewOwner() {

      assertEquals(NEW_OWNER, message.getNewOwner());
   }


   public void testGetPreviousOwner() {

      assertEquals(PREVIOUS_OWNER, message.getPreviousOwner());
   }


   public void testGetSourceStorageNumber() {

      assertEquals(SOURCE_STORAGE_NUMBER, message.getSourceStorageNumber());
   }


   public void testGetDestinationStorageNumber() {

      assertEquals(DESTINATION_STORAGE_NUMBER, message.getDestinationStorageNumber());
   }


   public void testGetBucketNumbers() {

      assertEquals(bucketNumbers, message.getBucketNumbers());
   }


   public void testGetCacheName() {

      assertEquals(TEST_CACHE, message.getCacheName());
   }


   /**
    * Tests that no exceptions occur when creating the object using a default constructor.
    */
   public void testDefaultConstructor() {

      assertNotNull(new CancelBucketTransferMessage().toString());
   }


   protected void setUp() throws Exception {

      super.setUp();
      bucketNumbers = new ArrayList<Integer>(1);
      bucketNumbers.add(1);
      message = new CancelBucketTransferMessage(TEST_CACHE);
      message.setPreviousOwner(PREVIOUS_OWNER);
      message.setSourceStorageNumber(SOURCE_STORAGE_NUMBER);
      message.setDestinationStorageNumber(DESTINATION_STORAGE_NUMBER);
      message.setBucketNumbers(bucketNumbers);
      message.setNewOwner(NEW_OWNER);
   }
}

/*
 * Cacheonix systems licenses this file to You under the LGPL 2.1
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
package org.cacheonix.impl.cache.web;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;
import org.cacheonix.impl.net.serializer.Serializer;
import org.cacheonix.impl.net.serializer.SerializerFactory;
import org.cacheonix.impl.net.serializer.Wireable;

import static org.cacheonix.impl.net.serializer.Serializer.TYPE_JAVA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * A tester for {@link IntegerHeader}.
 */
public final class IntegerHeaderTest extends TestCase {


   private static final int VALUE = 123456789;

   private static final String NAME = "name";

   /**
    * Object under test.
    */
   private IntegerHeader header;


   public void testGetValue() {

      assertEquals(VALUE, header.getValue());
   }


   public void testToString() {

      assertNotNull(header.toString());
   }


   public void testAddToResponse() {

      final HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
      header.addToResponse(httpServletResponse);
      verify(httpServletResponse).addIntHeader(NAME, VALUE);
   }


   public void testGetName() {

      assertEquals(NAME, header.getName());
   }


   public void testGetWireableType() {

      assertEquals(Wireable.TYPE_INTEGER_HEADER, header.getWireableType());
   }


   public void testWriteReadWire() throws Exception {

      final Serializer ser = SerializerFactory.getInstance().getSerializer(TYPE_JAVA);
      assertEquals(header, ser.deserialize(ser.serialize(header)));
   }


   public void testEquals() {

      assertEquals(header, new IntegerHeader(NAME, VALUE));
   }


   public void testHashCode() {

      assertEquals(228041706, header.hashCode());
   }


   public void setUp() throws Exception {

      super.setUp();

      header = new IntegerHeader(NAME, VALUE);
   }


   public void tearDown() throws Exception {

      header = null;

      super.tearDown();
   }


   public String toString() {

      return "IntegerHeaderTest{" +
              "integerHeader=" + header +
              "} " + super.toString();
   }
}
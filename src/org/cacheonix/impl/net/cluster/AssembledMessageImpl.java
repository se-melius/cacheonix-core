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
package org.cacheonix.impl.net.cluster;

import org.cacheonix.impl.net.processor.Message;

/**
 * An immutable holder of an assembled message and metadata characterising the assembly process.
 */
@SuppressWarnings("RedundantIfStatement")
final class AssembledMessageImpl implements AssembledMessage {

   private final Message message;

   private final long startFrame;


   AssembledMessageImpl(final Message message, final long startFrame) {

      this.message = message;
      this.startFrame = startFrame;
   }


   public Message getMessage() {

      return message;
   }


   public long getStartFrameNumber() {

      return startFrame;
   }


   public boolean equals(final Object o) {

      if (this == o) {
         return true;
      }
      if (o == null || !o.getClass().equals(getClass())) {
         return false;
      }

      final AssembledMessageImpl that = (AssembledMessageImpl) o;

      if (startFrame != that.startFrame) {
         return false;
      }
      if (message != null ? !message.equals(that.message) : that.message != null) {
         return false;
      }

      return true;
   }


   public int hashCode() {

      int result = message != null ? message.hashCode() : 0;
      result = 31 * result + (int) (startFrame ^ startFrame >>> 32);
      return result;
   }


   public String toString() {

      return "AssembledMessage{" +
              "message=" + message +
              ", startFrame=" + startFrame +
              '}';
   }
}

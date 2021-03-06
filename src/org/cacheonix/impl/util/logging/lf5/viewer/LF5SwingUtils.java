/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cacheonix.impl.util.logging.lf5.viewer;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableModel;

/**
 * Provides methods to accomplish common yet non-trivial tasks with Swing. Obvious implementations of these methods have
 * been tried and failed.
 *
 * @author Richard Wan
 */

// Contributed by ThoughtWorks Inc.

public final class LF5SwingUtils {

   private LF5SwingUtils() {
   }
   //--------------------------------------------------------------------------
   //   Constants:
   //--------------------------------------------------------------------------

   //--------------------------------------------------------------------------
   //   Protected Variables:
   //--------------------------------------------------------------------------

   //--------------------------------------------------------------------------
   //   Private Variables:
   //--------------------------------------------------------------------------

   //--------------------------------------------------------------------------
   //   Constructors:
   //--------------------------------------------------------------------------

   //--------------------------------------------------------------------------
   //   Public Methods:
   //--------------------------------------------------------------------------


   /**
    * Selects a the specified row in the specified JTable and scrolls the specified JScrollable to the newly selected
    * row. More importantly, the call to repaint() delayed long enough to have the table properly paint the newly
    * selected row which may be offscreen.
    *
    * @param table should belong to the specified JScrollPane
    */
   public static void selectRow(final int row, final JTable table, final JScrollPane pane) {
      if (table == null || pane == null) {
         return;
      }
      if (!contains(row, table.getModel())) {
         return;
      }
      moveAdjustable(row * table.getRowHeight(), pane.getVerticalScrollBar());
      selectRow(row, table.getSelectionModel());
      // repaint must be done later because moveAdjustable
      // posts requests to the swing thread which must execute before
      // the repaint logic gets executed.
      repaintLater(table);
   }


   /**
    * Makes the specified Adjustable track if the view area expands and the specified Adjustable is located near the of
    * the view.
    */
   public static void makeScrollBarTrack(final Adjustable scrollBar) {
      if (scrollBar == null) {
         return;
      }
      scrollBar.addAdjustmentListener(new TrackingAdjustmentListener());
   }


   /**
    * Makes the vertical scroll bar of the specified JScrollPane track if the view expands (e.g. if rows are added to an
    * underlying table).
    */
   public static void makeVerticalScrollBarTrack(final JScrollPane pane) {
      if (pane == null) {
         return;
      }
      makeScrollBarTrack(pane.getVerticalScrollBar());
   }


   //--------------------------------------------------------------------------
   //   Protected Methods:
   //--------------------------------------------------------------------------
   private static boolean contains(final int row, final TableModel model) {

      if (model == null) {
         return false;
      }
      return row >= 0 && row < model.getRowCount();
   }


   private static void selectRow(final int row, final ListSelectionModel model) {
      if (model == null) {
         return;
      }
      model.setSelectionInterval(row, row);
   }


   private static void moveAdjustable(final int location, final Adjustable scrollBar) {
      if (scrollBar == null) {
         return;
      }
      scrollBar.setValue(location);
   }


   /**
    * Work around for JTable/viewport bug.
    *
    * @link http://developer.java.sun.com/developer/bugParade/bugs/4205145.html
    */
   private static void repaintLater(final JComponent component) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            component.repaint();
         }
      });
   }
   //--------------------------------------------------------------------------
   //   Private Methods:
   //--------------------------------------------------------------------------

   //--------------------------------------------------------------------------
   //   Nested Top-Level Classes or Interfaces
   //--------------------------------------------------------------------------
}


/*-
 * #%L
 * ImageJ2 plugin for teaching colocalization analysis.
 * %%
 * Copyright (C) 2025 ImageJ2 developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package sc.fiji.coloc;

import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.TextWidget;

/**
 * Wizard step 4: Configure display and visualization options
 */
@Plugin(type = Command.class, name = "Coloc Teacher: Step 4 of 4 - Display Options")
public class DisplayWizard implements Command {

    @Parameter(label = "<html><h3>STEP 4 OF 4: DISPLAY AND VISUALIZATION OPTIONS</h3>" +
               "<p>Visual outputs help you understand and validate your colocalization analysis.</p>" +
               "<p><b>Display options:</b><br>" +
               "• <b>Show intermediate images:</b> Display threshold images, masks, processing steps<br>" +
               "• <b>Generate scatterplot:</b> 2D histogram of pixel intensities</p>" +
               "<p><b>Interpreting scatterplots:</b><br>" +
               "• Diagonal line pattern = positive correlation<br>" +
               "• Horizontal/vertical spread = poor correlation<br>" +
               "• Tight clusters = good correlation in that intensity range</p>" +
               "<p>For teaching purposes, it's recommended to show all visualizations " +
               "to better understand how the algorithms work.</p>" +
               "<p><b>Choose your display preferences:</b></p></html>", 
               style = TextWidget.AREA_STYLE)
    private String info = "";

    @Parameter(label = "Display intermediate images", 
               description = "Show threshold images and processing steps")
    public boolean displayImages = true;

    @Parameter(label = "Generate scatterplot", 
               description = "2D histogram showing pixel intensity correlation")
    public boolean useScatterplot = true;

    @Override
    public void run() {
        // Nothing to do here - parameters are collected by the framework
        // The main plugin will retrieve these values and complete the analysis
    }
}

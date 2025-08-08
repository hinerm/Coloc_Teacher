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

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.TextWidget;

/**
 * Wizard step 4: Configure display and visualization options
 */
@Plugin(type = Command.class, name = "Coloc Teacher: Step 4 - Display Options")
public class DisplayWizard implements Command {

    @Parameter(label = "Educational Information", type = ItemIO.OUTPUT, 
               style = TextWidget.AREA_STYLE, 
               description = "Read-only information about display options")
    private String info = 
        "STEP 4: DISPLAY AND VISUALIZATION OPTIONS\n\n" +
        "Visual outputs help you understand and validate your colocalization analysis.\n\n" +
        "DISPLAY OPTIONS:\n" +
        "• Show intermediate images: Display threshold images, masks, etc.\n" +
        "  - Useful for: Understanding how the analysis works\n" +
        "  - Shows: Binary masks, threshold results, intermediate steps\n\n" +
        "• Generate scatterplot: 2D histogram of pixel intensities\n" +
        "  - Useful for: Visualizing pixel-by-pixel correlation\n" +
        "  - Shows: Channel 1 vs Channel 2 intensity relationship\n" +
        "  - Pattern indicates: Linear correlation, outliers, data quality\n\n" +
        "INTERPRETING SCATTERPLOTS:\n" +
        "• Diagonal line: Perfect positive correlation\n" +
        "• Horizontal/vertical spread: Poor correlation\n" +
        "• Tight cluster: Good correlation in that intensity range\n" +
        "• Outliers: May indicate artifacts or interesting biology\n\n" +
        "For teaching purposes, it's recommended to show all visualizations\n" +
        "to better understand how the algorithms work:";

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

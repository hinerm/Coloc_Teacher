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
import org.scijava.widget.NumberWidget;
import org.scijava.widget.TextWidget;

/**
 * Wizard step 1: Configure synthetic image parameters
 */
@Plugin(type = Command.class, name = "Coloc Teacher: Step 1 - Synthetic Image Setup")
public class SyntheticImageWizard implements Command {

    @Parameter(label = "Educational Information", type = ItemIO.OUTPUT, 
               style = TextWidget.AREA_STYLE, 
               description = "Read-only information about synthetic image generation")
    private String info = 
        "STEP 1: SYNTHETIC IMAGE GENERATION\n\n" +
        "We'll create two synthetic fluorescence images to practice colocalization analysis.\n" +
        "This controlled approach lets you:\n" +
        "• Know the ground truth (actual overlap)\n" +
        "• Test how different parameters affect results\n" +
        "• Learn to interpret colocalization statistics\n\n" +
        "IMAGE PARAMETERS:\n" +
        "• Width/Height: Image dimensions in pixels\n" +
        "• Number of spots: How many fluorescent objects to simulate\n" +
        "• Spot radius: Size of each fluorescent spot\n" +
        "• Overlap fraction: What percentage of spots overlap between channels\n" +
        "  - 0.0 = No overlap (perfect segregation)\n" +
        "  - 0.5 = Half the spots overlap\n" +
        "  - 1.0 = Perfect overlap (complete colocalization)\n\n" +
        "Adjust these parameters to create different colocalization scenarios:";

    @Parameter(label = "Image width (pixels)", min = "64", max = "1024", 
               style = NumberWidget.SPINNER_STYLE, stepSize = "32")
    public int width = 256;

    @Parameter(label = "Image height (pixels)", min = "64", max = "1024", 
               style = NumberWidget.SPINNER_STYLE, stepSize = "32")
    public int height = 256;

    @Parameter(label = "Number of spots", min = "5", max = "200", 
               style = NumberWidget.SPINNER_STYLE, stepSize = "5")
    public int numSpots = 50;

    @Parameter(label = "Spot radius (pixels)", min = "2.0", max = "20.0", 
               style = NumberWidget.SPINNER_STYLE, stepSize = "0.5")
    public double spotRadius = 5.0;

    @Parameter(label = "Overlap fraction (0.0-1.0)", min = "0.0", max = "1.0", 
               style = NumberWidget.SPINNER_STYLE, stepSize = "0.1")
    public double overlapFraction = 0.7;

    @Parameter(label = "Add Gaussian noise", description = "Adds realistic noise to images")
    public boolean addNoise = true;

    @Parameter(label = "Noise standard deviation", min = "0.0", max = "50.0", 
               style = NumberWidget.SPINNER_STYLE, stepSize = "1.0")
    public double noiseStdDev = 10.0;

    @Override
    public void run() {
        // Nothing to do here - parameters are collected by the framework
        // The main plugin will retrieve these values and continue to next step
    }
}

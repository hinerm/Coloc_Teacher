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

import org.scijava.ItemVisibility;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.NumberWidget;
import org.scijava.widget.TextWidget;

/**
 * Wizard step 1: Configure synthetic image parameters
 */
@Plugin(type = Command.class, name = "Coloc Teacher: Step 1 of 4 - Synthetic Image Setup")
public class SyntheticImageWizard extends WizardStep {

    // Test mode parameter - when true, skips dialog and uses default values
    @Parameter(required = false, persist = false, visibility = ItemVisibility.INVISIBLE)
    private boolean testMode = false;

    @Parameter(label = "Educational Information", style = TextWidget.AREA_STYLE, 
               persist = false, required = false, visibility = ItemVisibility.MESSAGE)
    private String educationalContent = "<html><p>We'll create two synthetic fluorescence images to practice colocalization analysis. " +
               "This controlled approach lets you:</p>" +
               "<ul>" +
               "<li>Know the ground truth (actual overlap)</li>" +
               "<li>Test how different parameters affect results</li>" +
               "<li>Learn to interpret colocalization statistics</li>" +
               "</ul>" +
               "<p><b>Key Parameters:</b><br>" +
               "• <b>Overlap fraction:</b> What percentage of spots overlap between channels<br>" +
               "&nbsp;&nbsp;- 0.0 = No overlap (perfect segregation)<br>" +
               "&nbsp;&nbsp;- 0.5 = Half the spots overlap<br>" +
               "&nbsp;&nbsp;- 1.0 = Perfect overlap (complete colocalization)</p>" +
               "<p><b>Configure your synthetic images:</b></p></html>";

    @Parameter(label = " ", style = "separator")
    private String separator2 = "";

    @Override
    public int getStepNumber() { return 1; }

    @Override
    public int getTotalSteps() { return 4; }

    @Override
    public String getStepTitle() { return "Synthetic Image Setup"; }

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
}

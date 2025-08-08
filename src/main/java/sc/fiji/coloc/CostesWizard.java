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
 * Wizard step 2: Configure Costes significance test parameters
 */
@Plugin(type = Command.class, name = "Coloc Teacher: Step 2 - Costes Significance Test")
public class CostesWizard implements Command {

    @Parameter(label = "Educational Information", type = ItemIO.OUTPUT, 
               style = TextWidget.AREA_STYLE, 
               description = "Read-only information about Costes significance testing")
    private String info = 
        "STEP 2: COSTES SIGNIFICANCE TEST\n\n" +
        "The Costes test determines if the colocalization you observe is statistically significant\n" +
        "or could have occurred by chance.\n\n" +
        "HOW IT WORKS:\n" +
        "• Creates randomized versions of your images\n" +
        "• Calculates correlation for each randomized pair\n" +
        "• Compares your real correlation to the random distribution\n" +
        "• Provides a P-value for statistical significance\n\n" +
        "INTERPRETATION:\n" +
        "• P < 0.05: Colocalization is statistically significant\n" +
        "• P > 0.05: Colocalization might be due to chance\n\n" +
        "PARAMETERS:\n" +
        "• Number of randomizations: More = more accurate P-value (but slower)\n" +
        "• PSF (Point Spread Function): Accounts for optical blur\n" +
        "• Display shuffled images: Shows examples of randomized data\n\n" +
        "Recommendations: Use at least 100 randomizations for reliable statistics:";

    @Parameter(label = "Perform Costes significance test", 
               description = "Statistical test for significance of colocalization")
    public boolean useCostes = true;

    @Parameter(label = "Number of randomizations", min = "10", max = "1000", 
               style = NumberWidget.SPINNER_STYLE, stepSize = "10",
               description = "More randomizations = more accurate P-value")
    public int nrCostesRandomisations = 100;

    @Parameter(label = "PSF (Point Spread Function)", min = "0.0", max = "10.0", 
               style = NumberWidget.SPINNER_STYLE, stepSize = "0.1",
               description = "Accounts for optical blur - use 0 if unknown")
    public double psf = 3.0;

    @Parameter(label = "Display shuffled images", 
               description = "Show examples of the randomized images used in test")
    public boolean displayShuffledCostes = false;

    @Override
    public void run() {
        // Nothing to do here - parameters are collected by the framework
        // The main plugin will retrieve these values and continue to next step
    }
}

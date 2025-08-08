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
import org.scijava.widget.NumberWidget;

/**
 * Wizard step 2: Configure Costes significance test parameters
 */
@Plugin(type = Command.class, name = "Coloc Teacher: Step 2 of 4 - Costes Significance Test")
public class CostesWizard extends WizardStep {

    @Override
    public int getStepNumber() { return 2; }

    @Override
    public int getTotalSteps() { return 4; }

    @Override
    public String getStepTitle() { return "Costes Significance Test"; }

    @Override
    public String getEducationalContent() {
        return "<html><h3>STEP 2 OF 4: COSTES SIGNIFICANCE TEST</h3>" +
               "<p>The Costes test determines if the colocalization you observe is statistically significant " +
               "or could have occurred by chance.</p>" +
               "<p><b>How it works:</b><br>" +
               "• Creates randomized versions of your images<br>" +
               "• Calculates correlation for each randomized pair<br>" +
               "• Compares your real correlation to the random distribution<br>" +
               "• Provides a P-value for statistical significance</p>" +
               "<p><b>Interpretation:</b><br>" +
               "• P < 0.05: Colocalization is statistically significant<br>" +
               "• P > 0.05: Colocalization might be due to chance</p>" +
               "<p><b>Choose your parameters:</b></p></html>";
    }

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
}

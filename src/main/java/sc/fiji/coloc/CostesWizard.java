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
import org.scijava.widget.TextWidget;
import org.scijava.ItemVisibility;

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

    @Parameter(label = "Educational Information", style = TextWidget.AREA_STYLE, 
               persist = false, required = false, visibility = ItemVisibility.MESSAGE)
    private String educationalContent = "<html><p>The Costes significance test determines if colocalization is statistically significant " +
               "by comparing your data to randomized versions.</p>" +
               "<p><b>How it works:</b><br>" +
               "1. Scrambles one channel while keeping the other intact<br>" +
               "2. Calculates colocalization on scrambled data<br>" +
               "3. Repeats this many times to build a distribution<br>" +
               "4. Compares your real data to this 'random' distribution</p>" +
               "<p><b>Key Parameters:</b><br>" +
               "• <b>Randomizations:</b> More = better statistics but slower<br>" +
               "• <b>PSF:</b> Point Spread Function width (affects scrambling)<br>" +
               "• <b>Display shuffled:</b> Show examples of randomized data</p>" +
               "<p><b>Configure significance testing:</b></p></html>";

    @Parameter(label = " ", style = "separator", 
               persist = false, required = false, visibility = ItemVisibility.MESSAGE)
    private String separator = "";

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

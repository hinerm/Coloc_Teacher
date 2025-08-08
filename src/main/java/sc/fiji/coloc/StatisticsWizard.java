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
 * Wizard step 3: Choose statistical methods for colocalization analysis
 */
@Plugin(type = Command.class, name = "Coloc Teacher: Step 3 of 4 - Statistical Methods")
public class StatisticsWizard implements Command {

    @Parameter(label = "<html><h3>STEP 3 OF 4: STATISTICAL METHODS</h3>" +
               "<p>Different statistical measures reveal different aspects of colocalization. " +
               "Understanding when to use each method is key to proper analysis.</p>" +
               "<p><b>Correlation-based measures:</b><br>" +
               "• <b>Spearman's rank:</b> Non-parametric correlation, robust to outliers<br>" +
               "• <b>Kendall's tau:</b> Alternative non-parametric correlation for small datasets</p>" +
               "<p><b>Overlap-based measures:</b><br>" +
               "• <b>Manders' coefficients:</b> Fraction of pixels that overlap<br>" +
               "• <b>Li's ICQ:</b> Intensity correlation quotient vs. random distribution</p>" +
               "<p><b>Choose the methods appropriate for your research:</b></p></html>", 
               style = TextWidget.AREA_STYLE)
    private String info = "";

    @Parameter(label = "Use Li's ICQ (Intensity Correlation Quotient)", 
               description = "Compares intensity correlation to random distribution")
    public boolean useLiICQ = true;

    @Parameter(label = "Use Spearman's rank correlation", 
               description = "Non-parametric correlation, robust to outliers")
    public boolean useSpearmanRank = true;

    @Parameter(label = "Use Manders' coefficients", 
               description = "Fraction of each channel that overlaps with the other")
    public boolean useManders = true;

    @Parameter(label = "Use Kendall's tau", 
               description = "Alternative non-parametric correlation coefficient")
    public boolean useKendallTau = true;

    @Override
    public void run() {
        // Nothing to do here - parameters are collected by the framework
        // The main plugin will retrieve these values and continue to next step
    }
}

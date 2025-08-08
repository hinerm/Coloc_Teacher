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
 * Wizard step 3: Choose statistical methods for colocalization analysis
 */
@Plugin(type = Command.class, name = "Coloc Teacher: Step 3 - Statistical Methods")
public class StatisticsWizard implements Command {

    @Parameter(label = "Educational Information", type = ItemIO.OUTPUT, 
               style = TextWidget.AREA_STYLE, 
               description = "Read-only information about colocalization statistics")
    private String info = 
        "STEP 3: STATISTICAL METHODS\n\n" +
        "Different statistical measures reveal different aspects of colocalization.\n" +
        "Understanding when to use each method is key to proper analysis.\n\n" +
        "CORRELATION-BASED MEASURES:\n" +
        "• Pearson's R: Linear correlation (-1 to +1)\n" +
        "  - Best for: Linear relationships, normally distributed data\n" +
        "  - Sensitive to: Outliers, non-linear relationships\n\n" +
        "• Spearman's rank correlation: Non-parametric correlation\n" +
        "  - Best for: Non-linear relationships, any distribution\n" +
        "  - Robust to: Outliers, non-normal data\n\n" +
        "• Kendall's tau: Alternative non-parametric correlation\n" +
        "  - Best for: Small datasets, tied values\n" +
        "  - More robust than Spearman for small samples\n\n" +
        "OVERLAP-BASED MEASURES:\n" +
        "• Manders' coefficients: Fraction of pixels that overlap\n" +
        "  - Best for: Quantifying actual spatial overlap\n" +
        "  - Independent of: Correlation, intensity relationships\n\n" +
        "• Li's ICQ: Intensity correlation quotient\n" +
        "  - Best for: Comparing to random distribution\n" +
        "  - Values: -0.5 (segregation) to +0.5 (colocalization)\n\n" +
        "Choose the methods appropriate for your research question:";

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

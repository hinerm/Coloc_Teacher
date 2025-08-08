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
import org.scijava.widget.TextWidget;

/**
 * Welcome wizard step: Introduction and choice between synthetic or user images
 */
@Plugin(type = Command.class, name = "Coloc Teacher: Welcome")
public class WelcomeWizard extends WizardStep {

    @Parameter(label = "Welcome to Coloc Teacher!", style = TextWidget.AREA_STYLE, 
               persist = false, required = false, visibility = ItemVisibility.MESSAGE)
    private String welcomeMessage = "<html><h2>Welcome to Coloc Teacher!</h2>" +
               "<p>This wizard will guide you through the parameters and concepts of colocalization analysis " +
               "using the Coloc2 plugin. Colocalization analysis helps you understand how much two different " +
               "fluorescent signals overlap in your images.</p>" +
               "<br>" +
               "<p><b>What you'll learn:</b></p>" +
               "<ul>" +
               "<li>How to interpret colocalization statistics (Pearson's, Manders', etc.)</li>" +
               "<li>When to use Costes significance testing</li>" +
               "<li>How different parameters affect your results</li>" +
               "<li>How to avoid common pitfalls in colocalization analysis</li>" +
               "</ul>" +
               "<br>" +
               "<p><b>Choose your approach:</b></p>" +
               "<p>• <b>Synthetic images:</b> We'll create controlled test images where you know the ground truth</p>" +
               "<p>• <b>Your own images:</b> Use your existing two-channel fluorescence images. You <b>must</b> have at least two images open before clicking OK.</p>" +
               "</html>";

    @Parameter(label = " ", style = "separator", 
               persist = false, required = false, visibility = ItemVisibility.MESSAGE)
    private String separator = "";

    @Parameter(label = "Use synthetic images for demonstration?", 
               description = "If checked, we'll create synthetic test images. If unchecked, you'll select your own images.")
    public boolean useSyntheticImages = true;

    @Override
    public String getStepTitle() {
        return "Welcome to Coloc Teacher";
    }
}

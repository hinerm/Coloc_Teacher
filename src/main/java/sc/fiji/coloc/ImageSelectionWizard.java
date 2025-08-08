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

import ij.ImagePlus;

import org.scijava.ItemVisibility;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.TextWidget;

/**
 * Image selection wizard step: Choose two images for colocalization analysis
 */
@Plugin(type = Command.class, name = "Coloc Teacher: Select Images")
public class ImageSelectionWizard extends WizardStep {

    @Parameter(label = "Image Selection Instructions", style = TextWidget.AREA_STYLE, 
               persist = false, required = false, visibility = ItemVisibility.MESSAGE)
    private String instructionMessage = "<html><p>Select the two images you want to analyze for colocalization. " +
               "These should be two-channel fluorescence images or separate single-channel images.</p>" +
               "<br>" +
               "<p><b>Requirements:</b></p>" +
               "<ul>" +
               "<li>Images should be the same size (width × height)</li>" +
               "<li>Images should represent the same field of view</li>" +
               "<li>Ideally, images should be properly registered (aligned)</li>" +
               "<li>Background should be subtracted if necessary</li>" +
               "</ul>" +
               "<br>" +
               "<p><b>Tips:</b></p>" +
               "<ul>" +
               "<li>Channel 1 is often called the 'reference' channel</li>" +
               "<li>Channel 2 is the channel you're testing for colocalization with Channel 1</li>" +
               "<li>Make sure both images are open in ImageJ before running this wizard</li>" +
               "</ul>" +
               "</html>";

    @Parameter(label = " ", style = "separator", 
               persist = false, required = false, visibility = ItemVisibility.MESSAGE)
    private String separator = "";

    @Parameter(label = "Channel 1 (Reference)", 
               description = "Select the first channel/image for colocalization analysis")
    public ImagePlus channel1Image;

    @Parameter(label = "Channel 2 (Test)", 
               description = "Select the second channel/image for colocalization analysis")
    public ImagePlus channel2Image;

    @Override
    public String getStepTitle() {
        return "Select Images for Analysis";
    }
}

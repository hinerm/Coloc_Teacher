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
import org.scijava.ui.UIService;
import org.scijava.ItemVisibility;
import org.scijava.widget.TextWidget;

/**
 * Abstract base class for wizard steps in the Coloc Teacher plugin.
 * Provides common functionality for step progression, cancellation, and educational content display.
 */
public abstract class WizardStep implements Command {

    /**
     * HACK: Workaround because when we try to set this programmatically
     * through the CommandService, it is "satisfied" and doesn't appear in the
     * wizard dialog.
     */
    public static String PROGRESS_MESSAGE;

    @Parameter
    private UIService uiService;
    
    @Parameter(label = "Wizard Progress", style = TextWidget.AREA_STYLE, 
               persist = false, required = false, visibility = ItemVisibility.MESSAGE)
    private String wizardProgress = PROGRESS_MESSAGE;

    // Cancellation flag - will be set by dialog result
    private boolean cancelled = false;

    /**
     * Callback method for cancel button
     */
    protected void cancelWizard() {
        cancelled = true;
    }

    /**
     * Set cancellation status
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Check if the wizard step was cancelled
     * @return true if cancelled, false otherwise
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Get the step title (without step numbering)
     * @return step title
     */
    public abstract String getStepTitle();

    @Override
    public void run() {
        // Subclasses can override this for additional functionality
    }
}

package edu.mit.scansite.client.ui.event;

import com.google.gwt.event.shared.EventHandler;

import edu.mit.scansite.shared.dispatch.features.OrthologScanSequencePatternResult;

/**
 * @author Konstantin Krismer
 */
public interface OrthologScanSequencePatternEventHandler extends EventHandler {
  void onOrthologScanSequencePatternEvent(OrthologScanSequencePatternResult result);
}

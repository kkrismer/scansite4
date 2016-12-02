package edu.mit.scansite.shared.dispatch.features;

import java.util.List;

import net.customware.gwt.dispatch.shared.Result;
import edu.mit.scansite.shared.transferobjects.IdentifierType;

/**
 * @author Konstantin Krismer
 */
public class IdentifierTypeRetrieverResult implements Result {

  private List<IdentifierType> types;

  public IdentifierTypeRetrieverResult() {

  }

  public IdentifierTypeRetrieverResult(List<IdentifierType> types) {
    this.types = types;
  }

  public List<IdentifierType> getTypes() {
    return types;
  }

  public void setTypes(List<IdentifierType> types) {
    this.types = types;
  }
}

package com.hamzajg.accounting.bank.model.journal;

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;

import com.hamzajg.accounting.bank.model.*;

import java.util.Set;

/**
 * See
 * <a href="https://docs.vlingo.io/vlingo-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class JournalLinesChanged extends IdentifiedDomainEvent {

  public final String id;
  public final Set<JournalLine> journalLines;

  public JournalLinesChanged(final JournalState state) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.id = state.id;
    this.journalLines = state.journalLines;
  }

  @Override
  public String identity() {
    return id;
  }
}

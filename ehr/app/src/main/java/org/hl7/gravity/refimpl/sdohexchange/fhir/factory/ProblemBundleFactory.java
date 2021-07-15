package org.hl7.gravity.refimpl.sdohexchange.fhir.factory;

import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;

public class ProblemBundleFactory extends ConditionBundleFactory {

  private DateTimeType startDate;

  @Override
  protected Condition createCondition() {
    Condition condition = super.createCondition();
    if (startDate != null) {
      condition.setOnset(startDate);
    }
    return condition;
  }

  public void setStartDate(LocalDate startDate) {
    if (startDate != null) {
      this.startDate = new DateTimeType(Date.from(startDate.atStartOfDay(ZoneId.systemDefault())
          .toInstant()));
    } else {
      this.startDate = null;
    }
  }
}

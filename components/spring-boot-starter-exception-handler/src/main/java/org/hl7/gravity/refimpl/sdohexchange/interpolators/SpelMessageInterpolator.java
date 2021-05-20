package org.hl7.gravity.refimpl.sdohexchange.interpolators;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionException;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

/**
 * Message interpolator that uses the Spring Expression Language (SpEL) to evaluate expressions inside a template
 * message.
 *
 * SpEL expressions are delimited by {@code #{} and {@code }}. The provided variables are accessible directly by name.
 */
@Slf4j
public class SpelMessageInterpolator {

  private final EvaluationContext evalContext;
  private final SpelExpressionParser spelExpressionParser;

  /**
   * Creates a new instance with {@link StandardEvaluationContext} including {@link
   * org.springframework.expression.spel.support.ReflectivePropertyAccessor ReflectivePropertyAccessor} and {@link
   * MapAccessor}.
   */
  public SpelMessageInterpolator() {
    StandardEvaluationContext ctx = new StandardEvaluationContext();
    ctx.addPropertyAccessor(new MapAccessor());
    this.evalContext = ctx;
    this.spelExpressionParser = new SpelExpressionParser();
  }

  /**
   * Creates a new instance with a custom {@link EvaluationContext}.
   */
  public SpelMessageInterpolator(EvaluationContext evalContext) {
    Assert.notNull(evalContext, "EvaluationContext must not be null");
    this.evalContext = evalContext;
    this.spelExpressionParser = new SpelExpressionParser();
  }

  /**
   * Interpolates message template with parameters values. If it cannot be parsed, exception will be caught and logged.
   *
   * @param messageTemplate
   *     current message template
   * @param variables
   *     map of variables
   * @return interpolated value
   */
  public String interpolate(String messageTemplate, Map<String, Object> variables) {
    Assert.notNull(messageTemplate, "messageTemplate must not be null");
    try {
      Expression expression = spelExpressionParser.parseExpression(messageTemplate, new TemplateParserContext());
      return expression.getValue(evalContext, variables, String.class);
    } catch (ExpressionException ex) {
      log.error(String.format("Failed to interpolate message template: '%s'", messageTemplate), ex);
    }
    return messageTemplate;
  }
}
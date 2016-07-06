/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.expression;

import java.util.Map;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Shared utility class used to evaluate and cache SpEL expressions that
 * are defined on {@link java.lang.reflect.AnnotatedElement}.
 *
 * @author Stephane Nicoll
 * @since 4.2
 * @see AnnotatedElementKey
 */
public abstract class CachedExpressionEvaluator {

	private final SpelExpressionParser parser;


	/**
	 * Create a new instance with the specified {@link SpelExpressionParser}.
	 */
	protected CachedExpressionEvaluator(SpelExpressionParser parser) {
		Assert.notNull(parser, "Parser must not be null");
		this.parser = parser;
	}

	/**
	 * Create a new instance with a default {@link SpelExpressionParser}.
	 */
	protected CachedExpressionEvaluator() {
		this(new SpelExpressionParser());
	}


	/**
	 * Return the {@link SpelExpressionParser} to use.
	 */
	protected SpelExpressionParser getParser() {
		return this.parser;
	}


	/**
	 * Return the {@link Expression} for the specified SpEL value
	 * <p>Parse the expression if it hasn't been already.
	 * @param cache the cache to use
	 * @param elementKey the element on which the expression is defined
	 * @param expression the expression to parse
	 */
	protected Expression getExpression(Map<ExpressionKey, Expression> cache,
			AnnotatedElementKey elementKey, String expression) {

		ExpressionKey expressionKey = createKey(elementKey, expression);
		Expression expr = cache.get(expressionKey);
		if (expr == null) {
			expr = getParser().parseExpression(expression);
			cache.put(expressionKey, expr);
		}
		return expr;
	}

	private ExpressionKey createKey(AnnotatedElementKey elementKey, String expression) {
		return new ExpressionKey(elementKey, expression);
	}


	protected static class ExpressionKey {

		private final AnnotatedElementKey key;

		private final String expression;

		protected ExpressionKey(AnnotatedElementKey key, String expression) {
			this.key = key;
			this.expression = expression;
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof ExpressionKey)) {
				return false;
			}
			ExpressionKey otherKey = (ExpressionKey) other;
			return (this.key.equals(otherKey.key) &&
					ObjectUtils.nullSafeEquals(this.expression, otherKey.expression));
		}

		@Override
		public int hashCode() {
			return this.key.hashCode() + (this.expression != null ? this.expression.hashCode() * 29 : 0);
		}
	}

}
/*
 * Copyright 2016 the original author or authors.
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

package org.springframework.integration.dsl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import org.aopalliance.aop.Advice;

import org.springframework.expression.Expression;
import org.springframework.integration.expression.FunctionExpression;
import org.springframework.integration.handler.DelayHandler;
import org.springframework.integration.store.MessageGroupStore;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

/**
 * A {@link ConsumerEndpointSpec} for a {@link DelayHandler}.
 *
 * @author Artem Bilan
 *
 * @since 5.0
 */
public final class DelayerEndpointSpec extends ConsumerEndpointSpec<DelayerEndpointSpec, DelayHandler> {

	private final List<Advice> delayedAdvice = new LinkedList<Advice>();

	DelayerEndpointSpec(DelayHandler delayHandler) {
		super(delayHandler);
		Assert.notNull(delayHandler, "'delayHandler' must not be null.");
		this.handler.setDelayedAdviceChain(this.delayedAdvice);
	}

	/**
	 * @param defaultDelay the defaultDelay.
	 * @return the endpoint spec.
	 * @see DelayHandler#setDefaultDelay(long)
	 */
	public DelayerEndpointSpec defaultDelay(long defaultDelay) {
		this.handler.setDefaultDelay(defaultDelay);
		return _this();
	}

	/**
	 * @param ignoreExpressionFailures the ignoreExpressionFailures.
	 * @return the endpoint spec.
	 * @see DelayHandler#setIgnoreExpressionFailures(boolean)
	 */
	public DelayerEndpointSpec ignoreExpressionFailures(boolean ignoreExpressionFailures) {
		this.handler.setIgnoreExpressionFailures(ignoreExpressionFailures);
		return _this();
	}

	/**
	 * @param messageStore the message store.
	 * @return the endpoint spec.
	 */
	public DelayerEndpointSpec messageStore(MessageGroupStore messageStore) {
		this.handler.setMessageStore(messageStore);
		return _this();
	}

	/**
	 * Configure a list of {@link Advice} objects that will be applied, in nested order,
	 * when delayed messages are sent.
	 * @param advice the advice chain.
	 * @return the endpoint spec.
	 */
	public DelayerEndpointSpec delayedAdvice(Advice... advice) {
		this.delayedAdvice.addAll(Arrays.asList(advice));
		return _this();
	}

	public DelayerEndpointSpec delayExpression(Expression delayExpression) {
		this.handler.setDelayExpression(delayExpression);
		return this;
	}

	public DelayerEndpointSpec delayExpression(String delayExpression) {
		this.handler.setDelayExpression(PARSER.parseExpression(delayExpression));
		return this;
	}

	/**
	 * Specify the function to determine delay value against {@link Message}.
	 * Typically used with a Java 8 Lambda expression:
	 * <pre class="code">
	 * {@code
	 *  .<Foo>delay("delayer", m -> m.getPayload().getDate(),
	 *            c -> c.advice(this.delayedAdvice).messageStore(this.messageStore()))
	 * }
	 * </pre>
	 * @param delayFunction the {@link Function} to determine delay.
	 * @param <P> the payload type.
	 * @return the endpoint spec.
	 */
	public <P> DelayerEndpointSpec delayFunction(Function<Message<P>, Object> delayFunction) {
		this.handler.setDelayExpression(new FunctionExpression<>(delayFunction));
		return this;
	}

}

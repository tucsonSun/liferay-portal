/**
 * Copyright (c) 2000-2008 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portal.kernel.messaging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="GlobalDestinationEventListener.java.html"><b><i>View
 * Source</i></b></a>
 *
 * A listener that will register a specified listener to all destinations
 * registered into the message bus
 *
 * @author Michael C. Han
 */
public class GlobalDestinationEventListener
	implements DestinationEventListener {

	/**
	 *
	 * @param listener to receive all messages
	 * @param ignoredDestinations where listener where not receive events
	 */
	public GlobalDestinationEventListener(MessageListener listener,
										  List<String> ignoredDestinations) {
		_listener = listener;
		_ignoredDestinations = new HashMap<String, String>();
		for (String destination : ignoredDestinations) {
			_ignoredDestinations.put(destination, destination);
		}
	}

	public void destinationAdded(Destination dest) {
		if (!_ignoredDestinations.containsKey(dest.getName())) {
			dest.register(_listener);
		}
	}

	public void destinationRemoved(Destination dest) {
		if (!_ignoredDestinations.containsKey(dest.getName())) {
			dest.unregister(_listener);
		}
	}

	private MessageListener _listener;
	private Map<String, String> _ignoredDestinations;
}

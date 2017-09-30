/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License");  you may not use this file except in compliance with the License.  
 *
 * You may obtain a copy of the License at
 *   
 *       http://www.apache.org/licenses/LICENSE-2.0
 *    
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See the NOTICE file distributed with this work for 
 * additional information regarding copyright ownership. 
 *******************************************************************************/
package org.oulipo.browser.editor;

import org.oulipo.net.IRI;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.net.TumblerField;
import org.oulipo.resources.model.Link;

public final class LinkFactory {

	public static Link bold(TumblerAddress homeDocument) {
		return style(homeDocument, TumblerAddress.BOLD, TumblerField.BOLD);
	}

	public static Link fontFamilySerif(TumblerAddress homeDocument) {
		return style(homeDocument, TumblerAddress.FONT_FAMILY_SERIF, TumblerField.FONT_FAMILY_SERIF);
	}

	public static Link italic(TumblerAddress homeDocument) {
		return style(homeDocument, TumblerAddress.ITALIC, TumblerField.ITALIC);
	}

	public static Link strikeThrough(TumblerAddress homeDocument) {
		return style(homeDocument, TumblerAddress.STRIKE_THROUGH, TumblerField.STRIKE_THROUGH);
	}

	private static Link style(TumblerAddress homeDocument, TumblerAddress styleAddress, TumblerField field) {
		Link link = new Link();
		link.linkTypes.add(styleAddress);
		link.sequence = field.get(1);
		try {
			link.resourceId = new IRI(homeDocument.toExternalForm() + ".0." + field.asStringNoException());
		} catch (MalformedTumblerException e1) {
			e1.printStackTrace();
		}
		return link;

	}

	public static Link underline(TumblerAddress homeDocument) {
		return style(homeDocument, TumblerAddress.UNDERLINE, TumblerField.UNDERLINE);
	}

}

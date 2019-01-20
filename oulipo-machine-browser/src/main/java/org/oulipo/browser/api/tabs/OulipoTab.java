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
package org.oulipo.browser.api.tabs;

import com.google.common.base.Strings;
import de.endrullis.draggabletabs.DraggableTab;
import javafx.scene.image.ImageView;
import org.oulipo.security.session.CodeGenerator;

/**
 * A tab that includes tumbler and site specific information useful for
 * displaying to the user.
 *
 * This class should be extended by extension providers to provide additional
 * information needed for display,
 *
 */
public class OulipoTab extends DraggableTab {

	private String description;

	private String id;

	/**
	 * Image displayed in the tab
	 */
	private ImageView image;

	private String tumblerAddress;

	public OulipoTab(String title) {
		super(title);
		id = CodeGenerator.generateCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OulipoTab other = (OulipoTab) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getDescription() {
		return description;
	}

	public String getTumblerAddress() {
		return tumblerAddress;
	}

	public boolean hasAddress() {
		return !Strings.isNullOrEmpty(tumblerAddress);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setImage(String url) {
		this.image = new ImageView(url);
		setGraphic(image);
	}

	public void setTumblerAddress(String tumblerAddress) {
		this.tumblerAddress = tumblerAddress;
	}

}

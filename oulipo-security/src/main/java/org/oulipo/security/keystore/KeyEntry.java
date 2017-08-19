/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License.  
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
package org.oulipo.security.keystore;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.io.BaseEncoding;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class KeyEntry {

	private String alias;

	private String kty;

	private String crv;

	private String pk;

	private String iv;
	
	private KeyEntry() { }
	
	private KeyEntry(String alias, String kty, String crv, String pk, String iv) {
		this.alias = alias;
		this.kty = kty;
		this.crv = crv;
		this.pk = pk;
		this.iv = iv;
	}

	public static class Builder {
		
		private String alias;

		private String kty;

		private String crv;

		private String pk;

		private String iv;
		
		public KeyEntry build() {
			return new KeyEntry(alias, kty, crv, pk, iv);
		}

		public Builder alias(String alias) {
			this.alias = alias;
			return this;
		}

		public Builder kty(String kty) {
			this.kty = kty;
			return this;
		}

		public Builder crv(String crv) {
			this.crv = crv;
			return this;
		}

		public Builder pk(byte[] pk) {
			this.pk = BaseEncoding.base64Url().encode(pk);
			return this;
		}

		public Builder iv(byte[] iv) {
			this.iv = BaseEncoding.base64Url().encode(iv);
			return this;
		}
	}

	public String getAlias() {
		return alias;
	}

	public String getKty() {
		return kty;
	}

	public String getCrv() {
		return crv;
	}

	public String getPk() {
		return pk;
	}

	public String getIv() {
		return iv;
	}

}

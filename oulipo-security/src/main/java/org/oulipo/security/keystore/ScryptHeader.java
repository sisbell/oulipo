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
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ScryptHeader {

	private long n;

	private int p;

	private int r;

	private String salt;

	private ScryptHeader() {
	}

	private ScryptHeader(long n, int p, int r, String salt) {
		this.n = n;
		this.p = p;
		this.r = r;
		this.salt = salt;
	}

	public static class Builder {

		private Long n;

		private Integer p;

		private Integer r;

		private String salt;

		public Builder n(long n) {
			this.n = n;
			return this;
		}

		public Builder p(int p) {
			this.p = p;
			return this;
		}

		public Builder r(int r) {
			this.r = r;
			return this;
		}

		public Builder salt(byte[] salt) {
			this.salt = BaseEncoding.base64Url().encode(salt);
			return this;
		}

		public ScryptHeader build() {
			if (Strings.isNullOrEmpty(salt)) {
				throw new IllegalArgumentException("Salt is emtpy or null");
			}
			if (r == null) {
				throw new IllegalArgumentException("r is null");
			}

			if (p == null) {
				throw new IllegalArgumentException("p is null");
			}

			if (n == null) {
				throw new IllegalArgumentException("n is null");
			}
			return new ScryptHeader(n, p, r, salt);
		}
	}

	public long getN() {
		return n;
	}

	public int getP() {
		return p;
	}

	public int getR() {
		return r;
	}

	public String getSalt() {
		return salt;
	}
}

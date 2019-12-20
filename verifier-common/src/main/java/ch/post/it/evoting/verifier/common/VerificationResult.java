/**
 * This file is part of Verifier Swiss Post.
 * <p>
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.common;

import java.util.Map;

public class VerificationResult {
    private VerificationDefinition verificationDefinition;
    private Status status;
    private Map<Language, String> message;
    private VerificationResultSeverity severity;

    public VerificationResult() {
    }

    public VerificationResult(VerificationDefinition definition) {
        this.verificationDefinition = definition;
    }

    public VerificationDefinition getVerificationDefinition() {
        return verificationDefinition;
    }

    public void setVerificationDefinition(VerificationDefinition verificationDefinition) {
        this.verificationDefinition = verificationDefinition;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Map<Language, String> getMessage() {
        return message;
    }

    public void setMessage(Map<Language, String> message) {
        this.message = message;
    }

}

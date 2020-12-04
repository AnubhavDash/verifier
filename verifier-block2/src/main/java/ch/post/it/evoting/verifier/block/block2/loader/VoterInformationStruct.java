/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block2.loader;

public class VoterInformationStruct {
    private long count = 0;
    private String eeid = null;

    public long getCount() {
        return count;
    }

    public void increment() {
        count++;
    }

    public String getEeid() {
        return eeid;
    }

    public void setAndCheckUniqueEeid(String eeid) {
        if (this.eeid == null) {
            this.eeid = eeid;
        } else if (!this.eeid.equals(eeid)) {
            throw new RuntimeException("eeid not unique between all voterInformation.csv files");
        }
    }
}

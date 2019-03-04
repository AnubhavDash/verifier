/**
 * This file is part of Verifier Swiss Post.
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.  If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.report.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Test {
    private String testIdLabel;
    private String testNameLabel;
    private String testCategoryLabel;
    private String testDescriptionLabel;
    private String testStatusLabel;
    private String testMessageLabel;

    private String id;
    private String name;
    private String category;
    private String description;
    private String status;
    private String message;

}
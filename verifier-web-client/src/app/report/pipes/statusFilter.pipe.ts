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
import {Pipe, PipeTransform} from '@angular/core';

@Pipe({name: 'statusFilter'})
export class StatusFilterPipe implements PipeTransform {
  /**
   * Transform
   *
   * @param {any[]} items
   * @param {string} searchText
   * @returns {any[]}
   */
  transform(raw: any, statusFilter: string): any {
    if (!statusFilter || statusFilter === '') {
      return raw;
    }

    return Object.keys(raw)
      .filter(key => {
        if (statusFilter.indexOf('|') > -1) {
          const filters = statusFilter.split('|');
          return raw[key].status === filters[0] || raw[key].status === filters[1];
        } else {
          return raw[key].status === statusFilter;
        }
      })
      .reduce((obj, key) => {
        return {
          ...obj,
          [key]: raw[key]
        };
      }, {});
  }
}

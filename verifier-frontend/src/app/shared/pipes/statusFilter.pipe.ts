/*
 * (c) Copyright 2025 Swiss Post Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'statusFilter',
  standalone: true
})
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
      return [];
    }

    return Object.keys(raw)
      .filter(key => {
        if (statusFilter.indexOf('|') > -1) {
          const filters = statusFilter.split('|');
          return filters.some((element) => raw[key].status === element);
        } else {
          return raw[key].status === statusFilter;
        }
      })
      .reduce((obj, key) => ({
          ...obj,
          [key]: raw[key]
        }), {});
  }
}

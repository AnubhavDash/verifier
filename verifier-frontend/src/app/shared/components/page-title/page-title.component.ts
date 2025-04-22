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
import {AfterViewInit, Component, DestroyRef, HostBinding, inject, Input} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TranslateModule} from '@ngx-translate/core';
import {distinctUntilChanged, fromEvent, map} from "rxjs";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";

@Component({
	selector: 'ver-page-title',
	standalone: true,
	imports: [CommonModule, TranslateModule],
	templateUrl: './page-title.component.html',
})
export class PageTitleComponent implements AfterViewInit {
	@Input({required: true}) title!: string;
	@Input({required: true}) instructions!: string;

	@HostBinding('class.sticky') isSticky = false;

	destroyRef = inject(DestroyRef);

	ngAfterViewInit() {
		const main = document.querySelector('main');

		if (!main) return;

		fromEvent(main, 'scroll')
			.pipe(
				map(() => main.scrollTop !== 0),
				distinctUntilChanged(),
				takeUntilDestroyed(this.destroyRef)
			)
			.subscribe(isParentScrolled => {
				this.isSticky = isParentScrolled;
			});
	}
}

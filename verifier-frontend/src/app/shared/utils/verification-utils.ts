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
import {VerificationDefinition} from "../types/verification-definition";
import {VerificationStatus} from "../types/verification-status";

export function isDefault(key: string, verifications: Record<string, VerificationDefinition>): boolean {
  return verifications[key].status === VerificationStatus.IDLE;
}

export function isRunning(key: string, verifications: Record<string, VerificationDefinition>): boolean {
  return verifications[key].status === VerificationStatus.RUNNING;
}

export function isOK(key: string, verifications: Record<string, VerificationDefinition>): boolean {
  return verifications[key].status === VerificationStatus.OK;
}

export function isNotOK(key: string, verifications: Record<string, VerificationDefinition>): boolean {
  return verifications[key].status === VerificationStatus.NOK;
}

export function isError(key: string, verifications: Record<string, VerificationDefinition>): boolean {
  return verifications[key].status === VerificationStatus.ERROR;
}

export function showErrorStack(key: string, verifications: Record<string, VerificationDefinition>): boolean {
  return verifications[key].errorStack !== null && !this.isReportDisplay;
}

export function errorStack(key: string, verifications: Record<string, VerificationDefinition>): string[] {
  return verifications[key].errorStack || [];
}

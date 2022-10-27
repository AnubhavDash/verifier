/*
 * Copyright 2022 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.protocol.domain;

import java.util.List;

/**
 * List of symbols (with their UTF-8 codepoint) included in the write-in alphabet.
 */
public final class WriteInAlphabet {

	public static final List<String> WRITE_IN_ALPHABET = List.of(

			Character.toString(0x0023), // # (U+0023) -> 000
			Character.toString(0x0020), //   (U+0020) -> 001
			Character.toString(0x0027), // ' (U+0027) -> 002
			Character.toString(0x0028), // ( (U+0028) -> 003
			Character.toString(0x0029), // ) (U+0029) -> 004
			Character.toString(0x002C), // , (U+002C) -> 005
			Character.toString(0x002D), // - (U+002D) -> 006
			Character.toString(0x002E), // . (U+002E) -> 007
			Character.toString(0x002F), // / (U+002F) -> 008
			Character.toString(0x0030), // 0 (U+0030) -> 009

			Character.toString(0x0031), // 1 (U+0031) -> 010
			Character.toString(0x0032), // 2 (U+0032) -> 011
			Character.toString(0x0033), // 3 (U+0033) -> 012
			Character.toString(0x0034), // 4 (U+0034) -> 013
			Character.toString(0x0035), // 5 (U+0035) -> 014
			Character.toString(0x0036), // 6 (U+0036) -> 015
			Character.toString(0x0037), // 7 (U+0037) -> 016
			Character.toString(0x0038), // 8 (U+0038) -> 017
			Character.toString(0x0039), // 9 (U+0039) -> 018
			Character.toString(0x0041), // A (U+0041) -> 019

			Character.toString(0x0042), // B (U+0042) -> 020
			Character.toString(0x0043), // C (U+0043) -> 021
			Character.toString(0x0044), // D (U+0044) -> 022
			Character.toString(0x0045), // E (U+0045) -> 023
			Character.toString(0x0046), // F (U+0046) -> 024
			Character.toString(0x0047), // G (U+0047) -> 025
			Character.toString(0x0048), // H (U+0048) -> 026
			Character.toString(0x0049), // I (U+0049) -> 027
			Character.toString(0x004A), // J (U+004A) -> 028
			Character.toString(0x004B), // K (U+004B) -> 029

			Character.toString(0x004C), // L (U+004C) -> 030
			Character.toString(0x004D), // M (U+004D) -> 031
			Character.toString(0x004E), // N (U+004E) -> 032
			Character.toString(0x004F), // O (U+004F) -> 033
			Character.toString(0x0050), // P (U+0050) -> 034
			Character.toString(0x0051), // Q (U+0051) -> 035
			Character.toString(0x0052), // R (U+0052) -> 036
			Character.toString(0x0053), // S (U+0053) -> 037
			Character.toString(0x0054), // T (U+0054) -> 038
			Character.toString(0x0055), // U (U+0055) -> 039

			Character.toString(0x0056), // V (U+0056) -> 040
			Character.toString(0x0057), // W (U+0057) -> 041
			Character.toString(0x0058), // X (U+0058) -> 042
			Character.toString(0x0059), // Y (U+0059) -> 043
			Character.toString(0x005A), // Z (U+005A) -> 044
			Character.toString(0x0061), // a (U+0061) -> 045
			Character.toString(0x0062), // b (U+0062) -> 046
			Character.toString(0x0063), // c (U+0063) -> 047
			Character.toString(0x0064), // d (U+0064) -> 048
			Character.toString(0x0065), // e (U+0065) -> 049

			Character.toString(0x0066), // f (U+0066) -> 050
			Character.toString(0x0067), // g (U+0067) -> 051
			Character.toString(0x0068), // h (U+0068) -> 052
			Character.toString(0x0069), // i (U+0069) -> 053
			Character.toString(0x006A), // j (U+006A) -> 054
			Character.toString(0x006B), // k (U+006B) -> 055
			Character.toString(0x006C), // l (U+006C) -> 056
			Character.toString(0x006D), // m (U+006D) -> 057
			Character.toString(0x006E), // n (U+006E) -> 058
			Character.toString(0x006F), // o (U+006F) -> 059

			Character.toString(0x0070), // p (U+0070) -> 060
			Character.toString(0x0071), // q (U+0071) -> 061
			Character.toString(0x0072), // r (U+0072) -> 062
			Character.toString(0x0073), // s (U+0073) -> 063
			Character.toString(0x0074), // t (U+0074) -> 064
			Character.toString(0x0075), // u (U+0075) -> 065
			Character.toString(0x0076), // v (U+0076) -> 066
			Character.toString(0x0077), // w (U+0077) -> 067
			Character.toString(0x0078), // x (U+0078) -> 068
			Character.toString(0x0079), // y (U+0079) -> 069

			Character.toString(0x007A), // z (U+007A) -> 070
			Character.toString(0x00A0), //   (U+00A0) -> 071
			Character.toString(0x00A2), // ¢ (U+00A2) -> 072
			Character.toString(0x0160), // Š (U+0160) -> 073
			Character.toString(0x0161), // š (U+0161) -> 074
			Character.toString(0x017D), // Ž (U+017D) -> 075
			Character.toString(0x017E), // ž (U+017E) -> 076
			Character.toString(0x0152), // Œ (U+0152) -> 077
			Character.toString(0x0153), // œ (U+0153) -> 078
			Character.toString(0x0178), // Ÿ (U+0178) -> 079

			Character.toString(0x00C0), // À (U+00C0) -> 080
			Character.toString(0x00C1), // Á (U+00C1) -> 081
			Character.toString(0x00C2), // Â (U+00C2) -> 082
			Character.toString(0x00C3), // Ã (U+00C3) -> 083
			Character.toString(0x00C4), // Ä (U+00C4) -> 084
			Character.toString(0x00C5), // Å (U+00C5) -> 085
			Character.toString(0x00C6), // Æ (U+00C6) -> 086
			Character.toString(0x00C7), // Ç (U+00C7) -> 087
			Character.toString(0x00C8), // È (U+00C8) -> 088
			Character.toString(0x00C9), // É (U+00C9) -> 089

			Character.toString(0x00CA), // Ê (U+00CA) -> 090
			Character.toString(0x00CB), // Ë (U+00CB) -> 091
			Character.toString(0x00CC), // Ì (U+00CC) -> 092
			Character.toString(0x00CD), // Í (U+00CD) -> 093
			Character.toString(0x00CE), // Î (U+00CE) -> 094
			Character.toString(0x00CF), // Ï (U+00CF) -> 095
			Character.toString(0x00D0), // Ð (U+00D0) -> 096
			Character.toString(0x00D1), // Ñ (U+00D1) -> 097
			Character.toString(0x00D2), // Ò (U+00D2) -> 098
			Character.toString(0x00D3), // Ó (U+00D3) -> 099

			Character.toString(0x00D4), // Ô (U+00D4) -> 100
			Character.toString(0x00D5), // Õ (U+00D5) -> 101
			Character.toString(0x00D6), // Ö (U+00D6) -> 102
			Character.toString(0x00D8), // Ø (U+00D8) -> 103
			Character.toString(0x00D9), // Ù (U+00D9) -> 104
			Character.toString(0x00DA), // Ú (U+00DA) -> 105
			Character.toString(0x00DB), // Û (U+00DB) -> 106
			Character.toString(0x00DC), // Ü (U+00DC) -> 107
			Character.toString(0x00DD), // Ý (U+00DD) -> 108
			Character.toString(0x00DE), // Þ (U+00DE) -> 109

			Character.toString(0x00DF), // ß (U+00DF) -> 110
			Character.toString(0x00E0), // à (U+00E0) -> 111
			Character.toString(0x00E1), // á (U+00E1) -> 112
			Character.toString(0x00E2), // â (U+00E2) -> 113
			Character.toString(0x00E3), // ã (U+00E3) -> 114
			Character.toString(0x00E4), // ä (U+00E4) -> 115
			Character.toString(0x00E5), // å (U+00E5) -> 116
			Character.toString(0x00E6), // æ (U+00E6) -> 117
			Character.toString(0x00E7), // ç (U+00E7) -> 118
			Character.toString(0x00E8), // è (U+00E8) -> 119

			Character.toString(0x00E9), // é (U+00E9) -> 120
			Character.toString(0x00EA), // ê (U+00EA) -> 121
			Character.toString(0x00EB), // ë (U+00EB) -> 122
			Character.toString(0x00EC), // ì (U+00EC) -> 123
			Character.toString(0x00ED), // í (U+00ED) -> 124
			Character.toString(0x00EE), // î (U+00EE) -> 125
			Character.toString(0x00EF), // ï (U+00EF) -> 126
			Character.toString(0x00F0), // ð (U+00F0) -> 127
			Character.toString(0x00F1), // ñ (U+00F1) -> 128
			Character.toString(0x00F2), // ò (U+00F2) -> 129

			Character.toString(0x00F3), // ó (U+00F3) -> 130
			Character.toString(0x00F4), // ô (U+00F4) -> 131
			Character.toString(0x00F5), // õ (U+00F5) -> 132
			Character.toString(0x00F6), // ö (U+00F6) -> 133
			Character.toString(0x00F8), // ø (U+00F8) -> 134
			Character.toString(0x00F9), // ù (U+00F9) -> 135
			Character.toString(0x00FA), // ú (U+00FA) -> 136
			Character.toString(0x00FB), // û (U+00FB) -> 137
			Character.toString(0x00FC), // ü (U+00FC) -> 138
			Character.toString(0x00FD), // ý (U+00FD) -> 139

			Character.toString(0x00FE), // þ (U+00FE) -> 140
			Character.toString(0x00FF)  // ÿ (U+00FF) -> 141)
	);
	private WriteInAlphabet() {
		// Empty constructor.
	}
}
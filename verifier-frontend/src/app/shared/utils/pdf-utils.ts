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
import {jsPDF} from "jspdf";
import autoTable from "jspdf-autotable";
import {formatDate} from "@angular/common";
import {PDFConfig} from "../types/pdf-config";

export const FONT_SIZES = {
  TITLE: 20,
  SECTION: 16,
  SUBSECTION: 14,
  NORMAL: 12,
  SMALL: 11,
  TINY: 9
};

// 0.40625 factor for line height
export const LINE_HEIGHTS = {
  TITLE: 8.125,
  SECTION: 6.5,
  SUBSECTION: 5.6875,
  NORMAL: 4.875,
  SMALL: 4.46875,
  TINY: 3.65625
};

export const COLORS = {
  BOX_FILL: '#f8f9fa',
  BOX_LINE_COLOR: [222, 226, 230],
  SECTION_FILL: '#c7c9c9',
  HEADER_FILL: '#ffffff',
  TEXT_COLOR: 0,
  MUTED_TEXT: '#212529',
  LINE_COLOR: [230, 230, 230],
  ALTERNATE_ROW: [242, 242, 242],
  WHITE: [255, 255, 255],
  SUCCESS_FILL: [163, 207, 187],
  SUCCESS_TEXT: [25, 135, 84],
  WARNING_FILL: [255, 249, 230],
  WARNING_TEXT: [255, 193, 7],
  ERROR_FILL: [241, 174, 181],
  ERROR_TEXT: [88, 21, 28]
};

export const MARGINS = {
  LEFT: 10,
  RIGHT: 10,
  TOP: 15,
  BOTTOM: 15,
  FOOTER: {
    BOTTOM: 7
  },
  TABLE: {
    BOTTOM: 10
  },
  INDENTATION: 5
};

export class PDFUtils {

  static initializePDF(title: string): PDFConfig {
    const doc = new jsPDF();
    const pageWidth = doc.internal.pageSize.getWidth();

    doc.setFontSize(FONT_SIZES.TITLE);
    doc.text(title, pageWidth / 2, 20, {align: 'center'});

    return {
      doc,
      pageWidth,
      x: MARGINS.LEFT,
      y: 30 // initial y position
    };
  }

  static checkPageBreak(config: PDFConfig, heightNeeded: number = 10): void {
    if (config.y + heightNeeded > config.doc.internal.pageSize.getHeight() - MARGINS.BOTTOM) {
      config.doc.addPage();
      config.y = MARGINS.TOP;
    }
  }

  static createSectionHeader(config: PDFConfig, text: string): void {
    text = this.removeMarkdownFormatting(text);
    // Rectangle height is calculated based on the number of lines in the text
    config.doc.setFontSize(FONT_SIZES.SECTION);
    const textLines = config.doc.splitTextToSize(text, PDFUtils.getMaxAvailableTextWidth(config));
    const rectHeight = LINE_HEIGHTS.SECTION + textLines.length * LINE_HEIGHTS.SECTION;

    config.doc.setFillColor(COLORS.SECTION_FILL);
    config.doc.rect(0, config.y, config.doc.internal.pageSize.getWidth(), rectHeight, 'F');
    config.doc.text(textLines, config.x, config.y + LINE_HEIGHTS.SECTION + 2.4); // 2.4 is a small offset for better alignment

    // Update y position for next element
    config.y += rectHeight + 5;
  }

  static createSubSectionHeader(config: PDFConfig, text: string): void {
    text = this.removeMarkdownFormatting(text);
    config.y += LINE_HEIGHTS.SUBSECTION;
    config.doc.setFontSize(FONT_SIZES.SUBSECTION);
    const textLines = config.doc.splitTextToSize(text, PDFUtils.getMaxAvailableTextWidth(config));
    PDFUtils.checkPageBreak(config, textLines.length * LINE_HEIGHTS.SUBSECTION + 5);

    config.doc.text(textLines, config.x, config.y);
    config.y += (textLines.length - 1) * LINE_HEIGHTS.SUBSECTION;
    PDFUtils.addSectionSeparatorLine(config);
  }

  static createTextSection(config: PDFConfig, text: string): void {
    text = this.removeMarkdownFormatting(text);
    config.y += LINE_HEIGHTS.NORMAL;
    config.doc.setFontSize(FONT_SIZES.NORMAL);
    const textLines = config.doc.splitTextToSize(text, PDFUtils.getMaxAvailableTextWidth(config));
    PDFUtils.checkPageBreak(config, textLines.length * LINE_HEIGHTS.NORMAL);

    config.doc.text(textLines, config.x, config.y);
    config.y += (textLines.length - 1) * LINE_HEIGHTS.NORMAL + 2;
  }

  static createSmallTextSection(config: PDFConfig, text: string): void {
    text = this.removeMarkdownFormatting(text);
    const smallTextLines = config.doc.splitTextToSize(text, PDFUtils.getMaxAvailableTextWidth(config) - MARGINS.INDENTATION);
    PDFUtils.checkPageBreak(config, smallTextLines.length * LINE_HEIGHTS.SMALL);

    config.y += LINE_HEIGHTS.SMALL + 2;
    config.doc.setFontSize(FONT_SIZES.SMALL);
    config.doc.text(smallTextLines, config.x + MARGINS.INDENTATION, config.y);
    config.y += (smallTextLines.length - 1) * LINE_HEIGHTS.SMALL;
  }

  static removeMarkdownFormatting(text: string): string {
    return text
      .replace(/\*\*(.*?)\*\*/g, '$1')  // Remove bold (**text**)
      .replace(/\*(.*?)\*/g, '$1');     // Remove italic (*text*)
  }

  static createRoundedBox(config: PDFConfig, text: string, value: string, relativeX: number, alignRight?: boolean): void {
    config.doc.setFontSize(FONT_SIZES.NORMAL);
    const textWidth = config.doc.getTextWidth(text);
    const valueWidth = config.doc.getTextWidth(value);
    const padding = 5;
    const margin = 1;
    const rectWidth = Math.max(textWidth, valueWidth) + padding * 2;
    const rectHeight = 15;

    config.doc.setFillColor(COLORS.BOX_FILL);
    config.doc.setDrawColor(COLORS.BOX_LINE_COLOR[0], COLORS.BOX_LINE_COLOR[1], COLORS.BOX_LINE_COLOR[2]);
    config.doc.setLineWidth(0.1);

    if (alignRight) {
      relativeX = config.pageWidth - rectWidth - relativeX;
    }
    let relativeY = config.y;
    relativeY += margin; // Top margin
    config.doc.roundedRect(relativeX, relativeY, rectWidth, rectHeight, 2, 2, 'FD');
    config.doc.text(value, relativeX + (rectWidth / 2), relativeY + 6, {align: 'center'});
    config.doc.text(text, relativeX + (rectWidth / 2), relativeY + 12, {align: 'center'});
    relativeY += rectHeight + margin; // Bottom margin

    // Update y position for next element
    config.y = relativeY;
  }

  static createTable(config: PDFConfig, headers: any[], data: any[][], options: any = {}): void {
    const defaultOptions = {
      head: [headers],
      body: data,
      startY: config.y,
      margin: {
        left: config.x,
        right: MARGINS.RIGHT
      },
      headStyles: {
        fillColor: COLORS.HEADER_FILL,
        textColor: COLORS.TEXT_COLOR,
        fontStyle: 'bold',
        lineWidth: {
          top: 0, right: 0, bottom: 0.5, left: 0
        },
        lineColor: 0
      },
      bodyStyles: {
        fillColor: COLORS.WHITE,
        textColor: COLORS.TEXT_COLOR,
      },
      alternateRowStyles: {
        fillColor: COLORS.ALTERNATE_ROW
      },
      styles: {
        fontSize: 10,
        cellPadding: 2,
      },
      ...options
    };

    autoTable(config.doc, defaultOptions);
    config.y = (config.doc as any).lastAutoTable.finalY;
  }

  static addSeparatorLine(config: PDFConfig): void {
    config.doc.setDrawColor(COLORS.BOX_LINE_COLOR[0], COLORS.BOX_LINE_COLOR[1], COLORS.BOX_LINE_COLOR[2]);
    config.doc.setLineWidth(0.2);
    config.doc.line(
      config.x,
      config.y + LINE_HEIGHTS.TINY,
      config.doc.internal.pageSize.getWidth() - MARGINS.RIGHT,
      config.y + LINE_HEIGHTS.TINY
    );
    config.y += LINE_HEIGHTS.SUBSECTION;
  }

  static addSectionSeparatorLine(config: PDFConfig): void {
    config.doc.setDrawColor(COLORS.TEXT_COLOR);
    config.doc.setLineWidth(0.2);
    config.doc.line(
      config.x,
      config.y + LINE_HEIGHTS.SUBSECTION / 2,
      config.doc.internal.pageSize.getWidth() - MARGINS.RIGHT,
      config.y + LINE_HEIGHTS.SUBSECTION / 2
    );
    config.y += LINE_HEIGHTS.SUBSECTION;
  }

  static generateFilename(prefix: string): string {
    const formattedDate = formatDate(new Date(), 'yyyyMMdd_HHmm', 'en');
    return `${prefix}-${formattedDate}`;
  }

  static getMaxAvailableTextWidth(config: PDFConfig): number {
    return config.doc.internal.pageSize.getWidth() - MARGINS.LEFT - MARGINS.RIGHT;
  }
}

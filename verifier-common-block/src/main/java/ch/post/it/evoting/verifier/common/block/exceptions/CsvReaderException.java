package ch.post.it.evoting.verifier.common.block.exceptions;

/**
 * Exception signaling an error occurred while reading a CSV file.
 */
public class CsvReaderException extends RuntimeException {
	public CsvReaderException(Throwable cause) {
		super(cause);
	}
}

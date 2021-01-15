package ch.post.it.evoting.verifier.common.block.tools.path;

public enum RelationType {

	SIGN,
	P7,
	METADATA;

	public String toFileExtension() {
		return "." + name().toLowerCase();
	}

}

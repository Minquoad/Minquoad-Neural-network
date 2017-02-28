package utilities;

public enum ApplicationMode {

	NONE(false),
	WILL_LEARN(false),
	LEARNING(true),
	WILL_PROCEED(false),
	PROCESSING(true);

	private boolean impliesOccupation = false;

	private ApplicationMode(boolean impliesOccupation) {
		this.impliesOccupation = impliesOccupation;
	}

	public boolean impliesOccupation() {
		return impliesOccupation;
	}

}
